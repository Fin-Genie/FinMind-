package com.example.ui.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.local.AppDatabase
import com.example.data.local.ChatMessageEntity
import com.example.data.repository.AdvisorRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

data class AdvisorUiState(
    val messages: List<ChatMessageEntity> = emptyList(),
    val isLoading: Boolean = false,
    val activeScenarioTag: String? = null,
    val errorMessage: String? = null
)

class AdvisorViewModel(application: Application) : AndroidViewModel(application) {

    private val db = AppDatabase.getDatabase(application)
    private val repository = AdvisorRepository(db.chatMessageDao(), db.analysisDao())

    private val _uiState = MutableStateFlow(AdvisorUiState())
    val uiState: StateFlow<AdvisorUiState> = _uiState.asStateFlow()

    init {
        viewModelScope.launch {
            repository.allChatMessages.collectLatest { msgList ->
                if (msgList.isEmpty()) {
                    // Seed initial CladRb welcome
                    seedInitialGreeting()
                } else {
                    _uiState.value = _uiState.value.copy(messages = msgList)
                }
            }
        }
    }

    private fun seedInitialGreeting() {
        viewModelScope.launch {
            val welcomeText = """
                Welcome! I am **CladRb**, your Quantitative Financial Advisor and analytical strategist. 

                Whether you're calibrating **Markowitz efficient frontiers**, testing **cointegrated pairs via Augmented Dickey-Fuller**, modeling **GARCH volatility clustering**, evaluating **DCF valuations**, or hedging tail risk via **Student-t VaR & Black-Scholes Greeks**, we will execute with mathematical precision and unwavering clarity.

                *Where would you like our analytical lens focused today?*
            """.trimIndent()
            repository.saveMessage(sender = "CLADRB", content = welcomeText, scenarioTag = "WELCOME")
        }
    }

    fun sendMessage(prompt: String, scenarioTag: String? = null) {
        if (prompt.isBlank() || _uiState.value.isLoading) return

        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true, activeScenarioTag = scenarioTag, errorMessage = null)
            
            // Save User message
            repository.saveMessage(sender = "USER", content = prompt.trim(), scenarioTag = scenarioTag)

            try {
                val currentHistory = _uiState.value.messages
                val response = repository.consultCladRb(prompt, currentHistory)
                repository.saveMessage(sender = "CLADRB", content = response, scenarioTag = scenarioTag)
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(errorMessage = "CladRb consultation error: ${e.localizedMessage}")
            } finally {
                _uiState.value = _uiState.value.copy(isLoading = false, activeScenarioTag = null)
            }
        }
    }

    fun clearHistory() {
        viewModelScope.launch {
            repository.clearChat()
            seedInitialGreeting()
        }
    }

    fun saveReportToDossier(title: String, summary: String, metricsJson: String, insight: String) {
        viewModelScope.launch {
            repository.saveAnalysis(
                title = title,
                category = "ADVISOR_NOTE",
                summary = summary,
                detailedMetricsJson = metricsJson,
                cladrbInsight = insight
            )
        }
    }
}
