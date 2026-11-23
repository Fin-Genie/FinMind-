package com.example.domain.math

import kotlin.math.abs
import kotlin.math.exp
import kotlin.math.ln
import kotlin.math.max
import kotlin.math.min
import kotlin.math.pow
import kotlin.math.sqrt
import kotlin.random.Random

data class MonteCarloInputs(
    val initialCapital: Double = 100000.0,
    val expectedAnnualReturn: Double = 0.10, // 10%
    val annualVolatility: Double = 0.18,     // 18%
    val horizonYears: Int = 10,
    val numSimulations: Int = 2000,
    val monthlyContribution: Double = 500.0,
    val annualInflationRate: Double = 0.025, // 2.5%
    val targetGoal: Double = 350000.0,
    val useFatTailsStudentT: Boolean = true,
    val adjustForInflation: Boolean = false
)

data class TrajectoryStep(
    val year: Double,
    val stepIndex: Int,
    val p5: Double,
    val p25: Double,
    val p50: Double,
    val p75: Double,
    val p95: Double
)

data class DistributionBin(
    val minVal: Double,
    val maxVal: Double,
    val centerVal: Double,
    val count: Int,
    val probability: Double,
    val cumulativeProbability: Double,
    val returnPct: Double
)

data class MonteCarloSimulationResult(
    val inputs: MonteCarloInputs,
    val trajectorySteps: List<TrajectoryStep>,
    val samplePaths: List<List<Double>>, // 15 visual stochastic paths
    val terminalWealths: List<Double>,
    val terminalReturnsPct: List<Double>,
    val distributionBins: List<DistributionBin>,
    val medianTerminalWealth: Double,
    val meanTerminalWealth: Double,
    val p5TerminalWealth: Double,
    val p25TerminalWealth: Double,
    val p75TerminalWealth: Double,
    val p95TerminalWealth: Double,
    val probabilityOfSuccess: Double, // P(Wealth >= targetGoal)
    val probabilityOfLoss: Double,    // P(Wealth < totalInvested)
    val totalInvested: Double,
    val medianCagr: Double,
    val p5Cagr: Double,
    val p95Cagr: Double,
    val maxDrawdownExpected: Double,
    val expectedShortfallCVaR: Double,
    val skewness: Double,
    val kurtosis: Double,
    val cladrbInsight: String
)

object MonteCarloEngine {

