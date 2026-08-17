package com.example.ui.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.local.AppDatabase
import com.example.data.repository.AdvisorRepository
import com.example.domain.math.OptionGreeks
import com.example.domain.math.OptionInputs
import com.example.domain.math.RiskGreeksEngine
import com.example.domain.math.VaRAnalysisResult
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

data class RiskGreeksUiState(
    val selectedSubTab: Int = 0, // 0 = VaR / Expected Shortfall, 1 = Black-Scholes Greeks
    val portfolioValue: Double = 1000000.0,
    val annualizedVolPct: Double = 18.0,
    val confidencePct: Double = 99.0,
    val horizonDays: Int = 10,
    val varResult: VaRAnalysisResult = RiskGreeksEngine.computeRiskMetrics(1000000.0, 0.18, 99.0, 10),
    val optionInputs: OptionInputs = OptionInputs(),
    val optionGreeks: OptionGreeks = RiskGreeksEngine.calculateOptionGreeks(OptionInputs()),
    val isCallSelected: Boolean = true,
    val saveSuccessMessage: String? = null
)

class RiskGreeksViewModel(application: Application) : AndroidViewModel(application) {

    private val db = AppDatabase.getDatabase(application)
    private val repository = AdvisorRepository(db.chatMessageDao(), db.analysisDao())

    private val _uiState = MutableStateFlow(RiskGreeksUiState())
    val uiState: StateFlow<RiskGreeksUiState> = _uiState.asStateFlow()

    fun setSelectedSubTab(tab: Int) {
        _uiState.value = _uiState.value.copy(selectedSubTab = tab)
    }

    fun updatePortfolioValue(value: Double) {
        _uiState.value = _uiState.value.copy(portfolioValue = value.coerceAtLeast(1000.0))
        recomputeVaR()
    }

    fun updateAnnualizedVol(volPct: Double) {
        _uiState.value = _uiState.value.copy(annualizedVolPct = volPct.coerceIn(1.0, 120.0))
        recomputeVaR()
    }

    fun updateConfidenceLevel(confPct: Double) {
        _uiState.value = _uiState.value.copy(confidencePct = confPct)
        recomputeVaR()
    }

    fun updateHorizonDays(days: Int) {
        _uiState.value = _uiState.value.copy(horizonDays = days.coerceIn(1, 252))
        recomputeVaR()
    }

    private fun recomputeVaR() {
        val s = _uiState.value
        val res = RiskGreeksEngine.computeRiskMetrics(
            portfolioValue = s.portfolioValue,
            annualizedVol = s.annualizedVolPct / 100.0,
            confidencePct = s.confidencePct,
            horizonDays = s.horizonDays
        )
        _uiState.value = _uiState.value.copy(varResult = res)
    }

    // Option Greeks
    fun updateOptionSpot(spot: Double) {
        val newInp = _uiState.value.optionInputs.copy(underlyingPriceS = spot.coerceAtLeast(1.0))
        recomputeGreeks(newInp)
    }

    fun updateOptionStrike(strike: Double) {
        val newInp = _uiState.value.optionInputs.copy(strikePriceK = strike.coerceAtLeast(1.0))
        recomputeGreeks(newInp)
    }

    fun updateOptionDaysToExpiry(days: Double) {
        val newInp = _uiState.value.optionInputs.copy(daysToExpirationT = days.coerceIn(1.0, 730.0))
        recomputeGreeks(newInp)
    }

    fun updateOptionImpliedVol(ivPct: Double) {
        val newInp = _uiState.value.optionInputs.copy(impliedVolatilitySigma = (ivPct / 100.0).coerceIn(0.01, 3.0))
        recomputeGreeks(newInp)
    }

    fun toggleCallPut(isCall: Boolean) {
        _uiState.value = _uiState.value.copy(isCallSelected = isCall)
    }

    private fun recomputeGreeks(inputs: OptionInputs) {
        val greeks = RiskGreeksEngine.calculateOptionGreeks(inputs)
        _uiState.value = _uiState.value.copy(optionInputs = inputs, optionGreeks = greeks)
    }

    fun saveToDossier() {
        viewModelScope.launch {
            val v = _uiState.value.varResult
            val g = _uiState.value.optionGreeks
            val isOption = _uiState.value.selectedSubTab == 1

            val title = if (isOption) "Black-Scholes Derivatives & Greeks Report" else "Fat-Tail VaR & Stress Test Dossier"
            val summary = if (isOption) {
                "Options Greeks: Spot $${_uiState.value.optionInputs.underlyingPriceS}, Strike $${_uiState.value.optionInputs.strikePriceK}, Call Δ: ${String.format("%.3f", g.callDelta)}, Vega: ${String.format("%.3f", g.vega)}."
            } else {
                "${v.timeHorizonDays}-Day ${String.format("%.0f", v.confidenceLevelPct)}% Student-t VaR: $${String.format("%,.0f", v.parametricVaRDollars)} (${String.format("%.2f", v.parametricVaRPct)}%). Expected Shortfall (CVaR): $${String.format("%,.0f", v.expectedShortfallCVaRDollars)}."
            }
            val metrics = if (isOption) {
                """{"callPrice": ${g.callPrice}, "delta": ${g.callDelta}, "gamma": ${g.gamma}, "vega": ${g.vega}}"""
            } else {
                """{"varDollars": ${v.parametricVaRDollars}, "cvarDollars": ${v.expectedShortfallCVaRDollars}, "loss2008": ${v.stressTestLoss2008Crisis}}"""
            }
            val insight = if (isOption) {
                "CladRb Derivatives Insight: High Gamma (${String.format("%.4f", g.gamma)}) accelerates directional risk as expiration approaches. Delta-neutral rebalancing frequency must increase."
            } else {
                "CladRb Risk Advice: Leptokurtic tail adjustment expands potential losses by ${String.format("%.1f", (v.fatTailAdjustmentMultiplier - 1.0) * 100)}% over standard Gaussian models. Maintain liquidity buffer above Expected Shortfall ($${String.format("%,.0f", v.expectedShortfallCVaRDollars)})."
            }

            repository.saveAnalysis(
                title = title,
                category = "RISK_VAR",
                summary = summary,
                detailedMetricsJson = metrics,
                cladrbInsight = insight
            )
            _uiState.value = _uiState.value.copy(saveSuccessMessage = "Risk profile safely stored in Dossier!")
        }
    }

    fun clearSaveMessage() {
        _uiState.value = _uiState.value.copy(saveSuccessMessage = null)
    }
}
