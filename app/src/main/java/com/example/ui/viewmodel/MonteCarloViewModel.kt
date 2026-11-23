package com.example.ui.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.local.AnalysisEntity
import com.example.data.local.AppDatabase
import com.example.data.remote.ContentDto
import com.example.data.remote.GeminiClient
import com.example.data.remote.GeminiRequest
import com.example.data.remote.GenerationConfigDto
import com.example.data.remote.PartDto
import com.example.domain.math.MonteCarloEngine
import com.example.domain.math.MonteCarloInputs
import com.example.domain.math.MonteCarloSimulationResult
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.json.JSONObject

enum class MonteCarloPreset(
    val title: String,
    val expectedReturn: Double,
    val volatility: Double,
    val description: String
) {
    AGGRESSIVE_TECH(
        title = "Aggressive Tech",
        expectedReturn = 0.16,
        volatility = 0.28,
        description = "High alpha semiconductors, AI, and SaaS compounders with elevated variance."
    ),
    BALANCED_60_40(
        title = "Balanced 60/40",
        expectedReturn = 0.085,
        volatility = 0.13,
        description = "Classic diversified equity and sovereign fixed income asset allocation."
    ),
    ALL_WEATHER(
        title = "All-Weather Parity",
        expectedReturn = 0.075,
        volatility = 0.09,
        description = "Risk parity across equities, duration, commodities, and inflation-protected paper."
    ),
    CONSERVATIVE(
        title = "Capital Preservation",
        expectedReturn = 0.052,
        volatility = 0.06,
        description = "Short-duration sovereign paper and high-quality dividend fortress balance sheets."
    )
}

data class MonteCarloUiState(
    val inputs: MonteCarloInputs = MonteCarloInputs(),
    val result: MonteCarloSimulationResult = MonteCarloEngine.runSimulation(MonteCarloInputs()),
    val isSimulating: Boolean = false,
    val selectedViewTab: Int = 0, // 0 = Trajectory Fan, 1 = Return Distribution Histogram
    val isAiGenerating: Boolean = false,
    val customAiInsight: String? = null,
    val snackbarMessage: String? = null
)

class MonteCarloViewModel(application: Application) : AndroidViewModel(application) {

    private val db = AppDatabase.getDatabase(application)
    private val analysisDao = db.analysisDao()

    private val _uiState = MutableStateFlow(MonteCarloUiState())
    val uiState: StateFlow<MonteCarloUiState> = _uiState.asStateFlow()

    fun updateInitialCapital(capital: Double) {
        val newInputs = _uiState.value.inputs.copy(initialCapital = capital.coerceAtLeast(1000.0))
        recomputeSimulation(newInputs)
    }

    fun updateExpectedReturn(returnPct: Double) {
        val newInputs = _uiState.value.inputs.copy(expectedAnnualReturn = (returnPct / 100.0).coerceIn(0.01, 0.40))
        recomputeSimulation(newInputs)
    }

    fun updateVolatility(volPct: Double) {
        val newInputs = _uiState.value.inputs.copy(annualVolatility = (volPct / 100.0).coerceIn(0.02, 0.60))
        recomputeSimulation(newInputs)
    }

    fun updateHorizonYears(years: Int) {
        val newInputs = _uiState.value.inputs.copy(horizonYears = years.coerceIn(1, 30))
        recomputeSimulation(newInputs)
    }

    fun updateMonthlyContribution(amount: Double) {
        val newInputs = _uiState.value.inputs.copy(monthlyContribution = amount.coerceAtLeast(0.0))
        recomputeSimulation(newInputs)
    }

    fun updateTargetGoal(goal: Double) {
        val newInputs = _uiState.value.inputs.copy(targetGoal = goal.coerceAtLeast(1000.0))
        recomputeSimulation(newInputs)
    }

    fun toggleFatTails(enabled: Boolean) {
        val newInputs = _uiState.value.inputs.copy(useFatTailsStudentT = enabled)
        recomputeSimulation(newInputs)
    }

    fun toggleInflationAdjustment(enabled: Boolean) {
        val newInputs = _uiState.value.inputs.copy(adjustForInflation = enabled)
        recomputeSimulation(newInputs)
    }

    fun applyPreset(preset: MonteCarloPreset) {
        val newInputs = _uiState.value.inputs.copy(
            expectedAnnualReturn = preset.expectedReturn,
            annualVolatility = preset.volatility
        )
        recomputeSimulation(newInputs)
    }

    fun selectViewTab(tab: Int) {
        _uiState.value = _uiState.value.copy(selectedViewTab = tab)
    }