    /**
     * Run full Monte Carlo simulation projecting stochastic portfolio outcomes
     * using Geometric Brownian Motion (GBM) with optional Student-t fat-tail innovations.
     */
    fun runSimulation(inputs: MonteCarloInputs): MonteCarloSimulationResult {
        val totalMonths = max(1, inputs.horizonYears * 12)
        val dt = 1.0 / 12.0
        val nSims = inputs.numSimulations.coerceIn(200, 5000)

        // Effective drift under Ito's Lemma: (mu - 0.5 * sigma^2)
        val drift = (inputs.expectedAnnualReturn - 0.5 * inputs.annualVolatility.pow(2)) * dt
        val volFactor = inputs.annualVolatility * sqrt(dt)
        val monthlyInflationRate = (1.0 + inputs.annualInflationRate).pow(dt) - 1.0

        val totalInvested = inputs.initialCapital + (inputs.monthlyContribution * totalMonths)

        // Matrix to collect wealth paths: [simIndex][monthIndex]
        val wealthPaths = Array(nSims) { DoubleArray(totalMonths + 1) }

        val random = Random(42) // Fixed seed for reproducible, deterministic rendering

        for (sim in 0 until nSims) {
            var currentS = inputs.initialCapital
            wealthPaths[sim][0] = currentS

            for (m in 1..totalMonths) {
                // Stochastic shock Z
                val z = if (inputs.useFatTailsStudentT) {
                    generateStudentTShock(random, degreesOfFreedom = 5)
                } else {
                    generateStandardNormalShock(random)
                }

                // Geometric Brownian Motion step
                val gbmReturn = exp(drift + volFactor * z)
                currentS = (currentS + inputs.monthlyContribution) * gbmReturn

                // Optional inflation deflation
                val recordedS = if (inputs.adjustForInflation) {
                    currentS / (1.0 + monthlyInflationRate).pow(m.toDouble())
                } else {
                    currentS
                }

                wealthPaths[sim][m] = max(0.0, recordedS)
            }
        }

        // Calculate Percentiles across each monthly time step
        val trajectorySteps = mutableListOf<TrajectoryStep>()
        for (m in 0..totalMonths) {
            val stepValues = DoubleArray(nSims) { wealthPaths[it][m] }.sorted()
            val yearFraction = m.toDouble() / 12.0

            val p5 = getPercentile(stepValues, 0.05)
            val p25 = getPercentile(stepValues, 0.25)
            val p50 = getPercentile(stepValues, 0.50)
            val p75 = getPercentile(stepValues, 0.75)
            val p95 = getPercentile(stepValues, 0.95)

            trajectorySteps.add(
                TrajectoryStep(
                    year = yearFraction,
                    stepIndex = m,
                    p5 = p5,
                    p25 = p25,
                    p50 = p50,
                    p75 = p75,
                    p95 = p95
                )
            )
        }

        // Extract 15 sample paths for visual richness
        val samplePaths = mutableListOf<List<Double>>()
        val stepInterval = max(1, nSims / 15)
        for (i in 0 until 15) {
            val pathIndex = min(nSims - 1, i * stepInterval)
            samplePaths.add(wealthPaths[pathIndex].toList())
        }

        // Terminal metrics
        val terminalWealths = DoubleArray(nSims) { wealthPaths[it][totalMonths] }.sorted()
        val terminalReturnsPct = terminalWealths.map { (it - totalInvested) / totalInvested * 100.0 }

        val p5Wealth = getPercentile(terminalWealths, 0.05)
        val p25Wealth = getPercentile(terminalWealths, 0.25)
        val p50Wealth = getPercentile(terminalWealths, 0.50)
        val p75Wealth = getPercentile(terminalWealths, 0.75)
        val p95Wealth = getPercentile(terminalWealths, 0.95)
        val meanWealth = terminalWealths.average()

        val successes = terminalWealths.count { it >= inputs.targetGoal }
        val probabilityOfSuccess = (successes.toDouble() / nSims.toDouble()) * 100.0

        val losses = terminalWealths.count { it < totalInvested }
        val probabilityOfLoss = (losses.toDouble() / nSims.toDouble()) * 100.0

        // CAGR calculations: (Wealth / Initial)^(1/T) - 1
        val tYears = inputs.horizonYears.toDouble()
        val medianCagr = ( (p50Wealth / totalInvested).pow(1.0 / tYears) - 1.0 ) * 100.0
        val p5Cagr = ( (max(0.001, p5Wealth) / totalInvested).pow(1.0 / tYears) - 1.0 ) * 100.0
        val p95Cagr = ( (p95Wealth / totalInvested).pow(1.0 / tYears) - 1.0 ) * 100.0

        // Expected Shortfall (CVaR) - average of bottom 5%
        val bottom5Count = max(1, (nSims * 0.05).toInt())
        val cvarLoss = totalInvested - terminalWealths.take(bottom5Count).average()
        val expectedShortfallCVaR = max(0.0, cvarLoss)

        // Estimated Maximum Drawdown from sample paths
        val maxDrawdownExpected = calculateEstimatedMaxDrawdown(wealthPaths, nSims, totalMonths)

        // Skewness and Kurtosis of terminal log-returns
        val logReturns = terminalWealths.map { ln(max(0.001, it) / inputs.initialCapital) }
        val skewness = computeSkewness(logReturns)
        val kurtosis = computeKurtosis(logReturns)

        // Binned Distribution for Histogram
        val distributionBins = computeDistributionBins(terminalWealths, totalInvested, numBins = 24)

        // CladRb Quantitative Insight
        val cladrbInsight = generateCladRbInsight(
            inputs = inputs,
            medianWealth = p50Wealth,
            p5Wealth = p5Wealth,
            p95Wealth = p95Wealth,
            probabilityOfSuccess = probabilityOfSuccess,
            probabilityOfLoss = probabilityOfLoss,
            medianCagr = medianCagr,
            totalInvested = totalInvested,
            skewness = skewness
        )

        return MonteCarloSimulationResult(
            inputs = inputs,
            trajectorySteps = trajectorySteps,
            samplePaths = samplePaths,
            terminalWealths = terminalWealths,
            terminalReturnsPct = terminalReturnsPct,
            distributionBins = distributionBins,
            medianTerminalWealth = p50Wealth,
            meanTerminalWealth = meanWealth,
            p5TerminalWealth = p5Wealth,
            p25TerminalWealth = p25Wealth,
            p75TerminalWealth = p75Wealth,
            p95TerminalWealth = p95Wealth,
            probabilityOfSuccess = probabilityOfSuccess,
            probabilityOfLoss = probabilityOfLoss,
            totalInvested = totalInvested,
            medianCagr = medianCagr,
            p5Cagr = p5Cagr,
            p95Cagr = p95Cagr,
            maxDrawdownExpected = maxDrawdownExpected,
            expectedShortfallCVaR = expectedShortfallCVaR,
            skewness = skewness,
            kurtosis = kurtosis,
            cladrbInsight = cladrbInsight
        )
    }

