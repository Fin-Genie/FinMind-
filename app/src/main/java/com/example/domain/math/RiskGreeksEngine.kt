package com.example.domain.math

import kotlin.math.abs
import kotlin.math.exp
import kotlin.math.ln
import kotlin.math.max
import kotlin.math.min
import kotlin.math.pow
import kotlin.math.sqrt
import kotlin.random.Random

data class VaRAnalysisResult(
    val portfolioValue: Double,
    val timeHorizonDays: Int,
    val confidenceLevelPct: Double, // 95% or 99%
    val parametricVaRDollars: Double,
    val parametricVaRPct: Double,
    val historicalVaRDollars: Double,
    val expectedShortfallCVaRDollars: Double, // Average loss beyond VaR cutoff
    val fatTailAdjustmentMultiplier: Double, // Student's t degrees of freedom correction
    val stressTestLoss2008Crisis: Double,
    val stressTestLoss2020Covid: Double,
    val simulatedPnLDistribution: List<Double>
)

data class OptionInputs(
    val underlyingPriceS: Double = 180.0,
    val strikePriceK: Double = 185.0,
    val daysToExpirationT: Double = 45.0, // 45 days
    val impliedVolatilitySigma: Double = 0.28, // 28%
    val riskFreeRateR: Double = 0.042, // 4.2%
    val dividendYieldQ: Double = 0.008 // 0.8%
)

data class OptionGreeks(
    val callPrice: Double,
    val putPrice: Double,
    val callDelta: Double,
    val putDelta: Double,
    val gamma: Double,
    val vega: Double,     // Per 1% change in volatility
    val callTheta: Double,// Per calendar day decay
    val putTheta: Double,
    val callRho: Double,  // Per 1% change in interest rates
    val putRho: Double,
    val d1: Double,
    val d2: Double,
    val strikePayoffsSpot: List<Double>,
    val callPayoffs: List<Double>,
    val putPayoffs: List<Double>
)

object RiskGreeksEngine {

    /**
     * Standard Normal Cumulative Distribution Function (CDF) Approximation
     * Abramowitz & Stegun formula (precision 1e-7)
     */
    fun stdNormalCdf(x: Double): Double {
        val b1 = 0.319381530
        val b2 = -0.356563782
        val b3 = 1.781477937
        val b4 = -1.821255978
        val b5 = 1.330274429
        val p = 0.2316419
        val c = 0.3989422804014327 // 1 / sqrt(2 * pi)

        if (x >= 0.0) {
            val t = 1.0 / (1.0 + p * x)
            return 1.0 - c * exp(-x * x / 2.0) * t * (t * (t * (t * (b5 * t + b4) + b3) + b2) + b1)
        } else {
            val t = 1.0 / (1.0 - p * x)
            return c * exp(-x * x / 2.0) * t * (t * (t * (t * (b5 * t + b4) + b3) + b2) + b1)
        }
    }

    fun stdNormalPdf(x: Double): Double {
        return (1.0 / sqrt(2.0 * Math.PI)) * exp(-0.5 * x * x)
    }

    /**
     * Compute Parametric VaR, Historical VaR, and Student-t Expected Shortfall (CVaR)
     */
    fun computeRiskMetrics(
        portfolioValue: Double = 1000000.0,
        annualizedVol: Double = 0.18,
        confidencePct: Double = 99.0,
        horizonDays: Int = 10,
        degreesOfFreedomT: Double = 5.0 // Fat-tailed stylized asset returns
    ): VaRAnalysisResult {
        val dt = horizonDays / 252.0
        val horizonVol = annualizedVol * sqrt(dt)

        // Gaussian critical z-score
        val zScore = if (confidencePct >= 99.0) 2.3263 else 1.6449

        // Student-t fat-tail quantile multiplier (leptokurtic tails)
        val tMultiplier = sqrt((degreesOfFreedomT - 2.0) / degreesOfFreedomT) * (if (confidencePct >= 99.0) 1.28 else 1.12)
        val adjustedZ = zScore * tMultiplier

        val parametricVaRPct = adjustedZ * horizonVol
        val parametricVaRDollars = portfolioValue * parametricVaRPct

        // Generate Monte Carlo PnL distribution (1000 trials)
        val rng = Random(88)
        val pnlList = ArrayList<Double>(1000)
        for (i in 0 until 1000) {
            // Student-t heavy tail generator
            val z = rng.nextGaussian()
            val chi2 = (0 until degreesOfFreedomT.toInt()).sumOf { rng.nextGaussian().pow(2) }
            val tDev = z / sqrt(chi2 / degreesOfFreedomT)
            val returnPnl = tDev * (annualizedVol / sqrt(252.0)) * sqrt(horizonDays.toDouble())
            pnlList.add(portfolioValue * returnPnl)
        }

        pnlList.sort()
        val cutoffIndex = ((1.0 - confidencePct / 100.0) * pnlList.size).toInt().coerceIn(1, pnlList.size - 1)
        val historicalVaRDollars = abs(pnlList[cutoffIndex])

        // Expected Shortfall (CVaR) = average of losses worse than VaR threshold
        val tailLosses = pnlList.take(cutoffIndex).map { abs(it) }
        val cVaRDollars = if (tailLosses.isNotEmpty()) tailLosses.average() else historicalVaRDollars * 1.25

        // Stress test scenarios
        val loss2008 = portfolioValue * (0.38 * sqrt(horizonDays / 30.0)).coerceAtMost(0.55)
        val loss2020 = portfolioValue * (0.28 * sqrt(horizonDays / 30.0)).coerceAtMost(0.40)

        return VaRAnalysisResult(
            portfolioValue = portfolioValue,
            timeHorizonDays = horizonDays,
            confidenceLevelPct = confidencePct,
            parametricVaRDollars = parametricVaRDollars,
            parametricVaRPct = parametricVaRPct * 100.0,
            historicalVaRDollars = historicalVaRDollars,
            expectedShortfallCVaRDollars = cVaRDollars,
            fatTailAdjustmentMultiplier = tMultiplier,
            stressTestLoss2008Crisis = loss2008,
            stressTestLoss2020Covid = loss2020,
            simulatedPnLDistribution = pnlList
        )
    }

