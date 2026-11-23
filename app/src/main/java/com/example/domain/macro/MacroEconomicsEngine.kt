package com.example.domain.macro

import kotlin.math.roundToInt

object MacroEconomicsEngine {

    /**
     * Compute Taylor Rule prescribed policy benchmark:
     * i* = r* + π + 0.5 * (π - π*) + 0.5 * (y - y_pot)
     */
    fun computeTaylorRule(profile: CountryMacroProfile): Double {
        val currentInflation = profile.getLatestValue(MacroIndicatorType.INFLATION)
        val currentGdp = profile.getLatestValue(MacroIndicatorType.GDP_GROWTH)
        val rStar = profile.neutralRateEstimate
        val piStar = profile.targetInflation

        val potentialGdp = when (profile.id) {
            "US" -> 2.1
            "EU" -> 1.2
            "JP" -> 0.8
            "UK" -> 1.3
            "CN" -> 4.5
            "DE" -> 0.9
            "IN" -> 6.5
            "CA" -> 1.8
            "AU" -> 2.2
            "BR" -> 2.4
            else -> 2.0
        }

        val inflationGap = currentInflation - piStar
        val outputGap = currentGdp - potentialGdp
        val benchmark = rStar + currentInflation + (0.5 * inflationGap) + (0.5 * outputGap)
        return (benchmark * 100.0).roundToInt() / 100.0
    }

    /**
     * Classify the macroeconomic regime based on real GDP growth and inflation relative to targets
     */
    fun determineMacroRegime(profile: CountryMacroProfile): MacroRegime {
        val inflation = profile.getLatestValue(MacroIndicatorType.INFLATION)
        val gdp = profile.getLatestValue(MacroIndicatorType.GDP_GROWTH)
        val targetInf = profile.targetInflation

        val isHighInflation = inflation > (targetInf + 0.5)
        val isHighGrowth = when (profile.id) {
            "IN" -> gdp >= 6.5
            "CN" -> gdp >= 4.5
            "BR" -> gdp >= 2.5
            else -> gdp >= 1.8
        }

        return when {
            !isHighInflation && isHighGrowth -> MacroRegime.GOLDILOCKS
            isHighInflation && isHighGrowth -> MacroRegime.REFLATION
            isHighInflation && !isHighGrowth -> MacroRegime.STAGFLATION
            else -> MacroRegime.SLOWDOWN
        }
    }