    /**
     * Standard Normal shock using Box-Muller transform
     */
    private fun generateStandardNormalShock(random: Random): Double {
        var u1 = random.nextDouble()
        while (u1 <= 1e-10) u1 = random.nextDouble()
        val u2 = random.nextDouble()
        return sqrt(-2.0 * ln(u1)) * kotlin.math.cos(2.0 * Math.PI * u2)
    }

    /**
     * Student's t distribution shock with degrees of freedom nu (models fat-tailed financial black swans)
     * T = Z / sqrt(V / nu), where V ~ Chi-Squared(nu)
     */
    private fun generateStudentTShock(random: Random, degreesOfFreedom: Int): Double {
        val z = generateStandardNormalShock(random)
        var chiSq = 0.0
        for (i in 0 until degreesOfFreedom) {
            val norm = generateStandardNormalShock(random)
            chiSq += norm * norm
        }
        val t = z / sqrt(chiSq / degreesOfFreedom.toDouble())
        // Standardize variance to 1.0: Var(T) = nu / (nu - 2)
        val scale = sqrt((degreesOfFreedom - 2).toDouble() / degreesOfFreedom.toDouble())
        return t * scale
    }

    private fun getPercentile(sorted: List<Double>, p: Double): Double {
        if (sorted.isEmpty()) return 0.0
        val idx = (p * (sorted.size - 1)).toInt().coerceIn(0, sorted.size - 1)
        return sorted[idx]
    }

    private fun getPercentile(sorted: DoubleArray, p: Double): Double {
        if (sorted.isEmpty()) return 0.0
        val idx = (p * (sorted.size - 1)).toInt().coerceIn(0, sorted.size - 1)
        return sorted[idx]
    }

    private fun calculateEstimatedMaxDrawdown(paths: Array<DoubleArray>, nSims: Int, totalMonths: Int): Double {
        var totalMaxDd = 0.0
        for (i in 0 until nSims) {
            var peak = paths[i][0]
            var maxDd = 0.0
            for (m in 1..totalMonths) {
                val v = paths[i][m]
                if (v > peak) {
                    peak = v
                } else if (peak > 0) {
                    val dd = (peak - v) / peak
                    if (dd > maxDd) maxDd = dd
                }
            }
            totalMaxDd += maxDd
        }
        return (totalMaxDd / nSims.toDouble()) * 100.0
    }

    private fun computeDistributionBins(
        terminalWealths: List<Double>,
        totalInvested: Double,
        numBins: Int
    ): List<DistributionBin> {
        if (terminalWealths.isEmpty()) return emptyList()

        val minW = terminalWealths.first()
        val maxW = terminalWealths.last()
        val range = (maxW - minW).coerceAtLeast(1.0)
        val binWidth = range / numBins.toDouble()

        val bins = mutableListOf<DistributionBin>()
        var cumulativeCount = 0

        for (i in 0 until numBins) {
            val bMin = minW + i * binWidth
            val bMax = if (i == numBins - 1) maxW + 0.01 else bMin + binWidth
            val count = terminalWealths.count { it >= bMin && it < bMax }
            cumulativeCount += count

            val centerVal = (bMin + bMax) / 2.0
            val prob = (count.toDouble() / terminalWealths.size.toDouble()) * 100.0
            val cumProb = (cumulativeCount.toDouble() / terminalWealths.size.toDouble()) * 100.0
            val returnPct = ((centerVal - totalInvested) / totalInvested) * 100.0

            bins.add(
                DistributionBin(
                    minVal = bMin,
                    maxVal = bMax,
                    centerVal = centerVal,
                    count = count,
                    probability = prob,
                    cumulativeProbability = cumProb,
                    returnPct = returnPct
                )
            )
        }

        return bins
    }

