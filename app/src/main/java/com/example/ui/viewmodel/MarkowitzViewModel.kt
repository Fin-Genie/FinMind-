package com.example.ui.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.local.AppDatabase
import com.example.data.repository.AdvisorRepository
import com.example.domain.math.Asset
import com.example.domain.math.MarkowitzResult
import com.example.domain.math.PortfolioOptimizer
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

data class MarkowitzUiState(
    val assets: List<Asset> = PortfolioOptimizer.generateDefaultAssets(),
    val riskFreeRatePct: Double = 4.2,
    val result: MarkowitzResult? = null,
    val isOptimizing: Boolean = false,
    val selectedPortfolioMode: String = "CURRENT", // "CURRENT", "MAX_SHARPE", "MIN_VOL"
    val saveSuccessMessage: String? = null
)

class MarkowitzViewModel(application: Application) : AndroidViewModel(application) {

    private val db = AppDatabase.getDatabase(application)
    private val repository = AdvisorRepository(db.chatMessageDao(), db.analysisDao())

    private val _uiState = MutableStateFlow(MarkowitzUiState())
    val uiState: StateFlow<MarkowitzUiState> = _uiState.asStateFlow()

    init {
        runOptimization()
    }

    fun updateAssetWeight(assetId: String, newWeight: Double) {
        val updated = _uiState.value.assets.map {
            if (it.id == assetId) it.copy(weight = newWeight.coerceIn(0.0, 1.0)) else it
        }
        _uiState.value = _uiState.value.copy(assets = updated)
        runOptimization()
    }

    fun updateAssetExpectedReturn(assetId: String, newReturnPct: Double) {
        val updated = _uiState.value.assets.map {
            if (it.id == assetId) it.copy(expectedReturn = (newReturnPct / 100.0).coerceIn(0.001, 1.0)) else it
        }
        _uiState.value = _uiState.value.copy(assets = updated)
        runOptimization()
    }

    fun updateAssetVolatility(assetId: String, newVolPct: Double) {
        val updated = _uiState.value.assets.map {
            if (it.id == assetId) it.copy(volatility = (newVolPct / 100.0).coerceIn(0.01, 1.5)) else it
        }
        _uiState.value = _uiState.value.copy(assets = updated)
        runOptimization()
    }

    fun updateRiskFreeRate(ratePct: Double) {
        _uiState.value = _uiState.value.copy(riskFreeRatePct = ratePct.coerceIn(0.0, 20.0))
        runOptimization()
    }

    fun addCustomAsset(ticker: String, name: String, expectedReturnPct: Double, volatilityPct: Double) {
        val newAsset = Asset(
            id = System.currentTimeMillis().toString(),
            ticker = ticker.uppercase().trim(),
            name = name.trim(),
            expectedReturn = expectedReturnPct / 100.0,
            volatility = volatilityPct / 100.0,
            weight = 0.20
        )
        val updated = _uiState.value.assets + newAsset
        _uiState.value = _uiState.value.copy(assets = updated)
        runOptimization()
    }

    fun removeAsset(assetId: String) {
        if (_uiState.value.assets.size <= 2) return // Need at least 2 assets for covariance
        val updated = _uiState.value.assets.filterNot { it.id == assetId }
        _uiState.value = _uiState.value.copy(assets = updated)
        runOptimization()
    }

    fun applyOptimalWeights(useMaxSharpe: Boolean) {
        val res = _uiState.value.result ?: return
        val optimalWeights = if (useMaxSharpe) res.maxSharpePortfolio.weights else res.minVariancePortfolio.weights
        val updated = _uiState.value.assets.mapIndexed { idx, asset ->
            asset.copy(weight = optimalWeights.getOrElse(idx) { 0.25 })
        }
        _uiState.value = _uiState.value.copy(
            assets = updated,
            selectedPortfolioMode = if (useMaxSharpe) "MAX_SHARPE" else "MIN_VOL"
        )
        runOptimization()
    }

    fun runOptimization() {
        viewModelScope.launch {
            val assets = _uiState.value.assets
            val rf = _uiState.value.riskFreeRatePct / 100.0
            val res = PortfolioOptimizer.optimize(assets = assets, riskFreeRate = rf, simulationCount = 1000)
            _uiState.value = _uiState.value.copy(result = res)
        }
    }

    fun saveToDossier() {
        val res = _uiState.value.result ?: return
        viewModelScope.launch {
            val summary = "Markowitz Frontier Optimization of ${_uiState.value.assets.size} Assets. Max Sharpe: ${String.format("%.2f", res.maxSharpePortfolio.sharpeRatio)} at Vol ${String.format("%.1f", res.maxSharpePortfolio.volatility * 100)}%."
            val metrics = """
                {"sharpe": ${res.maxSharpePortfolio.sharpeRatio}, "vol": ${res.maxSharpePortfolio.volatility}, "return": ${res.maxSharpePortfolio.expectedReturn}}
            """.trimIndent()
            val insight = "CladRb Recommendation: Rebalance portfolio weights to Tangency Point (${_uiState.value.assets.mapIndexed { idx, a -> "${a.ticker}: ${String.format("%.1f", res.maxSharpePortfolio.weights.getOrElse(idx) { 0.0 } * 100)}%" }.joinToString(", ")}) for maximum risk-adjusted return."

            repository.saveAnalysis(
                title = "Markowitz Portfolio Allocation Dossier",
                category = "MARKOWITZ",
                summary = summary,
                detailedMetricsJson = metrics,
                cladrbInsight = insight
            )
            _uiState.value = _uiState.value.copy(saveSuccessMessage = "Optimization saved to Quantitative Dossier!")
        }
    }

    fun clearSaveMessage() {
        _uiState.value = _uiState.value.copy(saveSuccessMessage = null)
    }
}