    /**
     * Generate comprehensive, quantitative, CladRb-persona commentary on market implications
     */
    fun generateCladRbCommentary(
        profile: CountryMacroProfile,
        timeHorizon: TimeHorizon
    ): CladRbMacroCommentary {
        val inflation = profile.getLatestValue(MacroIndicatorType.INFLATION)
        val prevInflation = profile.getPreviousValue(MacroIndicatorType.INFLATION)
        val interestRate = profile.getLatestValue(MacroIndicatorType.INTEREST_RATE)
        val gdp = profile.getLatestValue(MacroIndicatorType.GDP_GROWTH)
        val unemployment = profile.getLatestValue(MacroIndicatorType.UNEMPLOYMENT)

        val realRate = (profile.realPolicyRate * 100.0).roundToInt() / 100.0
        val taylorBenchmark = computeTaylorRule(profile)
        val taylorGap = ((interestRate - taylorBenchmark) * 100.0).roundToInt() / 100.0
        val yieldCurveBps = (profile.yieldCurveSpreadBps * 10.0).roundToInt() / 10.0
        val regime = determineMacroRegime(profile)

        val policyStanceBadge = when {
            realRate > 2.0 -> "Restricting Aggregate Demand (Real Rate +${realRate}%)"
            realRate in 0.5..2.0 -> "Moderately Restrictive Policy (Real Rate +${realRate}%)"
            realRate in -0.5..0.5 -> "Neutral Policy Equivalence"
            else -> "Accommodative Stimulus Drag (Negative Real Rates)"
        }

        val inflationDelta = inflation - prevInflation
        val inflationDirectionText = when {
            inflationDelta < -0.2 -> "compressing rapidly"
            inflationDelta > 0.2 -> "re-accelerating"
            else -> "anchored near current levels"
        }

        val executiveSummary = buildString {
            append("Dissecting ${profile.name}'s macroeconomic parameters under our ${timeHorizon.label} analytical horizon: ")
            append("Top-line inflation sits at ${inflation}% ($inflationDirectionText), against an effective policy benchmark of ${interestRate}%. ")
            append("This produces a positive real policy rate r = i - π of +${realRate}%, ")
            if (taylorGap > 0.5) {
                append("leaving central bank policy ${taylorGap}% tighter than standard Taylor Rule equilibrium (${taylorBenchmark}%). ")
            } else if (taylorGap < -0.5) {
                append("revealing central bank accommodation relative to Taylor Rule equilibrium (${taylorBenchmark}%). ")
            } else {
                append("perfectly aligned with Taylor Rule equilibrium (${taylorBenchmark}%). ")
            }
            append("With real GDP expanding at ${gdp}% and unemployment at ${unemployment}%, ")
            append("we characterize the current systemic state as a classic **${regime.title}**.")
        }

        val equitiesImplication = when (regime) {
            MacroRegime.GOLDILOCKS -> {
                "Sustained moderate economic expansion (+${gdp}%) without runaway price pressures is the premier tailwind for equity multiple expansion. " +
                "High-operating-leverage secular growth equities and quality balance-sheet compounders stand to benefit from stable discount rates. " +
                "Projected index forward P/E expansion toward 21-23x remains justifiable while corporate free cash flow margins withstand high real yields."
            }
            MacroRegime.REFLATION -> {
                "Accelerating nominal growth fosters top-line earnings beats, but sticky inflation (${inflation}%) forces discount rates upward, " +
                "sparking multiple compression in long-duration tech. Heavy rotation favored toward cyclicals, financials benefiting from wider net interest margins, " +
                "and industrials with strong pass-through pricing power."
            }
            MacroRegime.STAGFLATION -> {
                "Subdued GDP (+${gdp}%) paired with stubborn inflation (${inflation}%) exerts a direct squeeze on EBITDA margins. " +
                "Equity risk premia (ERP) are vulnerable to repricing. Maintain defensive defensive tilts toward healthcare, utilities, and consumer staples " +
                "with robust pricing power, while heavily underweighting debt-laden small caps vulnerable to elevated refinancing hurdles."
            }
            MacroRegime.SLOWDOWN -> {
                "Cooling output momentum signals late-cycle earnings vulnerability, particularly for consumer discretionary and cyclical industrials. " +
                "However, central bank rate-cutting cycles alleviate borrowing costs. Mega-cap quality growth with net cash balance sheets serves as the optimal equity bunker."
            }
        }

        val fixedIncomeImplication = buildString {
            append("With 10Y-2Y yield curve spread at ${yieldCurveBps} bps, ")
            if (yieldCurveBps < 0) {
                append("the yield curve remains inverted—historically signaling term-premium compression and late-cycle tightening stress. ")
            } else {
                append("the yield curve is in steepening normalization mode as front-end yields track central bank rate cuts. ")
            }
            if (realRate > 1.5) {
                append("Sovereign fixed income offers compelling real yields (+${realRate}%). Lock in intermediate duration (5Y-7Y paper) to capture capital appreciation as benchmark policy rates converge toward the neutral rate (${profile.neutralRateEstimate}%). ")
                append("Avoid speculative high-yield credit where default spreads underprice restrictive financing conditions.")
            } else {
                append("Maintain barbell duration with floating-rate notes and inflation-protected paper to preserve real capital against potential second-wave price stickiness.")
            }
        }

        val fxImplication = when (profile.id) {
            "US" -> "The US Dollar (DXY) retains structural yield-carry supremacy, supported by +${realRate}% real rates and resilient US GDP (+${gdp}%). Counter-currency strength will remain constrained until the Federal Reserve initiates aggressive terminal rate easing."
            "JP" -> "With the Bank of Japan normalizing from zero rates (policy rate at ${interestRate}%) while Western central banks ease, the JPY real yield differential is narrowing significantly. Watch for unwinding of global yen carry trades, exerting intermittent volatility across high-beta risk assets."
            "EU", "DE" -> "With ECB deposit rates easing toward neutral (${profile.neutralRateEstimate}%) and German industrial momentum subdued (+${gdp}%), the Euro faces downward carry pressure against the USD, making export-heavy DAX conglomerates relatively attractive."
            "UK" -> "Sterling (GBP) reflects a delicate balancing act: sticky services CPI (${inflation}%) limits aggressive BOE rate cuts, sustaining a modest carry cushion, but sluggish domestic GDP expansion (+${gdp}%) caps secular currency upside."
            "CN" -> "PBOC monetary easing and RRR reductions aim to stimulate credit velocity amid ${inflation}% CPI. The Yuan faces capital preservation management, though central bank swap facilities limit runaway depreciation."
            "IN" -> "The Indian Rupee (INR) benefits from stellar ${gdp}% GDP expansion, manageable external balances, and high foreign direct investment inflows, creating one of the most resilient emerging market currency risk profiles."
            "BR" -> "With Brazil's Selic rate at ${interestRate}% and inflation at ${inflation}%, Brazil offers a massive real yield cushion of +${realRate}%. This provides exceptional FX carry trade protection, supporting the BRL against broader EM currency swings."
            else -> "Real yield differentials remain the primary driver of currency flows. Sovereign economies with positive real rates and stable terms of trade will outperform high-debt deficit peers."
        }

        val commoditiesImplication = when (regime) {
            MacroRegime.GOLDILOCKS -> "Stable growth without inflation surges creates balanced conditions: industrial metals (Copper, Aluminum) track physical infrastructure expansion, while Gold serves primarily as a geopolitical hedge rather than an inflation hedge."
            MacroRegime.REFLATION -> "The premier environment for tangible assets: energy (Crude Oil, Natural Gas), base metals, and agricultural commodities experience strong cyclical demand pressure and backwardation on forward curves."
            MacroRegime.STAGFLATION -> "Gold emerges as the dominant macro asset, benefiting from currency debasement fears and negative stock-bond correlations. Energy remains structurally bid due to supply-side constraints."
            MacroRegime.SLOWDOWN -> "Subdued industrial production dampens cyclical commodity demand. Conversely, lower real yields and central bank rate cuts provide structural tailwinds for Gold and precious metals."
        }

        val tacticalPortfolioPlaybook = buildString {
            append("1. **Asset Allocation Tilt:** ")
            when (regime) {
                MacroRegime.GOLDILOCKS -> append("60% Quality Equities / 30% Core Fixed Income / 10% Opportunistic Alpha. ")
                MacroRegime.REFLATION -> append("45% Cyclical & Value Equities / 25% Commodities & Real Estate / 30% Short-Duration Cash Equities. ")
                MacroRegime.STAGFLATION -> append("35% Low-Beta Dividend Equities / 25% Gold & Physical Commodities / 40% TIPS & Short Treasuries. ")
                MacroRegime.SLOWDOWN -> append("45% Secular Tech & Quality Growth / 40% Intermediate Sovereign Duration / 15% Cash Equivalents. ")
            }
            append("\n2. **Fixed Income Strategy:** Lock in sovereign real yields (+${realRate}%) via 5-7 year maturities. Maintain selective investment-grade credit spreads.")
            append("\n3. **Derivative Hedge:** Deploy collar strategies or out-of-the-money put spreads on broad indexes to protect against late-cycle volatility spikes while harvesting elevated short-term risk premia.")
        }

        return CladRbMacroCommentary(
            countryName = profile.name,
            flag = profile.flag,
            regime = regime,
            stanceBadge = policyStanceBadge,
            executiveSummary = executiveSummary,
            taylorRuleBenchmark = taylorBenchmark,
            taylorRuleGap = taylorGap,
            realRate = realRate,
            yieldCurveSpreadBps = yieldCurveBps,
            equitiesImplication = equitiesImplication,
            fixedIncomeImplication = fixedIncomeImplication,
            fxImplication = fxImplication,
            commoditiesImplication = commoditiesImplication,
            tacticalPortfolioPlaybook = tacticalPortfolioPlaybook
        )
    }
}