    /**
     * Black-Scholes-Merton European Option Pricing with Dividends & Full Analytical Greeks
     */
    fun calculateOptionGreeks(inputs: OptionInputs): OptionGreeks {
        val s = inputs.underlyingPriceS
        val k = inputs.strikePriceK
        val t = max(0.001, inputs.daysToExpirationT / 365.0)
        val sigma = max(0.01, inputs.impliedVolatilitySigma)
        val r = inputs.riskFreeRateR
        val q = inputs.dividendYieldQ

        val sqrtT = sqrt(t)
        val d1 = (ln(s / k) + (r - q + 0.5 * sigma * sigma) * t) / (sigma * sqrtT)
        val d2 = d1 - sigma * sqrtT

        val nd1 = stdNormalCdf(d1)
        val nd2 = stdNormalCdf(d2)
        val nNegD1 = stdNormalCdf(-d1)
        val nNegD2 = stdNormalCdf(-d2)
        val npd1 = stdNormalPdf(d1)

        val expQt = exp(-q * t)
        val expRt = exp(-r * t)

        // Prices
        val callPrice = s * expQt * nd1 - k * expRt * nd2
        val putPrice = k * expRt * nNegD2 - s * expQt * nNegD1

        // Greeks
        val callDelta = expQt * nd1
        val putDelta = expQt * (nd1 - 1.0)

        val gamma = (expQt * npd1) / (s * sigma * sqrtT)
        val vega = (s * expQt * npd1 * sqrtT) / 100.0 // Scaled per 1% vol

        // Theta (per day)
        val term1 = -(s * sigma * expQt * npd1) / (2.0 * sqrtT)
        val callTheta = (term1 - r * k * expRt * nd2 + q * s * expQt * nd1) / 365.0
        val putTheta = (term1 + r * k * expRt * nNegD2 - q * s * expQt * nNegD1) / 365.0

        // Rho (per 1% interest rate)
        val callRho = (k * t * expRt * nd2) / 100.0
        val putRho = (-k * t * expRt * nNegD2) / 100.0

        // Payoff diagram points (Spot range: -30% to +30%)
        val spotPoints = ArrayList<Double>(25)
        val callPayoffs = ArrayList<Double>(25)
        val putPayoffs = ArrayList<Double>(25)

        for (i in 0..24) {
            val spot = s * (0.70 + i * (0.60 / 24.0))
            spotPoints.add(spot)
            // Expiration intrinsic payoff minus premium
            callPayoffs.add(max(0.0, spot - k) - callPrice)
            putPayoffs.add(max(0.0, k - spot) - putPrice)
        }

        return OptionGreeks(
            callPrice = max(0.0, callPrice),
            putPrice = max(0.0, putPrice),
            callDelta = callDelta,
            putDelta = putDelta,
            gamma = gamma,
            vega = vega,
            callTheta = callTheta,
            putTheta = putTheta,
            callRho = callRho,
            putRho = putRho,
            d1 = d1,
            d2 = d2,
            strikePayoffsSpot = spotPoints,
            callPayoffs = callPayoffs,
            putPayoffs = putPayoffs
        )
    }

    private fun Random.nextGaussian(): Double {
        var u1 = 0.0
        while (u1 == 0.0) u1 = nextDouble()
        val u2 = nextDouble()
        return sqrt(-2.0 * ln(u1)) * kotlin.math.cos(2.0 * Math.PI * u2)
    }
}
