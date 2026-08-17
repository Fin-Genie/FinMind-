package com.example.ui.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.local.AppDatabase
import com.example.data.repository.AdvisorRepository
import com.example.domain.math.DcfInputs
import com.example.domain.math.DcfValuationResult
import com.example.domain.math.ValuationEngine
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

data class ValuationUiState(
    val inputs: DcfInputs = DcfInputs(),
    val result: DcfValuationResult = ValuationEngine.calculateDcf(DcfInputs()),
    val saveSuccessMessage: String? = null
)

class ValuationViewModel(application: Application) : AndroidViewModel(application) {

    private val db = AppDatabase.getDatabase(application)
    private val repository = AdvisorRepository(db.chatMessageDao(), db.analysisDao())

    private val _uiState = MutableStateFlow(ValuationUiState())
    val uiState: StateFlow<ValuationUiState> = _uiState.asStateFlow()

    fun updateCompanyName(name: String) {
        val newInputs = _uiState.value.inputs.copy(companyName = name)
        recompute(newInputs)
    }

    fun updateCurrentSharePrice(price: Double) {
        val newInputs = _uiState.value.inputs.copy(currentSharePrice = price.coerceAtLeast(0.1))
        recompute(newInputs)
    }

    fun updateGrowthStage1(growthPct: Double) {
        val newInputs = _uiState.value.inputs.copy(fcfGrowthYears1to5Pct = growthPct.coerceIn(-20.0, 80.0))
        recompute(newInputs)
    }

    fun updateGrowthStage2(growthPct: Double) {
        val newInputs = _uiState.value.inputs.copy(fcfGrowthYears6to10Pct = growthPct.coerceIn(-10.0, 40.0))
        recompute(newInputs)
    }

    fun updateBeta(beta: Double) {
        val newInputs = _uiState.value.inputs.copy(beta = beta.coerceIn(0.2, 3.5))
        recompute(newInputs)
    }

    fun updatePerpetualGrowth(gPct: Double) {
        val newInputs = _uiState.value.inputs.copy(perpetualGrowthRatePct = gPct.coerceIn(0.5, 5.0))
        recompute(newInputs)
    }

    fun updateInitialFcf(fcfMillions: Double) {
        val newInputs = _uiState.value.inputs.copy(initialFreeCashFlowMillions = fcfMillions.coerceAtLeast(10.0))
        recompute(newInputs)
    }

    private fun recompute(inputs: DcfInputs) {
        val res = ValuationEngine.calculateDcf(inputs)
        _uiState.value = _uiState.value.copy(inputs = inputs, result = res)
    }

    fun saveToDossier() {
        val res = _uiState.value.result
        val inp = _uiState.value.inputs
        viewModelScope.launch {
            val summary = "DCF Valuation for ${inp.companyName}: Intrinsic Fair Value $${String.format("%.2f", res.intrinsicValuePerShare)} vs Market Spot $${String.format("%.2f", res.currentSharePrice)} (Margin of Safety: ${String.format("%.1f", res.marginOfSafetyPct)}%)."
            val metrics = """
                {"intrinsic": ${res.intrinsicValuePerShare}, "spot": ${res.currentSharePrice}, "wacc": ${res.calculatedWaccPct}, "margin": ${res.marginOfSafetyPct}}
            """.trimIndent()
            val insight = "CladRb Valuation Verdict: At WACC ${String.format("%.2f", res.calculatedWaccPct)}%, the asset exhibits ${if (res.marginOfSafetyPct > 0) "positive economic asymmetry (Undervalued)" else "premium valuation overreach"}. Terminal Value constitutes ${String.format("%.1f", (res.pvTerminalValueMillions / res.enterpriseValueMillions) * 100)}% of Enterprise Value."

            repository.saveAnalysis(
                title = "DCF Valuation Dossier: ${inp.companyName}",
                category = "DCF",
                summary = summary,
                detailedMetricsJson = metrics,
                cladrbInsight = insight
            )
            _uiState.value = _uiState.value.copy(saveSuccessMessage = "Valuation model preserved in Dossier!")
        }
    }

    fun clearSaveMessage() {
        _uiState.value = _uiState.value.copy(saveSuccessMessage = null)
    }
}
