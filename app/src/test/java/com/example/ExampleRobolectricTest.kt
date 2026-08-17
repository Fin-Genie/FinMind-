package com.example

import android.content.Context
import androidx.test.core.app.ApplicationProvider
import com.example.domain.math.DcfInputs
import com.example.domain.math.OptionInputs
import com.example.domain.math.PortfolioOptimizer
import com.example.domain.math.RiskGreeksEngine
import com.example.domain.math.TimeSeriesAnalytics
import com.example.domain.math.ValuationEngine
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [36])
class ExampleRobolectricTest {

    @Test
    fun `read string from context`() {
        val context = ApplicationProvider.getApplicationContext<Context>()
        val appName = context.getString(R.string.app_name)
        assertEquals("CladRb", appName)
    }

    @Test
    fun `test markowitz portfolio optimization engine`() {
        val assets = PortfolioOptimizer.generateDefaultAssets()
        val result = PortfolioOptimizer.optimize(assets = assets, riskFreeRate = 0.042, simulationCount = 200)

        assertNotNull(result)
        assertTrue(result.maxSharpePortfolio.sharpeRatio > 0.0)
        assertTrue(result.minVariancePortfolio.volatility < result.maxSharpePortfolio.volatility)
        assertEquals(4, result.maxSharpePortfolio.weights.size)
    }

    @Test
    fun `test DCF valuation engine and sensitivity matrix`() {
        val inputs = DcfInputs()
        val result = ValuationEngine.calculateDcf(inputs)

        assertNotNull(result)
        assertTrue(result.calculatedWaccPct > 0.0)
        assertTrue(result.intrinsicValuePerShare > 0.0)
        assertEquals(5, result.sensitivityMatrix.size)
        assertEquals(5, result.sensitivityMatrix[0].size)
    }

    @Test
    fun `test risk greeks and option pricing engine`() {
        val optInputs = OptionInputs()
        val greeks = RiskGreeksEngine.calculateOptionGreeks(optInputs)

        assertNotNull(greeks)
        assertTrue(greeks.callPrice > 0.0)
        assertTrue(greeks.callDelta in 0.0..1.0)
        assertTrue(greeks.putDelta in -1.0..0.0)
        assertTrue(greeks.gamma > 0.0)

        val riskRes = RiskGreeksEngine.computeRiskMetrics(portfolioValue = 1000000.0, annualizedVol = 0.18, confidencePct = 99.0, horizonDays = 10)
        assertTrue(riskRes.parametricVaRDollars > 0.0)
        assertTrue(riskRes.expectedShortfallCVaRDollars >= riskRes.parametricVaRDollars)
    }

    @Test
    fun `test time series ADF and GARCH engine`() {
        val (a, b) = TimeSeriesAnalytics.generateSyntheticPair()
        val returns = ArrayList<Double>(a.size - 1)
        for (i in 1 until a.size) {
            returns.add((a[i] - a[i - 1]) / a[i - 1])
        }

        val adf = TimeSeriesAnalytics.performAdfTest(returns)
        assertNotNull(adf)

        val garch = TimeSeriesAnalytics.fitGarch11(returns, 0.08, 0.88, 10)
        assertNotNull(garch)
        assertTrue(garch.persistence < 1.0)
    }
}
