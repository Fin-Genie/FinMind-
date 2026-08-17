package com.example.ui.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.local.AppDatabase
import com.example.data.model.FinancialNews
import com.example.data.model.MarketSentiment
import com.example.data.model.NewsCategory
import com.example.data.model.PortfolioImpactReport
import com.example.data.repository.NewsRepository
import com.example.domain.math.Asset
import com.example.domain.math.PortfolioOptimizer
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class NewsImpactUiState(
    val headlines: List<FinancialNews> = emptyList(),
    val selectedCategory: NewsCategory = NewsCategory.ALL,
    val searchQuery: String = "",
    val activeHoldings: List<Asset> = PortfolioOptimizer.generateDefaultAssets(),
    val currentReport: PortfolioImpactReport? = null,
    val analyzingNewsId: String? = null,
    val isBatchAnalyzing: Boolean = false,
    val saveStatusMessage: String? = null,
    val showCustomNewsDialog: Boolean = false,
    val showHoldingsEditor: Boolean = false
) {
    val filteredHeadlines: List<FinancialNews>
        get() = headlines.filter { news ->
            val matchesCategory = selectedCategory == NewsCategory.ALL || news.category == selectedCategory
            val matchesSearch = searchQuery.isBlank() ||
                    news.headline.contains(searchQuery, ignoreCase = true) ||
                    news.summary.contains(searchQuery, ignoreCase = true) ||
                    news.source.contains(searchQuery, ignoreCase = true) ||
                    news.affectedSectors.any { it.contains(searchQuery, ignoreCase = true) }
            matchesCategory && matchesSearch
        }
}

class NewsImpactViewModel(application: Application) : AndroidViewModel(application) {

    private val db = AppDatabase.getDatabase(application)
    private val repository = NewsRepository(db.analysisDao())

    private val _uiState = MutableStateFlow(NewsImpactUiState())
    val uiState: StateFlow<NewsImpactUiState> = _uiState.asStateFlow()

    init {
        loadHeadlines()
    }

    private fun loadHeadlines() {
        val list = repository.getHeadlines()
        _uiState.update { it.copy(headlines = list) }
    }

    fun setCategory(category: NewsCategory) {
        _uiState.update { it.copy(selectedCategory = category) }
    }

    fun setSearchQuery(query: String) {
        _uiState.update { it.copy(searchQuery = query) }
    }

    fun refreshHeadlines() {
        val refreshed = repository.refreshHeadlines()
        _uiState.update { it.copy(headlines = refreshed) }
    }

    fun openCustomNewsDialog(open: Boolean) {
        _uiState.update { it.copy(showCustomNewsDialog = open) }
    }

    fun openHoldingsEditor(open: Boolean) {
        _uiState.update { it.copy(showHoldingsEditor = open) }
    }

    fun addCustomHeadline(
        headline: String,
        source: String,
        summary: String,
        category: NewsCategory,
        sentiment: MarketSentiment
    ) {
        val created = repository.addCustomHeadline(
            headline = headline,
            source = source,
            summary = summary,
            category = category,
            sentiment = sentiment
        )
        _uiState.update {
            it.copy(
                headlines = repository.getHeadlines(),
                showCustomNewsDialog = false
            )
        }
        // Automatically trigger analysis on this custom headline
        analyzeHeadlineImpact(created)
    }

    fun analyzeHeadlineImpact(news: FinancialNews) {
        viewModelScope.launch {
            _uiState.update { it.copy(analyzingNewsId = news.id) }
            val report = repository.analyzeImpactWithGemini(
                news = news,
                holdings = _uiState.value.activeHoldings
            )
            _uiState.update {
                it.copy(
                    analyzingNewsId = null,
                    currentReport = report
                )
            }
        }
    }

    fun analyzeBatchImpact() {
        viewModelScope.launch {
            _uiState.update { it.copy(isBatchAnalyzing = true) }
            val topHeadlines = _uiState.value.headlines.take(3)
            val combinedHeadline = topHeadlines.joinToString(" | ") { it.headline }
            val combinedSummary = topHeadlines.joinToString(" ") { "${it.source}: ${it.summary}" }

            val compositeNews = FinancialNews(
                id = "batch-composite",
                headline = "Global Multi-Sector Synthesis: Top 3 Daily Market Headlines",
                source = "Macro Wire Desk Synthesis",
                category = NewsCategory.ALL,
                timeAgo = "Live",
                urgency = com.example.data.model.NewsUrgency.HIGH,
                summary = combinedSummary,
                sentiment = MarketSentiment.VOLATILE,
                affectedSectors = listOf("Broad Market", "Cross-Asset")
            )

            val report = repository.analyzeImpactWithGemini(
                news = compositeNews.copy(headline = combinedHeadline),
                holdings = _uiState.value.activeHoldings
            )

            _uiState.update {
                it.copy(
                    isBatchAnalyzing = false,
                    currentReport = report.copy(headlineTitle = "Multi-Headline Synthesis: " + topHeadlines.first().headline.take(35) + "...")
                )
            }
        }
    }

    fun updateHoldingWeight(assetId: String, newWeightPct: Double) {
        val normalizedWeight = (newWeightPct / 100.0).coerceIn(0.0, 1.0)
        val updated = _uiState.value.activeHoldings.map {
            if (it.id == assetId) it.copy(weight = normalizedWeight) else it
        }
        _uiState.update { it.copy(activeHoldings = updated) }
    }

    fun addCustomHolding(ticker: String, name: String, weightPct: Double) {
        val newAsset = Asset(
            id = System.currentTimeMillis().toString(),
            ticker = ticker.uppercase().trim(),
            name = name.trim().ifBlank { "${ticker.uppercase()} Holding" },
            expectedReturn = 0.12,
            volatility = 0.22,
            weight = (weightPct / 100.0).coerceIn(0.01, 1.0)
        )
        _uiState.update { it.copy(activeHoldings = it.activeHoldings + newAsset) }
    }

    fun saveCurrentReportToDossier() {
        val report = _uiState.value.currentReport ?: return
        viewModelScope.launch {
            repository.saveReportToDossier(report)
            _uiState.update {
                it.copy(saveStatusMessage = "Gemini Impact Report successfully archived to Dossier!")
            }
        }
    }

    fun clearSaveMessage() {
        _uiState.update { it.copy(saveStatusMessage = null) }
    }

    fun clearReport() {
        _uiState.update { it.copy(currentReport = null) }
    }
}
