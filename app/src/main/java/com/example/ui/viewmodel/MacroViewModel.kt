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
import com.example.domain.macro.CladRbMacroCommentary
import com.example.domain.macro.CountryMacroProfile
import com.example.domain.macro.MacroDataSet
import com.example.domain.macro.MacroEconomicsEngine
import com.example.domain.macro.MacroIndicatorType
import com.example.domain.macro.TimeHorizon
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.json.JSONObject

enum class MacroDashboardTab(val label: String) {
    OVERVIEW("Overview"),
    INFLATION("Inflation"),
    RATES("Rates"),
    GDP("GDP Growth"),
    JOBS("Unemployment"),
    QUADRANT("Cycle Quadrant"),
    PEERS("Global Peers")
}

data class MacroUiState(
    val selectedCountryId: String = "US",
    val selectedHorizon: TimeHorizon = TimeHorizon.ONE_YEAR,
    val activeTab: MacroDashboardTab = MacroDashboardTab.OVERVIEW,
    val showOverlay: Boolean = false,
    val isAiGenerating: Boolean = false,
    val customAiAnalysis: String? = null,
    val snackbarMessage: String? = null
) {
    val currentCountry: CountryMacroProfile
        get() = MacroDataSet.getCountryById(selectedCountryId)

    val commentary: CladRbMacroCommentary
        get() = MacroEconomicsEngine.generateCladRbCommentary(currentCountry, selectedHorizon)
}

class MacroViewModel(application: Application) : AndroidViewModel(application) {

    private val db = AppDatabase.getDatabase(application)
    private val analysisDao = db.analysisDao()

    private val _uiState = MutableStateFlow(MacroUiState())
    val uiState: StateFlow<MacroUiState> = _uiState.asStateFlow()

    fun selectCountry(countryId: String) {
        _uiState.value = _uiState.value.copy(
            selectedCountryId = countryId,
            customAiAnalysis = null
        )
    }

    fun selectHorizon(horizon: TimeHorizon) {
        _uiState.value = _uiState.value.copy(
            selectedHorizon = horizon
        )
    }

    fun selectTab(tab: MacroDashboardTab) {
        _uiState.value = _uiState.value.copy(
            activeTab = tab
        )
    }

    fun toggleOverlay(enabled: Boolean) {
        _uiState.value = _uiState.value.copy(
            showOverlay = enabled
        )
    }

    fun saveMacroReportToDossier() {
        val state = _uiState.value
        val country = state.currentCountry
        val commentary = state.commentary

        viewModelScope.launch {
            val metricsObj = JSONObject().apply {
                put("country", country.name)
                put("flag", country.flag)
                put("currency", country.currency)
                put("centralBank", country.centralBank)
                put("timeHorizon", state.selectedHorizon.label)
                put("inflation", country.getLatestValue(MacroIndicatorType.INFLATION))
                put("policyRate", country.getLatestValue(MacroIndicatorType.INTEREST_RATE))
                put("gdpGrowth", country.getLatestValue(MacroIndicatorType.GDP_GROWTH))
                put("unemployment", country.getLatestValue(MacroIndicatorType.UNEMPLOYMENT))
                put("realPolicyRate", commentary.realRate)
                put("taylorRuleBenchmark", commentary.taylorRuleBenchmark)
                put("taylorRuleGap", commentary.taylorRuleGap)
                put("yieldCurveSpreadBps", commentary.yieldCurveSpreadBps)
                put("regime", commentary.regime.title)
            }

            val entity = AnalysisEntity(
                title = "${country.flag} ${country.name} Macro Dossier (${state.selectedHorizon.label})",
                category = "GLOBAL_MACRO",
                summary = "${commentary.regime.title}: CPI ${country.getLatestValue(MacroIndicatorType.INFLATION)}% vs Policy Rate ${country.getLatestValue(MacroIndicatorType.INTEREST_RATE)}% (Real Rate: ${commentary.realRate}%)",
                detailedMetricsJson = metricsObj.toString(2),
                cladrbInsight = "${commentary.executiveSummary}\n\n**Equities:** ${commentary.equitiesImplication}\n\n**Fixed Income:** ${commentary.fixedIncomeImplication}\n\n**FX:** ${commentary.fxImplication}\n\n**Commodities:** ${commentary.commoditiesImplication}\n\n**Tactical Playbook:**\n${commentary.tacticalPortfolioPlaybook}"
            )

            analysisDao.insertAnalysis(entity)
            _uiState.value = _uiState.value.copy(
                snackbarMessage = "${country.name} Macro Dossier archived successfully!"
            )
        }
    }