    private fun recomputeSimulation(inputs: MonteCarloInputs) {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(inputs = inputs, isSimulating = true)
            val result = withContext(Dispatchers.Default) {
                MonteCarloEngine.runSimulation(inputs)
            }
            _uiState.value = _uiState.value.copy(
                inputs = inputs,
                result = result,
                isSimulating = false
            )
        }
    }

    fun saveReportToDossier() {
        val state = _uiState.value
        val res = state.result
        val inp = state.inputs

        viewModelScope.launch {
            val metricsObj = JSONObject().apply {
                put("initialCapital", inp.initialCapital)
                put("expectedReturnPct", inp.expectedAnnualReturn * 100.0)
                put("volatilityPct", inp.annualVolatility * 100.0)
                put("horizonYears", inp.horizonYears)
                put("monthlyContribution", inp.monthlyContribution)
                put("targetGoal", inp.targetGoal)
                put("totalInvested", res.totalInvested)
                put("medianTerminalWealth", res.medianTerminalWealth)
                put("p5TerminalWealth", res.p5TerminalWealth)
                put("p95TerminalWealth", res.p95TerminalWealth)
                put("probabilityOfSuccess", res.probabilityOfSuccess)
                put("probabilityOfLoss", res.probabilityOfLoss)
                put("medianCagr", res.medianCagr)
                put("expectedShortfallCVaR", res.expectedShortfallCVaR)
                put("maxDrawdownExpected", res.maxDrawdownExpected)
                put("useFatTails", inp.useFatTailsStudentT)
            }

            val entity = AnalysisEntity(
                title = "Monte Carlo Wealth Projection (${inp.horizonYears}Y, ${(inp.expectedAnnualReturn * 100).toInt()}% Return)",
                category = "MONTE_CARLO",
                summary = "Median: \$${String.format("%,.0f", res.medianTerminalWealth)} (CAGR: ${String.format("%.2f", res.medianCagr)}%) • Goal Probability: ${String.format("%.1f", res.probabilityOfSuccess)}%",
                detailedMetricsJson = metricsObj.toString(2),
                cladrbInsight = res.cladrbInsight
            )

            analysisDao.insertAnalysis(entity)
            _uiState.value = _uiState.value.copy(
                snackbarMessage = "Monte Carlo Dossier saved to archive!"
            )
        }
    }

    fun clearSnackbarMessage() {
        _uiState.value = _uiState.value.copy(snackbarMessage = null)
    }

    fun queryCladRbDeepDive(customQuestion: String) {
        val state = _uiState.value
        val res = state.result
        val inp = state.inputs

        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isAiGenerating = true)

            val prompt = """
                Initial Capital: ${'$'}${inp.initialCapital}
                Expected Annual Return: ${(inp.expectedAnnualReturn * 100)}%
                Annual Volatility: ${(inp.annualVolatility * 100)}%
                Time Horizon: ${inp.horizonYears} Years
                Monthly Contribution: ${'$'}${inp.monthlyContribution}
                Target Goal: ${'$'}${inp.targetGoal}
                Total Invested: ${'$'}${res.totalInvested}
                Median Projected Wealth: ${'$'}${res.medianTerminalWealth}
                5th Percentile Floor: ${'$'}${res.p5TerminalWealth}
                95th Percentile Bull: ${'$'}${res.p95TerminalWealth}
                Probability of Success: ${res.probabilityOfSuccess}%
                Probability of Capital Loss: ${res.probabilityOfLoss}%
                Expected Shortfall CVaR: ${'$'}${res.expectedShortfallCVaR}

                User Inquiry: $customQuestion
            """.trimIndent()

            val aiResponse = withContext(Dispatchers.IO) {
                fetchGeminiOrLocalAnalysis(prompt, res)
            }

            _uiState.value = _uiState.value.copy(
                isAiGenerating = false,
                customAiInsight = aiResponse
            )
        }
    }

    private suspend fun fetchGeminiOrLocalAnalysis(
        prompt: String,
        result: MonteCarloSimulationResult
    ): String {
        val apiKey = GeminiClient.getApiKey()

        if (apiKey.isBlank() || apiKey == "MY_GEMINI_API_KEY") {
            return generateLocalMonteCarloAdvice(result)
        }

        return try {
            val systemText = """
                You are CladRb, an elite Quantitative Financial Advisor and Stochastic Risk Architect.
                Analyze the provided Monte Carlo simulation metrics with mathematical rigor, charisma, and strategic clarity.
                Provide actionable insights on Ito's drift-drag, drawdown management, and wealth compounding probability.
            """.trimIndent()

            val request = GeminiRequest(
                contents = listOf(ContentDto(role = "user", parts = listOf(PartDto(text = prompt)))),
                systemInstruction = ContentDto(parts = listOf(PartDto(text = systemText))),
                generationConfig = GenerationConfigDto(temperature = 0.7, maxOutputTokens = 1500)
            )

            val response = try {
                GeminiClient.apiService.generateContentFlash(apiKey, request)
            } catch (e: Exception) {
                GeminiClient.apiService.generateContentPro(apiKey, request)
            }

            response.candidates?.firstOrNull()?.content?.parts?.firstOrNull()?.text
                ?: generateLocalMonteCarloAdvice(result)
        } catch (e: Exception) {
            generateLocalMonteCarloAdvice(result)
        }
    }

    private fun generateLocalMonteCarloAdvice(result: MonteCarloSimulationResult): String {
        return buildString {
            append("### CladRb Stochastic Consultation\n\n")
            append("Your simulation reflects an expected median outcome of **\$${String.format("%,.0f", result.medianTerminalWealth)}**, ")
            append("yielding a **${String.format("%.1f", result.probabilityOfSuccess)}% probability** of achieving your target.\n\n")
            append("#### Core Takeaways:\n")
            append("- **Asymmetric Upside:** Notice that the 95th percentile (\$${String.format("%,.0f", result.p95TerminalWealth)}) is much further from the median than the 5th percentile floor (\$${String.format("%,.0f", result.p5TerminalWealth)}). This right-skewness is the mathematical hallmark of exponential compounding.\n")
            append("- **Risk Management Directive:** Your expected maximum drawdown over this ${result.inputs.horizonYears}-year window is **${String.format("%.1f", result.maxDrawdownExpected)}%**. Ensure your portfolio structure incorporates periodic rebalancing to avoid sequence-of-returns risk during vulnerable transition years.")
        }
    }
}
