package com.example.ui.screens.timeseries

import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.BookmarkBorder
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Timeline
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
import com.example.ui.components.CointegrationSpreadChart
import com.example.ui.components.GarchVolatilityChart
import com.example.ui.components.MathFormulaCard
import com.example.ui.components.MetricStatCard
import com.example.ui.theme.CrimsonRisk
import com.example.ui.theme.CyanAccent
import com.example.ui.theme.EmeraldAccent
import com.example.ui.theme.GoldAccent
import com.example.ui.theme.ObsidianBg
import com.example.ui.theme.SlateBorder
import com.example.ui.theme.SlateSurface
import com.example.ui.theme.TextPrimary
import com.example.ui.theme.TextSecondary
import com.example.ui.theme.TextTertiary
import com.example.ui.viewmodel.TimeSeriesViewModel

@Composable
fun TimeSeriesVolatilityScreen(
    viewModel: TimeSeriesViewModel,
    snackbarHostState: SnackbarHostState,
    modifier: Modifier = Modifier
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

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
                        text = "Time Series & Volatility Studio",
                        style = MaterialTheme.typography.headlineLarge,
                        color = TextPrimary
                    )
                    Text(
                        text = "Stationarity (ADF), GARCH(1,1) & Cointegration Pairs",
                        style = MaterialTheme.typography.bodySmall,
                        color = CyanAccent
                    )
                }

                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    IconButton(
                        onClick = { viewModel.generateNewDataAndRun() },
                        modifier = Modifier
                            .size(42.dp)
                            .background(SlateSurface, RoundedCornerShape(10.dp))
                            .border(1.dp, CyanAccent.copy(alpha = 0.5f), RoundedCornerShape(10.dp))
                    ) {
                        Icon(imageVector = Icons.Default.Refresh, contentDescription = "Simulate New Series", tint = CyanAccent)
                    }

                    IconButton(
                        onClick = { viewModel.saveToDossier() },
                        modifier = Modifier
                            .size(42.dp)
                            .background(SlateSurface, RoundedCornerShape(10.dp))
                            .border(1.dp, GoldAccent.copy(alpha = 0.5f), RoundedCornerShape(10.dp))
                            .testTag("save_timeseries_button")
                    ) {
                        Icon(imageVector = Icons.Default.BookmarkBorder, contentDescription = "Save to Dossier", tint = GoldAccent)
                    }
                }
            }
        }

        // Sub Tabs
        item {
            TabRow(
                selectedTabIndex = uiState.selectedTab,
                containerColor = SlateSurface,
                contentColor = TextPrimary,
                indicator = { tabPositions ->
                    TabRowDefaults.SecondaryIndicator(
                        Modifier.tabIndicatorOffset(tabPositions[uiState.selectedTab]),
                        color = CyanAccent
                    )
                },
                modifier = Modifier
                    .border(1.dp, SlateBorder, RoundedCornerShape(10.dp))
                    .padding(2.dp)
            ) {
                Tab(
                    selected = uiState.selectedTab == 0,
                    onClick = { viewModel.setSelectedTab(0) },
                    text = { Text("ADF Stationarity", style = MaterialTheme.typography.labelMedium, color = if (uiState.selectedTab == 0) CyanAccent else TextSecondary) }
                )
                Tab(
                    selected = uiState.selectedTab == 1,
                    onClick = { viewModel.setSelectedTab(1) },
                    text = { Text("GARCH(1,1)", style = MaterialTheme.typography.labelMedium, color = if (uiState.selectedTab == 1) CyanAccent else TextSecondary) }
                )
                Tab(
                    selected = uiState.selectedTab == 2,
                    onClick = { viewModel.setSelectedTab(2) },
                    text = { Text("Cointegration", style = MaterialTheme.typography.labelMedium, color = if (uiState.selectedTab == 2) CyanAccent else TextSecondary) }
                )
            }
        }

        // Tab Content 0: ADF Stationarity Test
        if (uiState.selectedTab == 0) {
            item {
                val adf = uiState.adfResult
                if (adf != null) {
                    Column(verticalArrangement = Arrangement.spacedBy(14.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            MetricStatCard(
                                label = "ADF Test Stat (τ)",
                                value = String.format("%.3f", adf.testStatistic),
                                subValue = "Critical 5%: -2.87",
                                valueColor = if (adf.isStationary) EmeraldAccent else CrimsonRisk,
                                modifier = Modifier.weight(1f)
                            )
                            MetricStatCard(
                                label = "p-Value",
                                value = String.format("%.4f", adf.pValue),
                                subValue = if (adf.isStationary) "p < 0.05 (Pass)" else "p ≥ 0.05 (Fail)",
                                valueColor = if (adf.isStationary) EmeraldAccent else CrimsonRisk,
                                modifier = Modifier.weight(1f)
                            )
                            MetricStatCard(
                                label = "Stationarity State",
                                value = if (adf.isStationary) "Stationary I(0)" else "Unit Root I(1)",
                                subValue = "Mean Reverting",
                                valueColor = if (adf.isStationary) EmeraldAccent else GoldAccent,
                                modifier = Modifier.weight(1f)
                            )
                        }

                        MathFormulaCard(
                            title = "Augmented Dickey-Fuller (ADF) Regression",
                            formula = "Δy_t = α + β*t + γ*y_{t-1} + Σ δ_i*Δy_{t-i} + ε_t",
                            explanation = "Tests null hypothesis H_0: γ = 0 (presence of unit root, non-stationary random walk) versus alternative H_1: γ < 0 (mean-reverting stationary process)."
                        )

                        // Econometric interpretation card
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .background(SlateSurface, RoundedCornerShape(12.dp))
                                .border(1.dp, if (adf.isStationary) EmeraldAccent.copy(alpha = 0.4f) else GoldAccent.copy(alpha = 0.4f), RoundedCornerShape(12.dp))
                                .padding(14.dp)
                        ) {
                            Column {
                                Text(
                                    text = "CladRb Econometric Interpretation",
                                    style = MaterialTheme.typography.titleMedium,
                                    color = if (adf.isStationary) EmeraldAccent else GoldAccent
                                )
                                Spacer(modifier = Modifier.height(6.dp))
                                Text(
                                    text = adf.interpretation,
                                    style = MaterialTheme.typography.bodyMedium,
                                    color = TextPrimary,
                                    lineHeight = 20.sp
                                )
                            }
                        }
                    }
                }
            }
        }

        // Tab Content 1: GARCH(1,1)
        if (uiState.selectedTab == 1) {
            item {
                val garch = uiState.garchResult
                if (garch != null) {
                    Column(verticalArrangement = Arrangement.spacedBy(14.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            MetricStatCard(
                                label = "Persistence (α+β)",
                                value = String.format("%.3f", garch.persistence),
                                subValue = "Target < 1.0",
                                valueColor = if (garch.persistence < 0.99) EmeraldAccent else CrimsonRisk,
                                modifier = Modifier.weight(1f)
                            )
                            MetricStatCard(
                                label = "Unconditional Vol",
                                value = "${String.format("%.1f", garch.unconditionalVol * 100)}%",
                                subValue = "Long-Term Asymptote",
                                valueColor = GoldAccent,
                                modifier = Modifier.weight(1f)
                            )
                            MetricStatCard(
                                label = "Shock Half-Life",
                                value = "${String.format("%.1f", garch.halfLifeShocks)} d",
                                subValue = "Volatility Memory",
                                valueColor = CyanAccent,
                                modifier = Modifier.weight(1f)
                            )
                        }

                        // Volatility Cone Chart
                        GarchVolatilityChart(
                            historicalVolatilities = garch.historicalVolatilities,
                            forecastedVolatilities = garch.forecastedVolatilities,
                            unconditionalVol = garch.unconditionalVol
                        )

                        // Parameter Tuning Sliders
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .background(SlateSurface, RoundedCornerShape(12.dp))
                                .border(1.dp, SlateBorder, RoundedCornerShape(12.dp))
                                .padding(14.dp)
                        ) {
                            Text(text = "Calibrate GARCH Parameters", style = MaterialTheme.typography.titleMedium, color = TextPrimary)
                            Spacer(modifier = Modifier.height(10.dp))

                            Text(text = "ARCH Parameter α (Reaction to Shocks): ${String.format("%.3f", uiState.garchAlpha)}", style = MaterialTheme.typography.labelSmall, color = CyanAccent)
                            Slider(
                                value = uiState.garchAlpha.toFloat(),
                                onValueChange = { viewModel.updateGarchParameters(it.toDouble(), uiState.garchBeta) },
                                valueRange = 0.01f..0.30f,
                                colors = SliderDefaults.colors(thumbColor = CyanAccent, activeTrackColor = CyanAccent)
                            )

                            Text(text = "GARCH Parameter β (Variance Persistence): ${String.format("%.3f", uiState.garchBeta)}", style = MaterialTheme.typography.labelSmall, color = EmeraldAccent)
                            Slider(
                                value = uiState.garchBeta.toFloat(),
                                onValueChange = { viewModel.updateGarchParameters(uiState.garchAlpha, it.toDouble()) },
                                valueRange = 0.50f..0.95f,
                                colors = SliderDefaults.colors(thumbColor = EmeraldAccent, activeTrackColor = EmeraldAccent)
                            )
                        }

                        MathFormulaCard(
                            title = "GARCH(1,1) Conditional Heteroskedasticity",
                            formula = "σ_t^2 = ω + α * ε_{t-1}^2 + β * σ_{t-1}^2",
                            explanation = "Models time-varying volatility clustering where large returns of either sign are followed by large returns, capturing fat-tailed stochastic volatility in financial assets."
                        )
                    }
                }
            }
        }

        // Tab Content 2: Cointegration & Pairs Trading
        if (uiState.selectedTab == 2) {
            item {
                val coint = uiState.cointResult
                if (coint != null) {
                    Column(verticalArrangement = Arrangement.spacedBy(14.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            MetricStatCard(
                                label = "Hedge Ratio (β)",
                                value = String.format("%.3f", coint.hedgeRatioBeta),
                                subValue = "A - β*B",
                                valueColor = GoldAccent,
                                modifier = Modifier.weight(1f)
                            )
                            MetricStatCard(
                                label = "Current Z-Score",
                                value = String.format("%.2f", coint.currentZScore),
                                subValue = if (kotlin.math.abs(coint.currentZScore) > 2.0) "Actionable Trigger" else "Normal Drift",
                                valueColor = if (coint.currentZScore < -1.8) EmeraldAccent else if (coint.currentZScore > 1.8) CrimsonRisk else CyanAccent,
                                modifier = Modifier.weight(1f)
                            )
                            MetricStatCard(
                                label = "O-U Half-Life",
                                value = "${String.format("%.1f", coint.halfLifeDays)} d",
                                subValue = "Mean Reversion",
                                valueColor = CyanAccent,
                                modifier = Modifier.weight(1f)
                            )
                        }

                        // Cointegration Spread Chart
                        CointegrationSpreadChart(
                            zScores = coint.zScoreSeries,
                            currentZScore = coint.currentZScore
                        )

                        // Trading Signal Box
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .background(SlateSurface, RoundedCornerShape(12.dp))
                                .border(1.dp, if (coint.currentZScore < -1.8) EmeraldAccent else if (coint.currentZScore > 1.8) CrimsonRisk else GoldAccent, RoundedCornerShape(12.dp))
                                .padding(14.dp)
                        ) {
                            Column {
                                Text(text = "CladRb Quantitative Execution Signal", style = MaterialTheme.typography.titleMedium, color = GoldAccent)
                                Spacer(modifier = Modifier.height(4.dp))
                                Text(text = coint.tradingSignal, style = MaterialTheme.typography.bodyLarge, color = TextPrimary, fontWeight = androidx.compose.ui.text.font.FontWeight.SemiBold)
                            }
                        }

                        MathFormulaCard(
                            title = "Engle-Granger Two-Step Cointegration",
                            formula = "Spread s_t = y_t - β * x_t,   Z_t = (s_t - μ_s) / σ_s",
                            explanation = "Identifies stationary linear combinations of two non-stationary asset price series, enabling market-neutral statistical arbitrage and pairs trading."
                        )
                    }
                }
            }
        }

        item {
            Spacer(modifier = Modifier.height(20.dp))
        }
    }
}
