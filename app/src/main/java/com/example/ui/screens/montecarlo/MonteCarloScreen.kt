package com.example.ui.screens.montecarlo

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Send
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.Bookmark
import androidx.compose.material.icons.filled.ContentCopy
import androidx.compose.material.icons.filled.QueryStats
import androidx.compose.material.icons.filled.Tune
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Slider
import androidx.compose.material3.SliderDefaults
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.TabRowDefaults
import androidx.compose.material3.TabRowDefaults.tabIndicatorOffset
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalClipboardManager
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.ui.components.CladRbAvatarHeader
import com.example.ui.components.MathFormulaCard
import com.example.ui.components.MetricStatCard
import com.example.ui.components.MonteCarloTrajectoryFanChart
import com.example.ui.components.ReturnDistributionHistogramChart
import com.example.ui.theme.CardGradientEnd
import com.example.ui.theme.CardGradientStart
import com.example.ui.theme.CrimsonRisk
import com.example.ui.theme.CyanAccent
import com.example.ui.theme.EmeraldAccent
import com.example.ui.theme.GoldAccent
import com.example.ui.theme.LilacAccent
import com.example.ui.theme.LilacDeep
import com.example.ui.theme.ObsidianBg
import com.example.ui.theme.PurpleGreeks
import com.example.ui.theme.SlateBorder
import com.example.ui.theme.SlateSurface
import com.example.ui.theme.SlateSurfaceVariant
import com.example.ui.theme.TextPrimary
import com.example.ui.theme.TextSecondary
import com.example.ui.theme.TextTertiary
import com.example.ui.theme.TextWhite
import com.example.ui.viewmodel.MonteCarloPreset
import com.example.ui.viewmodel.MonteCarloViewModel