    private fun computeSkewness(data: List<Double>): Double {
        val n = data.size
        if (n < 3) return 0.0
        val mean = data.average()
        val std = sqrt(data.map { (it - mean).pow(2) }.average())
        if (std < 1e-6) return 0.0
        val m3 = data.map { (it - mean).pow(3) }.average()
        return m3 / std.pow(3)
    }

    private fun computeKurtosis(data: List<Double>): Double {
        val n = data.size
        if (n < 4) return 3.0
        val mean = data.average()
        val std = sqrt(data.map { (it - mean).pow(2) }.average())
        if (std < 1e-6) return 3.0
        val m4 = data.map { (it - mean).pow(4) }.average()
        return (m4 / std.pow(4)) - 3.0 // Excess kurtosis
    }

    private fun generateCladRbInsight(
        inputs: MonteCarloInputs,
        medianWealth: Double,
        p5Wealth: Double,
        p95Wealth: Double,
        probabilityOfSuccess: Double,
        probabilityOfLoss: Double,
        medianCagr: Double,
        totalInvested: Double,
        skewness: Double
    ): String {
        val driftDrag = (0.5 * inputs.annualVolatility.pow(2) * 100.0)
        val formattedMedian = String.format("%,.0f", medianWealth)
        val formattedP5 = String.format("%,.0f", p5Wealth)
        val formattedP95 = String.format("%,.0f", p95Wealth)
        val formattedTarget = String.format("%,.0f", inputs.targetGoal)

        return buildString {
            append("### CladRb Stochastic Synthesis: Monte Carlo Wealth Projection\n\n")
            append("Across **${inputs.numSimulations} stochastic Geometric Brownian Motion trajectories** over **${inputs.horizonYears} years**, ")
            append("your capital base exhibits an expected **Median Terminal Wealth of \$${formattedMedian}** ")
            append("(CAGR: **${String.format("%.2f", medianCagr)}%**), spanning a 90% confidence corridor from **\$${formattedP5}** (5th percentile floor) to **\$${formattedP95}** (95th percentile bull case).\n\n")

            append("#### 1. Stochastic Drift & Compounding Drag\n")
            append("Under Ito's Lemma, continuous geometric returns compound at the drift rate $(\\mu - \\frac{1}{2}\\sigma^2)$. ")
            append("With an annual volatility of **${(inputs.annualVolatility * 100).toInt()}%**, volatility drag claims **${String.format("%.2f", driftDrag)}% annualized** ")
            append("from your arithmetic mean. This proves why managing drawdowns matters far more than chasing sporadic upside spikes.\n\n")

            append("#### 2. Goal Feasibility & Downside Tail Risk\n")
            append("- **Probability of Reaching \$${formattedTarget}:** **${String.format("%.1f", probabilityOfSuccess)}%**\n")
            append("- **Probability of Capital Loss (Total Invested \$${String.format("%,.0f", totalInvested)}):** **${String.format("%.1f", probabilityOfLoss)}%**\n")
            append("- **Distribution Asymmetry:** Right-skewed (+${String.format("%.2f", skewness)}), reflecting the multiplicative nature of log-normal compounding.\n\n")

            append("#### 3. Strategic CladRb Counsel\n")
            if (probabilityOfSuccess >= 75.0) {
                append("Your probability profile is exceptionally robust (>75%). Do not fall prey to return-chasing; protect your compounding engine by rebalancing periodically into uncorrelated sovereign buffers.")
            } else if (probabilityOfSuccess in 50.0..74.9) {
                append("You have a viable probabilistic trajectory, but tail shocks could delay your target. Consider raising your monthly contributions by 15-20%—this systematically expands your terminal median without elevating portfolio variance.")
            } else {
                append("A ${String.format("%.1f", probabilityOfSuccess)}% probability of reaching your target indicates an aggressive hurdle rate. Rather than levering into excessive volatility, increase your capital injection frequency or extend the time horizon to harness exponential compounding.")
            }
        }
    }
}
