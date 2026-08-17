package com.example.ui.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.local.AppDatabase
import com.example.data.repository.AdvisorRepository
import com.example.domain.math.AdfTestResult
import com.example.domain.math.CointegrationResult
import com.example.domain.math.GarchResult
import com.example.domain.math.TimeSeriesAnalytics
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlin.random.Random

data class TimeSeriesUiState(
    val selectedTab: Int = 0, // 0 = ADF Stationarity, 1 = GARCH Volatility, 2 = Cointegration Pairs
    val assetAName: String = "Asset Alpha (Tech Basket)",
    val assetBName: String = "Asset Beta (Index Benchmark)",
    val adfResult: AdfTestResult? = null,
    val garchResult: GarchResult? = null,
    val cointResult: CointegrationResult? = null,
    val garchAlpha: Double = 0.08,
    val garchBeta: Double = 0.88,
    val forecastHorizonDays: Int = 14,
    val saveSuccessMessage: String? = null
)

class TimeSeriesViewModel(application: Application) : AndroidViewModel(application) {

    private val db = AppDatabase.getDatabase(application)
    private val repository = AdvisorRepository(db.chatMessageDao(), db.analysisDao())

    private val _uiState = MutableStateFlow(TimeSeriesUiState())
    val uiState: StateFlow<TimeSeriesUiState> = _uiState.asStateFlow()

    private var currentSeriesA: List<Double> = emptyList()
    private var currentSeriesB: List<Double> = emptyList()

    init {
        generateNewDataAndRun()
    }

    fun setSelectedTab(tab: Int) {
        _uiState.value = _uiState.value.copy(selectedTab = tab)
    }

    fun updateGarchParameters(alpha: Double, beta: Double) {
        val total = alpha + beta
        val normAlpha = if (total >= 0.999) alpha / total * 0.98 else alpha
        val normBeta = if (total >= 0.999) beta / total * 0.98 else beta
        _uiState.value = _uiState.value.copy(garchAlpha = normAlpha, garchBeta = normBeta)
        recomputeGarch()
    }

    fun generateNewDataAndRun() {
        viewModelScope.launch {
            val (a, b) = TimeSeriesAnalytics.generateSyntheticPair()
            currentSeriesA = a
            currentSeriesB = b

            // 1. Run ADF test on raw price series vs first differences
            val returnsA = ArrayList<Double>(a.size - 1)
            for (i in 1 until a.size) {
                returnsA.add((a[i] - a[i - 1]) / a[i - 1])
            }

            val adf = TimeSeriesAnalytics.performAdfTest(returnsA)
            val garch = TimeSeriesAnalytics.fitGarch11(
                returns = returnsA,
                alphaInit = _uiState.value.garchAlpha,
                betaInit = _uiState.value.garchBeta,
                forecastHorizon = _uiState.value.forecastHorizonDays
            )
            val coint = TimeSeriesAnalytics.analyzeCointegration(
                assetAName = _uiState.value.assetAName,
                seriesA = a,
                assetBName = _uiState.value.assetBName,
                seriesB = b
            )

            _uiState.value = _uiState.value.copy(
                adfResult = adf,
                garchResult = garch,
                cointResult = coint
            )
        }
    }

    private fun recomputeGarch() {
        if (currentSeriesA.size < 2) return
        val returnsA = ArrayList<Double>(currentSeriesA.size - 1)
        for (i in 1 until currentSeriesA.size) {
            returnsA.add((currentSeriesA[i] - currentSeriesA[i - 1]) / currentSeriesA[i - 1])
        }
        val garch = TimeSeriesAnalytics.fitGarch11(
            returns = returnsA,
            alphaInit = _uiState.value.garchAlpha,
            betaInit = _uiState.value.garchBeta,
            forecastHorizon = _uiState.value.forecastHorizonDays
        )
        _uiState.value = _uiState.value.copy(garchResult = garch)
    }

    fun saveToDossier() {
        viewModelScope.launch {
            val coint = _uiState.value.cointResult
            val garch = _uiState.value.garchResult
            val adf = _uiState.value.adfResult

            val summary = "Time-Series & Volatility Dossier: Cointegration Z-Score: ${String.format("%.2f", coint?.currentZScore ?: 0.0)}, GARCH Persistence: ${String.format("%.3f", garch?.persistence ?: 0.0)}."
            val metrics = """
                {"zScore": ${coint?.currentZScore ?: 0.0}, "beta": ${coint?.hedgeRatioBeta ?: 1.0}, "garchPersistence": ${garch?.persistence ?: 0.0}}
            """.trimIndent()
            val insight = "CladRb Signal: ${coint?.tradingSignal ?: "Mean Equilibrium"}. Stationarity: ${if (adf?.isStationary == true) "Confirmed (I(0))" else "Unit Root Present"}. Volatility Shock Half-Life: ${String.format("%.1f", garch?.halfLifeShocks ?: 20.0)} trading days."

            repository.saveAnalysis(
                title = "Time-Series Econometric & Volatility Report",
                category = "TIME_SERIES",
                summary = summary,
                detailedMetricsJson = metrics,
                cladrbInsight = insight
            )
            _uiState.value = _uiState.value.copy(saveSuccessMessage = "Time-Series Dossier successfully archived!")
        }
    }

    fun clearSaveMessage() {
        _uiState.value = _uiState.value.copy(saveSuccessMessage = null)
    }
}