@Composable
fun MonteCarloScreen(
    viewModel: MonteCarloViewModel,
    snackbarHostState: SnackbarHostState,
    modifier: Modifier = Modifier
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val res = uiState.result
    val inp = uiState.inputs
    val clipboardManager = LocalClipboardManager.current
    var customQuestionText by remember { mutableStateOf("") }
    var copyConfirmation by remember { mutableStateOf(false) }
    var showAdvancedSettings by remember { mutableStateOf(false) }

    LaunchedEffect(uiState.snackbarMessage) {
        uiState.snackbarMessage?.let {
            snackbarHostState.showSnackbar(it)
            viewModel.clearSnackbarMessage()
        }
    }

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .background(ObsidianBg)
            .padding(horizontal = 16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // 1. Header
        item {
            Spacer(modifier = Modifier.height(8.dp))
            CladRbAvatarHeader(
                subtitle = "Monte Carlo Stochastic Engine",
                activeQuote = "Stochastic distributions transform unknown futures into quantified odds."
            )
        }

        // 2. High-Level Outcome Summary Card
        item {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(
                        Brush.linearGradient(listOf(CardGradientStart, CardGradientEnd)),
                        RoundedCornerShape(24.dp)
                    )
                    .border(1.dp, LilacAccent.copy(alpha = 0.35f), RoundedCornerShape(24.dp))
                    .padding(18.dp)
            ) {
                Column {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column {
                            Text(
                                text = "PROBABILITY OF GOAL (\$${String.format("%,.0f", inp.targetGoal)})",
                                style = MaterialTheme.typography.labelSmall,
                                fontSize = 9.sp,
                                fontWeight = FontWeight.Bold,
                                letterSpacing = 1.sp,
                                color = LilacAccent
                            )
                            Row(verticalAlignment = Alignment.Bottom) {
                                Text(
                                    text = String.format("%.1f", res.probabilityOfSuccess),
                                    style = MaterialTheme.typography.headlineLarge,
                                    fontWeight = FontWeight.Bold,
                                    color = if (res.probabilityOfSuccess >= 70.0) EmeraldAccent else GoldAccent
                                )
                                Text(
                                    text = "%",
                                    style = MaterialTheme.typography.titleMedium,
                                    color = TextWhite,
                                    modifier = Modifier.padding(bottom = 4.dp, start = 2.dp)
                                )
                            }
                        }

                        // Success Indicator Badge
                        Box(
                            modifier = Modifier
                                .background(
                                    if (res.probabilityOfSuccess >= 70.0) EmeraldAccent.copy(alpha = 0.2f) else GoldAccent.copy(alpha = 0.2f),
                                    RoundedCornerShape(12.dp)
                                )
                                .border(
                                    1.dp,
                                    if (res.probabilityOfSuccess >= 70.0) EmeraldAccent else GoldAccent,
                                    RoundedCornerShape(12.dp)
                                )
                                .padding(horizontal = 10.dp, vertical = 6.dp)
                        ) {
                            Text(
                                text = if (res.probabilityOfSuccess >= 70.0) "High Confidence" else "Moderate Risk",
                                fontSize = 10.sp,
                                fontWeight = FontWeight.Bold,
                                color = if (res.probabilityOfSuccess >= 70.0) EmeraldAccent else GoldAccent
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(14.dp))

                    // Row of 3 key outputs
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        MetricStatCard(
                            label = "MEDIAN WEALTH",
                            value = "\$${String.format("%,.0f", res.medianTerminalWealth)}",
                            subValue = "CAGR: ${String.format("%.1f", res.medianCagr)}%",
                            valueColor = LilacAccent,
                            modifier = Modifier.weight(1f)
                        )
                        MetricStatCard(
                            label = "5TH % FLOOR",
                            value = "\$${String.format("%,.0f", res.p5TerminalWealth)}",
                            subValue = "Stress Floor",
                            valueColor = CrimsonRisk,
                            modifier = Modifier.weight(1f)
                        )
                        MetricStatCard(
                            label = "PROB. OF LOSS",
                            value = "${String.format("%.1f", res.probabilityOfLoss)}%",
                            subValue = "Total: \$${String.format("%,.0f", res.totalInvested)}",
                            valueColor = if (res.probabilityOfLoss < 10.0) EmeraldAccent else CrimsonRisk,
                            modifier = Modifier.weight(1f)
                        )
                    }
                }
            }
        }

        // 3. Visualization Mode Tabs
        item {
            val tabs = listOf("Trajectory Fan (Timeline)", "Return Distribution (PDF)")
            TabRow(
                selectedTabIndex = uiState.selectedViewTab,
                containerColor = SlateSurface,
                contentColor = LilacAccent,
                indicator = { tabPositions ->
                    TabRowDefaults.SecondaryIndicator(
                        Modifier.tabIndicatorOffset(tabPositions[uiState.selectedViewTab]),
                        color = LilacAccent
                    )
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(14.dp))
                    .border(1.dp, SlateBorder, RoundedCornerShape(14.dp))
            ) {
                tabs.forEachIndexed { index, title ->
                    Tab(
                        selected = uiState.selectedViewTab == index,
                        onClick = { viewModel.selectViewTab(index) },
                        text = {
                            Text(
                                text = title,
                                fontSize = 11.sp,
                                fontWeight = if (uiState.selectedViewTab == index) FontWeight.Bold else FontWeight.Normal,
                                color = if (uiState.selectedViewTab == index) LilacAccent else TextSecondary
                            )
                        },
                        modifier = Modifier.testTag("tab_mc_view_$index")
                    )
                }
            }
        }

        // 4. Interactive Chart Rendering
        item {
            if (uiState.selectedViewTab == 0) {
                MonteCarloTrajectoryFanChart(
                    trajectorySteps = res.trajectorySteps,
                    samplePaths = res.samplePaths,
                    targetGoal = inp.targetGoal,
                    totalInvested = res.totalInvested
                )
            } else {
                ReturnDistributionHistogramChart(
                    distributionBins = res.distributionBins,
                    targetGoal = inp.targetGoal,
                    totalInvested = res.totalInvested,
                    medianWealth = res.medianTerminalWealth
                )
            }
        }

        // 5. Preset Asset Allocation Templates
        item {
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                Text(
                    text = "STOCHASTIC ASSET ALLOCATION PRESETS",
                    style = MaterialTheme.typography.labelSmall,
                    fontSize = 9.sp,
                    fontWeight = FontWeight.Bold,
                    letterSpacing = 1.sp,
                    color = LilacAccent
                )

                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .horizontalScroll(rememberScrollState()),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    MonteCarloPreset.entries.forEach { preset ->
                        PresetCard(
                            preset = preset,
                            onClick = { viewModel.applyPreset(preset) },
                            modifier = Modifier.testTag("preset_${preset.name.lowercase()}")
                        )
                    }
                }
            }
        }

        // 6. Interactive Stochastic Inputs Panel
        item {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(SlateSurface, RoundedCornerShape(20.dp))
                    .border(1.dp, SlateBorder, RoundedCornerShape(20.dp))
                    .padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(14.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        Icon(
                            imageVector = Icons.Default.Tune,
                            contentDescription = null,
                            tint = LilacAccent,
                            modifier = Modifier.size(18.dp)
                        )
                        Text(
                            text = "Stochastic Inputs & Assumptions",
                            style = MaterialTheme.typography.titleSmall,
                            fontWeight = FontWeight.Bold,
                            color = TextWhite
                        )
                    }

                    if (uiState.isSimulating) {
                        CircularProgressIndicator(
                            modifier = Modifier.size(16.dp),
                            color = LilacAccent,
                            strokeWidth = 2.dp
                        )
                    }
                }

                // Initial Capital Slider
                StochasticSliderRow(
                    label = "Initial Capital",
                    valueText = "\$${String.format("%,.0f", inp.initialCapital)}",
                    value = inp.initialCapital.toFloat(),
                    valueRange = 10000f..500000f,
                    steps = 49,
                    onValueChange = { viewModel.updateInitialCapital(it.toDouble()) }
                )

                // Monthly Contribution
                StochasticSliderRow(
                    label = "Monthly Contribution",
                    valueText = "\$${String.format("%,.0f", inp.monthlyContribution)}/mo",
                    value = inp.monthlyContribution.toFloat(),
                    valueRange = 0f..3000f,
                    steps = 30,
                    onValueChange = { viewModel.updateMonthlyContribution(it.toDouble()) }
                )

                // Expected Return
                StochasticSliderRow(
                    label = "Expected Annual Return (μ)",
                    valueText = "${String.format("%.1f", inp.expectedAnnualReturn * 100)}%",
                    value = (inp.expectedAnnualReturn * 100).toFloat(),
                    valueRange = 3f..25f,
                    steps = 22,
                    onValueChange = { viewModel.updateExpectedReturn(it.toDouble()) }
                )

                // Annual Volatility
                StochasticSliderRow(
                    label = "Annual Volatility (σ)",
                    valueText = "${String.format("%.1f", inp.annualVolatility * 100)}%",
                    value = (inp.annualVolatility * 100).toFloat(),
                    valueRange = 5f..40f,
                    steps = 35,
                    onValueChange = { viewModel.updateVolatility(it.toDouble()) }
                )

                // Horizon Years
                StochasticSliderRow(
                    label = "Investment Horizon",
                    valueText = "${inp.horizonYears} Years",
                    value = inp.horizonYears.toFloat(),
                    valueRange = 1f..30f,
                    steps = 29,
                    onValueChange = { viewModel.updateHorizonYears(it.toInt()) }
                )

                // Target Goal
                StochasticSliderRow(
                    label = "Target Wealth Goal",
                    valueText = "\$${String.format("%,.0f", inp.targetGoal)}",
                    value = inp.targetGoal.toFloat(),
                    valueRange = 50000f..1500000f,
                    steps = 29,
                    onValueChange = { viewModel.updateTargetGoal(it.toDouble()) }
                )

                // Advanced Toggles (Fat Tails & Inflation)
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text(
                            text = "Student-t Fat Tails (ν=5)",
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Medium,
                            color = TextPrimary
                        )
                        Text(
                            text = "Models black-swan market shocks",
                            fontSize = 10.sp,
                            color = TextTertiary
                        )
                    }
                    Switch(
                        checked = inp.useFatTailsStudentT,
                        onCheckedChange = { viewModel.toggleFatTails(it) },
                        colors = SwitchDefaults.colors(
                            checkedThumbColor = LilacDeep,
                            checkedTrackColor = LilacAccent
                        )
                    )
                }

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text(
                            text = "Real Purchasing Power",
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Medium,
                            color = TextPrimary
                        )
                        Text(
                            text = "Deflate by ${inp.annualInflationRate * 100}% inflation",
                            fontSize = 10.sp,
                            color = TextTertiary
                        )
                    }
                    Switch(
                        checked = inp.adjustForInflation,
                        onCheckedChange = { viewModel.toggleInflationAdjustment(it) },
                        colors = SwitchDefaults.colors(
                            checkedThumbColor = LilacDeep,
                            checkedTrackColor = LilacAccent
                        )
                    )
                }
            }
        }

        // 7. Stochastic Formulation Card
        item {
            MathFormulaCard(
                title = "Geometric Brownian Motion (Ito's Lemma)",
                formula = "dS_t = μ S_t dt + σ S_t dW_t \nS_T = S_0 exp((μ - 0.5 σ²)T + σ √T Z)",
                explanation = "Continuous compounding drag (-1/2 σ²) reduces median compound geometric growth below arithmetic mean return. Managing volatility directly expands terminal median wealth."
            )
        }

        // 8. CladRb Quantitative Dossier Insight
        item {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(SlateSurface, RoundedCornerShape(20.dp))
                    .border(1.dp, SlateBorder, RoundedCornerShape(20.dp))
                    .padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        Box(
                            modifier = Modifier
                                .size(32.dp)
                                .clip(CircleShape)
                                .background(LilacAccent),
                            contentAlignment = Alignment.Center
                        ) {
                            Text("C", color = LilacDeep, fontWeight = FontWeight.Bold, fontSize = 16.sp)
                        }
                        Text(
                            text = "CladRb Stochastic Verdict",
                            style = MaterialTheme.typography.titleSmall,
                            fontWeight = FontWeight.Bold,
                            color = TextWhite
                        )
                    }

                    IconButton(
                        onClick = {
                            clipboardManager.setText(AnnotatedString(res.cladrbInsight))
                            copyConfirmation = true
                        }
                    ) {
                        Icon(
                            imageVector = Icons.Default.ContentCopy,
                            contentDescription = "Copy",
                            tint = if (copyConfirmation) EmeraldAccent else TextSecondary,
                            modifier = Modifier.size(18.dp)
                        )
                    }
                }

                Text(
                    text = res.cladrbInsight,
                    style = MaterialTheme.typography.bodySmall,
                    color = TextPrimary,
                    lineHeight = 18.sp
                )

                Button(
                    onClick = { viewModel.saveReportToDossier() },
                    colors = ButtonDefaults.buttonColors(containerColor = LilacAccent),
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("save_monte_carlo_dossier_btn")
                ) {
                    Icon(
                        imageVector = Icons.Default.Bookmark,
                        contentDescription = null,
                        tint = LilacDeep,
                        modifier = Modifier.size(16.dp)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = "Save Monte Carlo to Dossier",
                        color = LilacDeep,
                        fontWeight = FontWeight.Bold,
                        fontSize = 12.sp
                    )
                }
            }
        }

        // 9. AI Scenario Deep Dive
        item {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(SlateSurface, RoundedCornerShape(20.dp))
                    .border(1.dp, SlateBorder, RoundedCornerShape(20.dp))
                    .padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    Icon(
                        imageVector = Icons.Default.AutoAwesome,
                        contentDescription = null,
                        tint = LilacAccent,
                        modifier = Modifier.size(18.dp)
                    )
                    Text(
                        text = "Consult CladRb on Tail Risk Scenarios",
                        style = MaterialTheme.typography.titleSmall,
                        fontWeight = FontWeight.SemiBold,
                        color = TextWhite
                    )
                }

                Text(
                    text = "Request custom sequence-of-returns stress testing or asset allocation sensitivity modeling.",
                    fontSize = 11.sp,
                    color = TextSecondary
                )

                // Prompt Chips
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .horizontalScroll(rememberScrollState()),
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    val samplePrompts = listOf(
                        "Stress test 2008-style early drawdown",
                        "Evaluate increasing monthly savings vs volatility",
                        "Sequence-of-returns retirement risk"
                    )
                    samplePrompts.forEach { prompt ->
                        Box(
                            modifier = Modifier
                                .background(SlateSurfaceVariant, RoundedCornerShape(12.dp))
                                .border(1.dp, SlateBorder, RoundedCornerShape(12.dp))
                                .clickable {
                                    customQuestionText = prompt
                                    viewModel.queryCladRbDeepDive(prompt)
                                }
                                .padding(horizontal = 10.dp, vertical = 6.dp)
                        ) {
                            Text(
                                text = prompt,
                                fontSize = 10.sp,
                                color = LilacAccent,
                                fontWeight = FontWeight.Medium
                            )
                        }
                    }
                }

                // Query input
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    OutlinedTextField(
                        value = customQuestionText,
                        onValueChange = { customQuestionText = it },
                        placeholder = {
                            Text("Ask CladRb a stochastic inquiry...", fontSize = 12.sp, color = TextTertiary)
                        },
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedContainerColor = SlateSurfaceVariant,
                            unfocusedContainerColor = SlateSurfaceVariant,
                            focusedBorderColor = LilacAccent,
                            unfocusedBorderColor = SlateBorder,
                            focusedTextColor = TextWhite,
                            unfocusedTextColor = TextWhite
                        ),
                        shape = RoundedCornerShape(14.dp),
                        modifier = Modifier.weight(1f)
                    )

                    Spacer(modifier = Modifier.width(8.dp))

                    IconButton(
                        onClick = {
                            if (customQuestionText.isNotBlank()) {
                                viewModel.queryCladRbDeepDive(customQuestionText)
                            }
                        },
                        enabled = customQuestionText.isNotBlank() && !uiState.isAiGenerating,
                        modifier = Modifier
                            .size(44.dp)
                            .background(
                                if (customQuestionText.isNotBlank()) LilacAccent else SlateSurfaceVariant,
                                CircleShape
                            )
                    ) {
                        if (uiState.isAiGenerating) {
                            CircularProgressIndicator(modifier = Modifier.size(18.dp), color = LilacDeep, strokeWidth = 2.dp)
                        } else {
                            Icon(
                                imageVector = Icons.AutoMirrored.Filled.Send,
                                contentDescription = "Send",
                                tint = if (customQuestionText.isNotBlank()) LilacDeep else TextTertiary,
                                modifier = Modifier.size(18.dp)
                            )
                        }
                    }
                }

                uiState.customAiInsight?.let { insight ->
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(SlateSurfaceVariant, RoundedCornerShape(12.dp))
                            .border(1.dp, LilacAccent.copy(alpha = 0.4f), RoundedCornerShape(12.dp))
                            .padding(12.dp)
                    ) {
                        Text(
                            text = insight,
                            style = MaterialTheme.typography.bodySmall,
                            color = TextPrimary,
                            lineHeight = 18.sp
                        )
                    }
                }
            }
        }

        item {
            Spacer(modifier = Modifier.height(24.dp))
        }
    }
}