    fun clearSnackbarMessage() {
        _uiState.value = _uiState.value.copy(snackbarMessage = null)
    }

    fun queryCladRbDeepDive(customQuestion: String) {
        val state = _uiState.value
        val country = state.currentCountry
        val commentary = state.commentary

        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isAiGenerating = true)

            val prompt = """
                Country: ${country.name} (${country.flag})
                Horizon: ${state.selectedHorizon.label}
                Macro Regime: ${commentary.regime.title}
                Inflation (YoY): ${country.getLatestValue(MacroIndicatorType.INFLATION)}%
                Policy Rate: ${country.getLatestValue(MacroIndicatorType.INTEREST_RATE)}%
                Real Rate: ${commentary.realRate}%
                Real GDP Growth: ${country.getLatestValue(MacroIndicatorType.GDP_GROWTH)}%
                Unemployment: ${country.getLatestValue(MacroIndicatorType.UNEMPLOYMENT)}%
                Taylor Rule Gap: ${commentary.taylorRuleGap}%
                10Y-2Y Spread: ${commentary.yieldCurveSpreadBps} bps

                User Question: $customQuestion
            """.trimIndent()

            val aiResponse = withContext(Dispatchers.IO) {
                fetchGeminiOrLocalAnalysis(prompt, country, commentary)
            }

            _uiState.value = _uiState.value.copy(
                isAiGenerating = false,
                customAiAnalysis = aiResponse
            )
        }
    }

    private suspend fun fetchGeminiOrLocalAnalysis(
        prompt: String,
        country: CountryMacroProfile,
        commentary: CladRbMacroCommentary
    ): String {
        val apiKey = GeminiClient.getApiKey()

        if (apiKey.isBlank() || apiKey == "MY_GEMINI_API_KEY") {
            return generateLocalScenarioCommentary(country, commentary)
        }

        return try {
            val systemText = """
                You are CladRb, an elite Quantitative Financial Advisor and Global Macro Strategist.
                Provide mathematically precise, sophisticated, and charismatic commentary on the market implications
                of the provided economic indicators across Equities, Fixed Income duration, FX carry, and Commodities.
            """.trimIndent()

            val request = GeminiRequest(
                contents = listOf(
                    ContentDto(role = "user", parts = listOf(PartDto(text = prompt)))
                ),
                systemInstruction = ContentDto(parts = listOf(PartDto(text = systemText))),
                generationConfig = GenerationConfigDto(temperature = 0.7, maxOutputTokens = 1500)
            )

            val response = try {
                GeminiClient.apiService.generateContentFlash(apiKey, request)
            } catch (e: Exception) {
                GeminiClient.apiService.generateContentPro(apiKey, request)
            }

            response.candidates?.firstOrNull()?.content?.parts?.firstOrNull()?.text
                ?: generateLocalScenarioCommentary(country, commentary)
        } catch (e: Exception) {
            generateLocalScenarioCommentary(country, commentary)
        }
    }

    private fun generateLocalScenarioCommentary(
        country: CountryMacroProfile,
        commentary: CladRbMacroCommentary
    ): String {
        return buildString {
            append("### CladRb Macro Intelligence Scenario Breakdown\n\n")
            append("In ${country.name}, the coexistence of a **${commentary.realRate}% real interest rate** alongside **${country.getLatestValue(MacroIndicatorType.GDP_GROWTH)}% real GDP expansion** establishes a definitive **${commentary.regime.title}** environment.\n\n")
            append("#### Strategic Sensitivity Matrix:\n")
            append("- **Terminal Rate Trajectory:** The Taylor Rule (${commentary.taylorRuleBenchmark}%) indicates that central bank policy has a ${if (commentary.taylorRuleGap > 0) "restrictive buffer of ${commentary.taylorRuleGap}%" else "dovish cushion"}, granting flexibility to manage liquidity without triggering demand destruction.\n")
            append("- **Credit & Duration Spread:** Sovereign 10Y-2Y curve (${commentary.yieldCurveSpreadBps.toInt()} bps) underscores our recommendation to overweight intermediate 5-7 year duration over speculative credit spreads.\n")
            append("- **Cross-Asset Play:** Favour secular compounders with resilient pricing power and fortress balance sheets capable of compounding through restrictive monetary regimes.")
        }
    }
}
