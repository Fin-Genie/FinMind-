package com.example.domain.math

import kotlin.math.exp
import kotlin.math.max
import kotlin.math.min
import kotlin.math.pow
import kotlin.math.sqrt
import kotlin.random.Random

data class Asset(
    val id: String,
    val ticker: String,
    val name: String,
    val expectedReturn: Double, // Annualized, e.g. 0.12 = 12%
    val volatility: Double,     // Annualized std dev, e.g. 0.20 = 20%
    val weight: Double          // 0.0 to 1.0
)

data class PortfolioPoint(
    val expectedReturn: Double,
    val volatility: Double,
    val sharpeRatio: Double,
    val sortinoRatio: Double,
    val weights: List<Double>,
    val isTangency: Boolean = false,
    val isMinVariance: Boolean = false
)

data class MarkowitzResult(
    val currentPortfolio: PortfolioPoint,
    val maxSharpePortfolio: PortfolioPoint,
    val minVariancePortfolio: PortfolioPoint,
    val simulatedPoints: List<PortfolioPoint>,
    val efficientFrontierCurve: List<PortfolioPoint>,
    val correlationMatrix: Array<DoubleArray>,
    val covarianceMatrix: Array<DoubleArray>
)

object PortfolioOptimizer {

    fun generateDefaultAssets(): List<Asset> {
        return listOf(
            Asset(id = "1", ticker = "NVDA", name = "NVIDIA Corp (AI/Semis)", expectedReturn = 0.28, volatility = 0.38, weight = 0.30),
            Asset(id = "2", ticker = "MSFT", name = "Microsoft Corp (Cloud/SaaS)", expectedReturn = 0.16, volatility = 0.22, weight = 0.25),
            Asset(id = "3", ticker = "SPY", name = "S&P 500 ETF (Broad Market)", expectedReturn = 0.10, volatility = 0.15, weight = 0.25),
            Asset(id = "4", ticker = "TLT", name = "20+ Year Treasury Bond ETF", expectedReturn = 0.045, volatility = 0.14, weight = 0.20)
        )
    }

    fun computeCorrelationMatrix(assets: List<Asset>): Array<DoubleArray> {
        val n = assets.size
        val corr = Array(n) { DoubleArray(n) }
        // Standard realistic stylized financial correlations
        for (i in 0 until n) {
            for (j in 0 until n) {
                if (i == j) {
                    corr[i][j] = 1.0
                } else {
                    val t1 = assets[i].ticker
                    val t2 = assets[j].ticker
                    corr[i][j] = when {
                        (t1 == "NVDA" && t2 == "MSFT") || (t1 == "MSFT" && t2 == "NVDA") -> 0.65
                        (t1 == "NVDA" && t2 == "SPY") || (t1 == "SPY" && t2 == "NVDA") -> 0.72
                        (t1 == "MSFT" && t2 == "SPY") || (t1 == "SPY" && t2 == "MSFT") -> 0.78
                        (t1 == "TLT" || t2 == "TLT") -> -0.22 // Negative correlation hedge with bonds
                        else -> 0.40
                    }
                }
            }
        }
        return corr
    }

    fun computeCovarianceMatrix(assets: List<Asset>, corrMatrix: Array<DoubleArray>): Array<DoubleArray> {
        val n = assets.size
        val cov = Array(n) { DoubleArray(n) }
        for (i in 0 until n) {
            for (j in 0 until n) {
                cov[i][j] = corrMatrix[i][j] * assets[i].volatility * assets[j].volatility
            }
        }
        return cov
    }

