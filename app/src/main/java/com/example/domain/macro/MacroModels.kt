package com.example.domain.macro

/**
 * Key macroeconomic indicators visualized in the CladRb Global Economic Dashboard
 */
enum class MacroIndicatorType(
    val title: String,
    val shortLabel: String,
    val unit: String,
    val description: String
) {
    INFLATION(
        title = "Inflation Rate (YoY)",
        shortLabel = "Inflation",
        unit = "%",
        description = "Annual change in Consumer Price Index (CPI) measuring purchasing power erosion."
    ),
    INTEREST_RATE(
        title = "Central Bank Policy Rate",
        shortLabel = "Policy Rate",
        unit = "%",
        description = "Benchmark overnight lending rate set by the monetary authority to govern liquidity."
    ),
    GDP_GROWTH(
        title = "Real GDP Growth Rate",
        shortLabel = "GDP Growth",
        unit = "%",
        description = "Annualized real gross domestic product expansion adjusted for inflation."
    ),
    UNEMPLOYMENT(
        title = "Unemployment Rate",
        shortLabel = "Jobless Rate",
        unit = "%",
        description = "Percentage of active civilian labor force without employment seeking work."
    )
}

/**
 * Historical time horizons available for macroeconomic trend inspection
 */
enum class TimeHorizon(
    val label: String,
    val fullLabel: String,
    val description: String
) {
    ONE_YEAR("1Y", "Past 12 Months", "High-frequency monthly shifts & policy recalibration"),
    THREE_YEAR("3Y", "Past 3 Years", "Post-tightening cycle & disinflation trajectory"),
    FIVE_YEAR("5Y", "Past 5 Years", "Pandemic shock, liquidity surge & aggressive rate hiking"),
    TEN_YEAR("10Y", "Past 10 Years", "Full secular macro regime from ZIRP to structural inflation")
}

/**
 * Single data point along the time series
 */
data class MacroDataPoint(
    val periodLabel: String,
    val value: Double,
    val periodIndex: Int = 0
)

/**
 * Comprehensive macroeconomic profile for an individual sovereign nation or economic bloc
 */
data class CountryMacroProfile(
    val id: String,
    val name: String,
    val flag: String,
    val centralBank: String,
    val currency: String,
    val targetInflation: Double,
    val neutralRateEstimate: Double,
    val debtToGdp: Double,
    val tenYearYield: Double,
    val twoYearYield: Double,
    val seriesByHorizon: Map<TimeHorizon, Map<MacroIndicatorType, List<MacroDataPoint>>>
) {
    fun getSeries(horizon: TimeHorizon, indicator: MacroIndicatorType): List<MacroDataPoint> {
        return seriesByHorizon[horizon]?.get(indicator) ?: emptyList()
    }

    fun getLatestValue(indicator: MacroIndicatorType): Double {
        val series = seriesByHorizon[TimeHorizon.ONE_YEAR]?.get(indicator)
        return series?.lastOrNull()?.value ?: 0.0
    }

    fun getPreviousValue(indicator: MacroIndicatorType): Double {
        val series = seriesByHorizon[TimeHorizon.ONE_YEAR]?.get(indicator)
        return if (series != null && series.size >= 2) series[series.size - 2].value else getLatestValue(indicator)
    }

    val realPolicyRate: Double
        get() = getLatestValue(MacroIndicatorType.INTEREST_RATE) - getLatestValue(MacroIndicatorType.INFLATION)

    val yieldCurveSpreadBps: Double
        get() = (tenYearYield - twoYearYield) * 100.0
}

/**
 * Macroeconomic Regime Classification for Quadrant Radar
 */
enum class MacroRegime(
    val title: String,
    val subtitle: String,
    val description: String
) {
    GOLDILOCKS(
        title = "Goldilocks / Non-Inflationary Growth",
        subtitle = "Optimal Equity Multiple Expansion",
        description = "GDP expansion running above potential with cooling or target inflation."
    ),
    REFLATION(
        title = "Overheating / Reflationary Surge",
        subtitle = "Commodities & Real Assets Outperform",
        description = "Accelerating economic demand coupled with rising inflationary pressures."
    ),
    STAGFLATION(
        title = "Stagflationary Impasse",
        subtitle = "Cost-Push Margin Squeeze & High Volatility",
        description = "Subdued or contracting economic growth concurrent with sticky elevated inflation."
    ),
    SLOWDOWN(
        title = "Disinflationary Slowdown / Policy Pivot",
        subtitle = "Fixed Income Duration & Quality Flight",
        description = "Slowing economic momentum paired with declining inflation, prompting central bank cuts."
    )
}

/**
 * Comprehensive CladRb Commentary & Quantitative Market Implications
 */
data class CladRbMacroCommentary(
    val countryName: String,
    val flag: String,
    val regime: MacroRegime,
    val stanceBadge: String,
    val executiveSummary: String,
    val taylorRuleBenchmark: Double,
    val taylorRuleGap: Double,
    val realRate: Double,
    val yieldCurveSpreadBps: Double,
    val equitiesImplication: String,
    val fixedIncomeImplication: String,
    val fxImplication: String,
    val commoditiesImplication: String,
    val tacticalPortfolioPlaybook: String
)
