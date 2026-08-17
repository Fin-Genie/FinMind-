package com.example.domain.math

import kotlin.math.pow

data class DcfInputs(
    val companyName: String = "Apex Tech Global (APEX)",
    val currentSharePrice: Double = 175.0,
    val sharesOutstandingMillions: Double = 2500.0,
    val totalDebtMillions: Double = 45000.0,
    val cashAndEquivalentsMillions: Double = 62000.0,
    val initialFreeCashFlowMillions: Double = 28000.0,
    val fcfGrowthYears1to5Pct: Double = 14.5, // 14.5%
    val fcfGrowthYears6to10Pct: Double = 8.0,  // 8.0%
    val riskFreeRatePct: Double = 4.2,         // 4.2% US 10Y
    val equityRiskPremiumPct: Double = 5.0,    // 5.0%
    val beta: Double = 1.15,
    val costOfDebtPreTaxPct: Double = 5.2,
    val corporateTaxRatePct: Double = 21.0,
    val equityWeightPct: Double = 85.0,
    val perpetualGrowthRatePct: Double = 2.5,   // Gordon Growth terminal rate
    val terminalMultipleEbitda: Double = 16.0
)

data class ProjectedYearFcf(
    val year: Int,
    val projectedFcfMillions: Double,
    val discountFactor: Double,
    val presentValueMillions: Double
)

data class SensitivityCell(
    val discountRatePct: Double,
    val perpetualGrowthPct: Double,
    val intrinsicValuePerShare: Double,
    val marginOfSafetyPct: Double
)

data class DcfValuationResult(
    val costOfEquityPct: Double,
    val afterTaxCostOfDebtPct: Double,
    val calculatedWaccPct: Double,
    val projectedCashFlows: List<ProjectedYearFcf>,
    val cumulativePvFcfMillions: Double,
    val terminalValueGordonMillions: Double,
    val pvTerminalValueMillions: Double,
    val enterpriseValueMillions: Double,
    val netDebtMillions: Double,
    val equityValueMillions: Double,
    val intrinsicValuePerShare: Double,
    val currentSharePrice: Double,
    val marginOfSafetyPct: Double, // (Intrinsic - Current) / Current
    val sensitivityMatrix: List<List<SensitivityCell>>
)

object ValuationEngine {

    fun calculateDcf(inputs: DcfInputs): DcfValuationResult {
        // 1. CAPM Cost of Equity: r_e = r_f + \beta * ERP
        val costOfEquity = inputs.riskFreeRatePct + inputs.beta * inputs.equityRiskPremiumPct
        // 2. After-tax Cost of Debt: r_d * (1 - t)
        val afterTaxCostOfDebt = inputs.costOfDebtPreTaxPct * (1.0 - inputs.corporateTaxRatePct / 100.0)

        // 3. WACC = w_e * r_e + w_d * r_d * (1-t)
        val wEquity = inputs.equityWeightPct / 100.0
        val wDebt = 1.0 - wEquity
        val wacc = (wEquity * costOfEquity + wDebt * afterTaxCostOfDebt)
        val discountRate = (wacc / 100.0).coerceAtLeast(0.01)

        // 4. Project 10-year Free Cash Flows
        val projections = ArrayList<ProjectedYearFcf>(10)
        var currentFcf = inputs.initialFreeCashFlowMillions
        var sumPvFcf = 0.0

        for (year in 1..10) {
            val growth = if (year <= 5) inputs.fcfGrowthYears1to5Pct / 100.0 else inputs.fcfGrowthYears6to10Pct / 100.0
            currentFcf *= (1.0 + growth)
            val discountFactor = 1.0 / (1.0 + discountRate).pow(year)
            val pv = currentFcf * discountFactor
            sumPvFcf += pv
            projections.add(ProjectedYearFcf(year, currentFcf, discountFactor, pv))
        }

        // 5. Terminal Value via Gordon Growth: TV = FCF_10 * (1 + g) / (WACC - g)
        val g = (inputs.perpetualGrowthRatePct / 100.0).coerceAtMost(discountRate - 0.005)
        val finalYearFcf = projections.last().projectedFcfMillions
        val terminalValueGordon = (finalYearFcf * (1.0 + g)) / (discountRate - g).coerceAtLeast(0.001)
        val pvTerminalValue = terminalValueGordon / (1.0 + discountRate).pow(10)

        // 6. Enterprise Value & Equity Value
        val enterpriseValue = sumPvFcf + pvTerminalValue
        val netDebt = inputs.totalDebtMillions - inputs.cashAndEquivalentsMillions
        val equityValue = enterpriseValue - netDebt

        val intrinsicPerShare = (equityValue / inputs.sharesOutstandingMillions).coerceAtLeast(0.0)
        val marginOfSafety = if (inputs.currentSharePrice > 0) {
            ((intrinsicPerShare - inputs.currentSharePrice) / inputs.currentSharePrice) * 100.0
        } else 0.0

        // 7. Sensitivity Matrix (Discount Rate +- 1.5% vs Perpetual Growth +- 1.0%)
        val discountDeltas = listOf(-1.5, -0.75, 0.0, 0.75, 1.5)
        val growthDeltas = listOf(-1.0, -0.5, 0.0, 0.5, 1.0)
        val matrix = ArrayList<List<SensitivityCell>>()

        for (dDelta in discountDeltas) {
            val row = ArrayList<SensitivityCell>()
            val testWacc = (wacc + dDelta).coerceAtLeast(2.0)
            val testDiscRate = testWacc / 100.0

            for (gDelta in growthDeltas) {
                val testG = (inputs.perpetualGrowthRatePct + gDelta) / 100.0
                if (testDiscRate <= testG) {
                    row.add(SensitivityCell(testWacc, testG * 100.0, 0.0, -100.0))
                } else {
                    // Recompute PV with test discount rate
                    var testSumPv = 0.0
                    var testFcf = inputs.initialFreeCashFlowMillions
                    for (yr in 1..10) {
                        val gr = if (yr <= 5) inputs.fcfGrowthYears1to5Pct / 100.0 else inputs.fcfGrowthYears6to10Pct / 100.0
                        testFcf *= (1.0 + gr)
                        testSumPv += testFcf / (1.0 + testDiscRate).pow(yr)
                    }
                    val testTV = (testFcf * (1.0 + testG)) / (testDiscRate - testG)
                    val testPvTV = testTV / (1.0 + testDiscRate).pow(10)
                    val testEV = testSumPv + testPvTV
                    val testEqVal = testEV - netDebt
                    val testPerShare = (testEqVal / inputs.sharesOutstandingMillions).coerceAtLeast(0.0)
                    val testMargin = ((testPerShare - inputs.currentSharePrice) / inputs.currentSharePrice) * 100.0
                    row.add(SensitivityCell(testWacc, testG * 100.0, testPerShare, testMargin))
                }
            }
            matrix.add(row)
        }

        return DcfValuationResult(
            costOfEquityPct = costOfEquity,
            afterTaxCostOfDebtPct = afterTaxCostOfDebt,
            calculatedWaccPct = wacc,
            projectedCashFlows = projections,
            cumulativePvFcfMillions = sumPvFcf,
            terminalValueGordonMillions = terminalValueGordon,
            pvTerminalValueMillions = pvTerminalValue,
            enterpriseValueMillions = enterpriseValue,
            netDebtMillions = netDebt,
            equityValueMillions = equityValue,
            intrinsicValuePerShare = intrinsicPerShare,
            currentSharePrice = inputs.currentSharePrice,
            marginOfSafetyPct = marginOfSafety,
            sensitivityMatrix = matrix
        )
    }
}