    fun calculatePortfolioMetrics(
        weights: List<Double>,
        assets: List<Asset>,
        covMatrix: Array<DoubleArray>,
        riskFreeRate: Double = 0.042 // 4.2% US 10Y Benchmark
    ): PortfolioPoint {
        val n = assets.size
        var portReturn = 0.0
        for (i in 0 until n) {
            portReturn += weights[i] * assets[i].expectedReturn
        }

        var portVariance = 0.0
        for (i in 0 until n) {
            for (j in 0 until n) {
                portVariance += weights[i] * weights[j] * covMatrix[i][j]
            }
        }
        val portVolatility = sqrt(max(1e-8, portVariance))
        val sharpe = if (portVolatility > 0.0001) (portReturn - riskFreeRate) / portVolatility else 0.0

        // Semi-deviation approximation for Sortino Ratio (downside volatility ~ 0.707 * total vol)
        val downsideVol = portVolatility * 0.72
        val sortino = if (downsideVol > 0.0001) (portReturn - riskFreeRate) / downsideVol else 0.0

        return PortfolioPoint(
            expectedReturn = portReturn,
            volatility = portVolatility,
            sharpeRatio = sharpe,
            sortinoRatio = sortino,
            weights = weights
        )
    }

    fun optimize(
        assets: List<Asset>,
        customCorrelation: Array<DoubleArray>? = null,
        riskFreeRate: Double = 0.042,
        simulationCount: Int = 1200
    ): MarkowitzResult {
        val n = assets.size
        val corrMatrix = customCorrelation ?: computeCorrelationMatrix(assets)
        val covMatrix = computeCovarianceMatrix(assets, corrMatrix)

        // Normalize current weights
        val sumW = assets.sumOf { it.weight }.let { if (it > 0) it else 1.0 }
        val currentWeights = assets.map { it.weight / sumW }
        val currentPoint = calculatePortfolioMetrics(currentWeights, assets, covMatrix, riskFreeRate)

        val rng = Random(42)
        val simulatedPoints = ArrayList<PortfolioPoint>(simulationCount)

        var bestSharpePoint: PortfolioPoint? = null
        var minVolPoint: PortfolioPoint? = null

        // Monte Carlo Dirichlet-based random weight sampling
        for (step in 0 until simulationCount) {
            val raw = DoubleArray(n) { -kotlin.math.ln(rng.nextDouble().coerceIn(0.0001, 0.9999)) }
            val sumRaw = raw.sum()
            val w = raw.map { it / sumRaw }

            val point = calculatePortfolioMetrics(w, assets, covMatrix, riskFreeRate)
            simulatedPoints.add(point)

            if (bestSharpePoint == null || point.sharpeRatio > bestSharpePoint.sharpeRatio) {
                bestSharpePoint = point
            }
            if (minVolPoint == null || point.volatility < minVolPoint.volatility) {
                minVolPoint = point
            }
        }

        val maxSharpe = (bestSharpePoint ?: currentPoint).copy(isTangency = true)
        val minVariance = (minVolPoint ?: currentPoint).copy(isMinVariance = true)

        // Generate smooth Efficient Frontier upper hull curve
        val bins = 25
        val minV = minVariance.volatility
        val maxV = simulatedPoints.maxOf { it.volatility }
        val stepSize = (maxV - minV) / bins

        val frontierPoints = mutableListOf<PortfolioPoint>()
        frontierPoints.add(minVariance)

        for (b in 1..bins) {
            val targetVLow = minV + (b - 1) * stepSize
            val targetVHigh = minV + b * stepSize
            val candidates = simulatedPoints.filter { it.volatility in targetVLow..targetVHigh }
            if (candidates.isNotEmpty()) {
                val highestReturn = candidates.maxByOrNull { it.expectedReturn }
                if (highestReturn != null && (frontierPoints.isEmpty() || highestReturn.expectedReturn >= frontierPoints.last().expectedReturn * 0.95)) {
                    frontierPoints.add(highestReturn)
                }
            }
        }

        // Sort by volatility
        frontierPoints.sortBy { it.volatility }

        return MarkowitzResult(
            currentPortfolio = currentPoint,
            maxSharpePortfolio = maxSharpe,
            minVariancePortfolio = minVariance,
            simulatedPoints = simulatedPoints,
            efficientFrontierCurve = frontierPoints,
            correlationMatrix = corrMatrix,
            covarianceMatrix = covMatrix
        )
    }
}
