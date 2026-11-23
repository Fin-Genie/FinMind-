package com.example

import com.example.data.local.AnalysisDao
import com.example.data.local.AnalysisEntity
import com.example.data.model.FinancialNews
import com.example.data.model.ImpactDirection
import com.example.data.model.MarketSentiment
import com.example.data.model.NewsCategory
import com.example.data.model.NewsUrgency
import com.example.data.repository.NewsRepository
import com.example.domain.macro.MacroDataSet
import com.example.domain.macro.MacroEconomicsEngine
import com.example.domain.macro.MacroIndicatorType
import com.example.domain.macro.TimeHorizon
import com.example.domain.math.PortfolioOptimizer
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOf
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue
import org.junit.Test

class FakeAnalysisDao : AnalysisDao {
    override fun getAllAnalyses(): Flow<List<AnalysisEntity>> = flowOf(emptyList())
    override suspend fun insertAnalysis(analysis: AnalysisEntity): Long = 1L
    override suspend fun deleteAnalysisById(id: Long) {}
    override suspend fun getAnalysisById(id: Long): AnalysisEntity? = null
}

class ExampleUnitTest {
    private val fakeDao = FakeAnalysisDao()

    @Test
    fun testDefaultHeadlinesLoaded() {
        val repo = NewsRepository(fakeDao)
        val headlines = repo.getHeadlines()
        assertTrue(headlines.isNotEmpty())
        assertTrue(headlines.any { it.sentiment == MarketSentiment.BULLISH })
        assertTrue(headlines.any { it.sentiment == MarketSentiment.BEARISH })
    }

    @Test
    fun testCustomHeadlineAddition() {
        val repo = NewsRepository(fakeDao)
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
        val repo = NewsRepository(fakeDao)
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

    @Test
    fun testMacroDataSetCoverage() {
        val countries = MacroDataSet.allCountries
        assertEquals(10, countries.size)
        val us = MacroDataSet.getCountryById("US")
        assertEquals("United States", us.name)
        assertEquals("USD", us.currency)

        // Check that all 4 indicators exist across 1Y, 3Y, 5Y, and 10Y
        TimeHorizon.entries.forEach { horizon ->
            MacroIndicatorType.entries.forEach { indicator ->
                val series = us.getSeries(horizon, indicator)
                assertTrue("Missing series for $horizon $indicator", series.isNotEmpty())
            }
        }
    }

    @Test
    fun testMacroEconomicsCalculations() {
        val us = MacroDataSet.getCountryById("US")
        val taylorRule = MacroEconomicsEngine.computeTaylorRule(us)
        assertTrue("Taylor rule benchmark should be positive", taylorRule > 0.0)

        val commentary = MacroEconomicsEngine.generateCladRbCommentary(
            us,
            TimeHorizon.ONE_YEAR
        )
        assertNotNull(commentary)
        assertTrue(commentary.executiveSummary.contains("United States"))
        assertTrue(commentary.equitiesImplication.isNotBlank())
        assertTrue(commentary.fixedIncomeImplication.isNotBlank())
        assertTrue(commentary.fxImplication.isNotBlank())
        assertTrue(commentary.commoditiesImplication.isNotBlank())
        assertTrue(commentary.tacticalPortfolioPlaybook.isNotBlank())
    }

    @Test
    fun testMonteCarloSimulation() {
        val inputs = com.example.domain.math.MonteCarloInputs(
            initialCapital = 100000.0,
            expectedAnnualReturn = 0.10,
            annualVolatility = 0.18,
            horizonYears = 5,
            numSimulations = 500,
            monthlyContribution = 500.0,
            targetGoal = 200000.0,
            useFatTailsStudentT = true
        )
        val result = com.example.domain.math.MonteCarloEngine.runSimulation(inputs)

        assertNotNull(result)
        assertEquals(inputs, result.inputs)
        assertTrue("Trajectory steps should cover 60 months", result.trajectorySteps.size == 61)

        // Verify percentile ordering at each step: p5 <= p25 <= p50 <= p75 <= p95
        result.trajectorySteps.forEach { step ->
            assertTrue(step.p5 <= step.p25)
            assertTrue(step.p25 <= step.p50)
            assertTrue(step.p50 <= step.p75)
            assertTrue(step.p75 <= step.p95)
        }

        // Verify distribution bins
        assertTrue("Bins should not be empty", result.distributionBins.isNotEmpty())
        val sumProb = result.distributionBins.sumOf { it.probability }
        assertTrue("Total probability mass should be approx 100%", sumProb in 99.0..101.0)

        // Verify goal probability is between 0% and 100%
        assertTrue(result.probabilityOfSuccess in 0.0..100.0)
        assertTrue(result.probabilityOfLoss in 0.0..100.0)
        assertTrue(result.cladrbInsight.contains("Monte Carlo"))
    }
}