@Composable
private fun StochasticSliderRow(
    label: String,
    valueText: String,
    value: Float,
    valueRange: ClosedFloatingPointRange<Float>,
    steps: Int,
    onValueChange: (Float) -> Unit
) {
    Column {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(label, fontSize = 11.sp, color = TextSecondary)
            Text(valueText, fontSize = 11.sp, fontWeight = FontWeight.Bold, color = LilacAccent)
        }
        Slider(
            value = value,
            onValueChange = onValueChange,
            valueRange = valueRange,
            steps = steps,
            colors = SliderDefaults.colors(
                thumbColor = LilacAccent,
                activeTrackColor = LilacAccent,
                inactiveTrackColor = SlateSurfaceVariant
            )
        )
    }
}

@Composable
private fun PresetCard(
    preset: MonteCarloPreset,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .width(150.dp)
            .background(SlateSurfaceVariant, RoundedCornerShape(14.dp))
            .border(1.dp, SlateBorder, RoundedCornerShape(14.dp))
            .clickable(onClick = onClick)
            .padding(10.dp)
    ) {
        Text(
            text = preset.title,
            style = MaterialTheme.typography.labelMedium,
            fontWeight = FontWeight.Bold,
            color = LilacAccent
        )
        Text(
            text = "E[R]: ${(preset.expectedReturn * 100).toInt()}% • Vol: ${(preset.volatility * 100).toInt()}%",
            fontSize = 9.sp,
            color = TextSecondary,
            modifier = Modifier.padding(vertical = 2.dp)
        )
        Text(
            text = preset.description,
            fontSize = 8.5.sp,
            color = TextTertiary,
            maxLines = 2,
            lineHeight = 11.sp
        )
    }
}
