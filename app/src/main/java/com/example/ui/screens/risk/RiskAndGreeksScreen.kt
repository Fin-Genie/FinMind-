package com.example.ui.screens.risk

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.BookmarkBorder
import androidx.compose.material.icons.filled.Security
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Slider
import androidx.compose.material3.SliderDefaults
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.TabRowDefaults
import androidx.compose.material3.TabRowDefaults.tabIndicatorOffset
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.ui.components.MathFormulaCard
import com.example.ui.components.MetricStatCard
import com.example.ui.components.OptionPayoffChart
import com.example.ui.theme.CrimsonRisk
import com.example.ui.theme.CyanAccent
import com.example.ui.theme.EmeraldAccent
import com.example.ui.theme.GoldAccent
import com.example.ui.theme.ObsidianBg
import com.example.ui.theme.PurpleGreeks
import com.example.ui.theme.SlateBorder
import com.example.ui.theme.SlateSurface
import com.example.ui.theme.SlateSurfaceVariant
import com.example.ui.theme.TextPrimary
import com.example.ui.theme.TextSecondary
import com.example.ui.theme.TextTertiary
import com.example.ui.viewmodel.RiskGreeksViewModel

@Composable
fun RiskAndGreeksScreen(
    viewModel: RiskGreeksViewModel,
    snackbarHostState: SnackbarHostState,
    modifier: Modifier = Modifier
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val vRes = uiState.varResult
    val gRes = uiState.optionGreeks
    val oInp = uiState.optionInputs

    LaunchedEffect(uiState.saveSuccessMessage) {
        uiState.saveSuccessMessage?.let {
            snackbarHostState.showSnackbar(it)
            viewModel.clearSaveMessage()
        }
    }

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .background(ObsidianBg)
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Title Bar
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        text = "Risk Management & Greeks",
                        style = MaterialTheme.typography.headlineLarge,
                        color = TextPrimary
                    )
                    Text(
                        text = "Fat-Tail VaR, Expected Shortfall & Black-Scholes",
                        style = MaterialTheme.typography.bodySmall,
                        color = CrimsonRisk
                    )
                }

                IconButton(
                    onClick = { viewModel.saveToDossier() },
                    modifier = Modifier
                        .size(42.dp)
                        .background(SlateSurface, RoundedCornerShape(10.dp))
                        .border(1.dp, GoldAccent.copy(alpha = 0.5f), RoundedCornerShape(10.dp))
                        .testTag("save_risk_button")
                ) {
                    Icon(
                        imageVector = Icons.Default.BookmarkBorder,
                        contentDescription = "Save to Dossier",
                        tint = GoldAccent
                    )
                }
            }
        }

        // Sub Tabs (VaR vs Option Greeks)
        item {
            TabRow(
                selectedTabIndex = uiState.selectedSubTab,
                containerColor = SlateSurface,
                contentColor = TextPrimary,
                indicator = { tabPositions ->
                    TabRowDefaults.SecondaryIndicator(
                        Modifier.tabIndicatorOffset(tabPositions[uiState.selectedSubTab]),
                        color = CrimsonRisk
                    )
                },
                modifier = Modifier
                    .border(1.dp, SlateBorder, RoundedCornerShape(10.dp))
                    .padding(2.dp)
            ) {
                Tab(
                    selected = uiState.selectedSubTab == 0,
                    onClick = { viewModel.setSelectedSubTab(0) },
                    text = { Text("Fat-Tail VaR & CVaR", style = MaterialTheme.typography.labelMedium, color = if (uiState.selectedSubTab == 0) CrimsonRisk else TextSecondary) }
                )
                Tab(
                    selected = uiState.selectedSubTab == 1,
                    onClick = { viewModel.setSelectedSubTab(1) },
                    text = { Text("Black-Scholes & Greeks", style = MaterialTheme.typography.labelMedium, color = if (uiState.selectedSubTab == 1) PurpleGreeks else TextSecondary) }
                )
            }
        }

        // SubTab 0: VaR & Expected Shortfall
        if (uiState.selectedSubTab == 0) {
            item {
                Column(verticalArrangement = Arrangement.spacedBy(14.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        MetricStatCard(
                            label = "${String.format("%.0f", vRes.confidenceLevelPct)}% Student-t VaR",
                            value = "$${String.format("%,.0f", vRes.parametricVaRDollars)}",
                            subValue = "${String.format("%.2f", vRes.parametricVaRPct)}% Max Loss",
                            valueColor = CrimsonRisk,
                            modifier = Modifier.weight(1f)
                        )
                        MetricStatCard(
                            label = "Expected Shortfall (CVaR)",
                            value = "$${String.format("%,.0f", vRes.expectedShortfallCVaRDollars)}",
                            subValue = "Average Tail Loss",
                            valueColor = GoldAccent,
                            modifier = Modifier.weight(1f)
                        )
                        MetricStatCard(
                            label = "Fat-Tail Multiplier",
                            value = "${String.format("%.2f", vRes.fatTailAdjustmentMultiplier)}x",
                            subValue = "ν=5 Deg of Freedom",
                            valueColor = CyanAccent,
                            modifier = Modifier.weight(1f)
                        )
                    }

                    // Stress Test Scenarios Card
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(SlateSurface, RoundedCornerShape(12.dp))
                            .border(1.dp, SlateBorder, RoundedCornerShape(12.dp))
                            .padding(14.dp),
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Text(
                            text = "Historical Crisis Stress Simulation",
                            style = MaterialTheme.typography.titleMedium,
                            color = TextPrimary
                        )

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text(text = "2008 Global Financial Crisis Shock:", style = MaterialTheme.typography.bodySmall, color = TextSecondary)
                            Text(
                                text = "-$${String.format("%,.0f", vRes.stressTestLoss2008Crisis)} (${String.format("%.1f", (vRes.stressTestLoss2008Crisis / vRes.portfolioValue) * 100)}%)",
                                style = MaterialTheme.typography.labelMedium,
                                color = CrimsonRisk
                            )
                        }

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text(text = "2020 COVID Liquidity Freeze Shock:", style = MaterialTheme.typography.bodySmall, color = TextSecondary)
                            Text(
                                text = "-$${String.format("%,.0f", vRes.stressTestLoss2020Covid)} (${String.format("%.1f", (vRes.stressTestLoss2020Covid / vRes.portfolioValue) * 100)}%)",
                                style = MaterialTheme.typography.labelMedium,
                                color = CrimsonRisk
                            )
                        }
                    }

                    // Risk Horizon Sliders
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(SlateSurface, RoundedCornerShape(12.dp))
                            .border(1.dp, SlateBorder, RoundedCornerShape(12.dp))
                            .padding(14.dp),
                        verticalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        Text(text = "Calibrate Risk Parameters", style = MaterialTheme.typography.titleMedium, color = TextPrimary)

                        Text(text = "Holding Period Horizon: ${uiState.horizonDays} Trading Days", style = MaterialTheme.typography.labelSmall, color = CyanAccent)
                        Slider(
                            value = uiState.horizonDays.toFloat(),
                            onValueChange = { viewModel.updateHorizonDays(it.toInt()) },
                            valueRange = 1f..30f,
                            colors = SliderDefaults.colors(thumbColor = CyanAccent, activeTrackColor = CyanAccent)
                        )

                        Text(text = "Annualized Volatility (σ): ${String.format("%.1f", uiState.annualizedVolPct)}%", style = MaterialTheme.typography.labelSmall, color = CrimsonRisk)
                        Slider(
                            value = uiState.annualizedVolPct.toFloat(),
                            onValueChange = { viewModel.updateAnnualizedVol(it.toDouble()) },
                            valueRange = 5f..60f,
                            colors = SliderDefaults.colors(thumbColor = CrimsonRisk, activeTrackColor = CrimsonRisk)
                        )

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Button(
                                onClick = { viewModel.updateConfidenceLevel(95.0) },
                                modifier = Modifier.weight(1f),
                                colors = ButtonDefaults.buttonColors(
                                    containerColor = if (uiState.confidencePct == 95.0) GoldAccent else SlateBorder.copy(alpha = 0.5f),
                                    contentColor = if (uiState.confidencePct == 95.0) ObsidianBg else TextPrimary
                                )
                            ) {
                                Text("95% Confidence")
                            }

                            Button(
                                onClick = { viewModel.updateConfidenceLevel(99.0) },
                                modifier = Modifier.weight(1f),
                                colors = ButtonDefaults.buttonColors(
                                    containerColor = if (uiState.confidencePct == 99.0) CrimsonRisk else SlateBorder.copy(alpha = 0.5f),
                                    contentColor = if (uiState.confidencePct == 99.0) ObsidianBg else TextPrimary
                                )
                            ) {
                                Text("99% Confidence")
                            }
                        }
                    }

                    MathFormulaCard(
                        title = "Student-t Fat-Tail Expected Shortfall (CVaR)",
                        formula = "CVaR_α = E [ L | L > VaR_α ] = μ + σ * [ f(z_α) / (1 - α) ] * sqrt((ν-2)/ν)",
                        explanation = "Quantifies the mathematical expectation of severe losses strictly beyond the VaR threshold, accounting for fat-tailed excess kurtosis common in liquidity shocks."
                    )
                }
            }
        }

        // SubTab 1: Black-Scholes & Greeks
        if (uiState.selectedSubTab == 1) {
            item {
                Column(verticalArrangement = Arrangement.spacedBy(14.dp)) {
                    // Option Call / Put Switcher
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(SlateSurface, RoundedCornerShape(10.dp))
                            .border(1.dp, SlateBorder, RoundedCornerShape(10.dp))
                            .padding(4.dp)
                    ) {
                        Box(
                            modifier = Modifier
                                .weight(1f)
                                .background(if (uiState.isCallSelected) EmeraldAccent else SlateSurface, RoundedCornerShape(8.dp))
                                .clickable { viewModel.toggleCallPut(true) }
                                .padding(vertical = 10.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = "Call Option ($${String.format("%.2f", gRes.callPrice)})",
                                style = MaterialTheme.typography.labelMedium,
                                color = if (uiState.isCallSelected) ObsidianBg else TextSecondary
                            )
                        }

                        Box(
                            modifier = Modifier
                                .weight(1f)
                                .background(if (!uiState.isCallSelected) PurpleGreeks else SlateSurface, RoundedCornerShape(8.dp))
                                .clickable { viewModel.toggleCallPut(false) }
                                .padding(vertical = 10.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = "Put Option ($${String.format("%.2f", gRes.putPrice)})",
                                style = MaterialTheme.typography.labelMedium,
                                color = if (!uiState.isCallSelected) ObsidianBg else TextSecondary
                            )
                        }
                    }

                    // Greeks 5-Grid
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        MetricStatCard(
                            label = "Delta (Δ)",
                            value = String.format("%.3f", if (uiState.isCallSelected) gRes.callDelta else gRes.putDelta),
                            subValue = "Spot Drift",
                            valueColor = EmeraldAccent,
                            modifier = Modifier.weight(1f)
                        )
                        MetricStatCard(
                            label = "Gamma (Γ)",
                            value = String.format("%.4f", gRes.gamma),
                            subValue = "Convexity",
                            valueColor = CyanAccent,
                            modifier = Modifier.weight(1f)
                        )
                        MetricStatCard(
                            label = "Vega (V)",
                            value = String.format("%.3f", gRes.vega),
                            subValue = "Per +1% IV",
                            valueColor = GoldAccent,
                            modifier = Modifier.weight(1f)
                        )
                        MetricStatCard(
                            label = "Theta (Θ)",
                            value = "$${String.format("%.3f", if (uiState.isCallSelected) gRes.callTheta else gRes.putTheta)}",
                            subValue = "Daily Bleed",
                            valueColor = CrimsonRisk,
                            modifier = Modifier.weight(1f)
                        )
                    }

                    // Payoff Diagram
                    OptionPayoffChart(
                        spotPoints = gRes.strikePayoffsSpot,
                        callPayoffs = gRes.callPayoffs,
                        putPayoffs = gRes.putPayoffs,
                        strikeK = oInp.strikePriceK,
                        isCallSelected = uiState.isCallSelected
                    )

                    // Greeks Tuning Sliders
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(SlateSurface, RoundedCornerShape(12.dp))
                            .border(1.dp, SlateBorder, RoundedCornerShape(12.dp))
                            .padding(14.dp),
                        verticalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        Text(text = "Option Parameters", style = MaterialTheme.typography.titleMedium, color = TextPrimary)

                        Text(text = "Underlying Spot S: $${String.format("%.1f", oInp.underlyingPriceS)}", style = MaterialTheme.typography.labelSmall, color = TextPrimary)
                        Slider(
                            value = oInp.underlyingPriceS.toFloat(),
                            onValueChange = { viewModel.updateOptionSpot(it.toDouble()) },
                            valueRange = 100f..300f,
                            colors = SliderDefaults.colors(thumbColor = GoldAccent, activeTrackColor = GoldAccent)
                        )

                        Text(text = "Strike Price K: $${String.format("%.1f", oInp.strikePriceK)}", style = MaterialTheme.typography.labelSmall, color = GoldAccent)
                        Slider(
                            value = oInp.strikePriceK.toFloat(),
                            onValueChange = { viewModel.updateOptionStrike(it.toDouble()) },
                            valueRange = 100f..300f,
                            colors = SliderDefaults.colors(thumbColor = GoldAccent, activeTrackColor = GoldAccent)
                        )

                        Text(text = "Days to Expiration T: ${oInp.daysToExpirationT.toInt()} Days", style = MaterialTheme.typography.labelSmall, color = CyanAccent)
                        Slider(
                            value = oInp.daysToExpirationT.toFloat(),
                            onValueChange = { viewModel.updateOptionDaysToExpiry(it.toDouble()) },
                            valueRange = 5f..180f,
                            colors = SliderDefaults.colors(thumbColor = CyanAccent, activeTrackColor = CyanAccent)
                        )

                        Text(text = "Implied Volatility (σ): ${String.format("%.1f", oInp.impliedVolatilitySigma * 100)}%", style = MaterialTheme.typography.labelSmall, color = PurpleGreeks)
                        Slider(
                            value = (oInp.impliedVolatilitySigma * 100).toFloat(),
                            onValueChange = { viewModel.updateOptionImpliedVol(it.toDouble()) },
                            valueRange = 10f..80f,
                            colors = SliderDefaults.colors(thumbColor = PurpleGreeks, activeTrackColor = PurpleGreeks)
                        )
                    }

                    MathFormulaCard(
                        title = "Black-Scholes-Merton Analytical Model",
                        formula = "C = S*e^{-qT}*N(d_1) - K*e^{-rT}*N(d_2),  d_1 = [ln(S/K) + (r-q+σ^2/2)T] / [σ*sqrt(T)]",
                        explanation = "Prices European options under geometric Brownian motion, deriving closed-form Greeks for continuous dynamic delta-hedging."
                    )
                }
            }
        }

        item {
            Spacer(modifier = Modifier.height(20.dp))
        }
    }
}
