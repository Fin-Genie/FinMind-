package com.example

import com.example.data.model.FinancialNews
import com.example.data.model.ImpactDirection
import com.example.data.model.MarketSentiment
import com.example.data.model.NewsCategory
import com.example.data.model.NewsUrgency
import com.example.data.repository.NewsRepository
import com.example.domain.math.PortfolioOptimizer
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue
import org.junit.Test

class ExampleUnitTest {
    @Test
    fun testDefaultHeadlinesLoaded() {
        val repo = NewsRepository(null)
        val headlines = repo.getHeadlines()
        assertTrue(headlines.isNotEmpty())
        assertTrue(headlines.any { it.sentiment == MarketSentiment.BULLISH })
        assertTrue(headlines.any { it.sentiment == MarketSentiment.BEARISH })
    }

    @Test
    fun testCustomHeadlineAddition() {
        val repo = NewsRepository(null)
        val initialCount = repo.getHeadlines().size
        val custom = repo.addCustomHeadline(
            headline = "Fed cuts benchmark rate by 50 basis points",
            source = "Federal Reserve Press Release",
            summary = "FOMC lowers target range for federal funds rate to support employment and target 2% inflation.",
            category = NewsCategory.MACRO_RATES,
            sentiment = MarketSentiment.BULLISH
        )
        val updated = repo.getHeadlines()
        assertEquals(initialCount + 1, updated.size)
        assertEquals("Fed cuts benchmark rate by 50 basis points", updated.first().headline)
        assertTrue(custom.isCustom)
    }

    @Test
    fun testHoldingsImpactGeneration() {
        val repo = NewsRepository(null)
        val testHoldings = PortfolioOptimizer.generateDefaultAssets()
        val news = FinancialNews(
            id = "test-1",
            headline = "Tech Hyperscalers Accelerate AI Data Center Infrastructure CapEx",
            source = "Tech Wire",
            category = NewsCategory.TECH_AI,
            timeAgo = "10m ago",
            urgency = NewsUrgency.HIGH,
            summary = "Cloud giants commit additional billions to GPU clusters.",
            sentiment = MarketSentiment.BULLISH,
            affectedSectors = listOf("Semiconductors", "Cloud")
        )

        val report = repo.generateDeterministicImpactReport(news, testHoldings)
        assertNotNull(report)
        assertEquals(news.headline, report.headlineTitle)
        assertTrue(report.assetImpacts.isNotEmpty())
        assertTrue(report.assetImpacts.any { it.ticker == "NVDA" && it.impactDirection == ImpactDirection.BULLISH })
    }
}
