package com.example.ui.screens.valuation

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
import androidx.compose.material.icons.filled.ShowChart
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Slider
import androidx.compose.material3.SliderDefaults
import androidx.compose.material3.SnackbarHostState
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
import com.example.ui.components.DcfSensitivityHeatmap
import com.example.ui.components.MathFormulaCard
import com.example.ui.components.MetricStatCard
import com.example.ui.theme.CrimsonRisk
import com.example.ui.theme.CyanAccent
import com.example.ui.theme.EmeraldAccent
import com.example.ui.theme.EmeraldLight
import com.example.ui.theme.GoldAccent
import com.example.ui.theme.ObsidianBg
import com.example.ui.theme.SlateBorder
import com.example.ui.theme.SlateSurface
import com.example.ui.theme.TextPrimary
import com.example.ui.theme.TextSecondary
import com.example.ui.theme.TextTertiary
import com.example.ui.viewmodel.ValuationViewModel

@Composable
fun DcfValuationScreen(
    viewModel: ValuationViewModel,
    snackbarHostState: SnackbarHostState,
    modifier: Modifier = Modifier
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val res = uiState.result
    val inp = uiState.inputs

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
                        text = "DCF Valuation Engine",
                        style = MaterialTheme.typography.headlineLarge,
                        color = TextPrimary
                    )
                    Text(
                        text = "Multi-Stage WACC CAPM & 2D Gordon Sensitivity",
                        style = MaterialTheme.typography.bodySmall,
                        color = EmeraldAccent
                    )
                }

                IconButton(
                    onClick = { viewModel.saveToDossier() },
                    modifier = Modifier
                        .size(42.dp)
                        .background(SlateSurface, RoundedCornerShape(10.dp))
                        .border(1.dp, GoldAccent.copy(alpha = 0.5f), RoundedCornerShape(10.dp))
                        .testTag("save_dcf_button")
                ) {
                    Icon(
                        imageVector = Icons.Default.BookmarkBorder,
                        contentDescription = "Save to Dossier",
                        tint = GoldAccent
                    )
                }
            }
        }

        // Intrinsic Valuation Verdict Card
        item {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(SlateSurface, RoundedCornerShape(14.dp))
                    .border(
                        1.5.dp,
                        if (res.marginOfSafetyPct > 0) EmeraldAccent else CrimsonRisk,
                        RoundedCornerShape(14.dp)
                    )
                    .padding(16.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text(
                            text = inp.companyName,
                            style = MaterialTheme.typography.titleMedium,
                            color = TextSecondary
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Row(verticalAlignment = Alignment.Bottom, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                            Text(
                                text = "$${String.format("%.2f", res.intrinsicValuePerShare)}",
                                style = MaterialTheme.typography.headlineLarge,
                                color = if (res.marginOfSafetyPct > 0) EmeraldLight else GoldAccent,
                                fontSize = 28.sp
                            )
                            Text(
                                text = "Fair Value",
                                style = MaterialTheme.typography.labelSmall,
                                color = TextTertiary,
                                modifier = Modifier.padding(bottom = 4.dp)
                            )
                        }
                    }

                    Column(horizontalAlignment = Alignment.End) {
                        Text(
                            text = "Market: $${String.format("%.2f", inp.currentSharePrice)}",
                            style = MaterialTheme.typography.labelSmall,
                            color = TextSecondary
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Box(
                            modifier = Modifier
                                .background(
                                    if (res.marginOfSafetyPct > 0) EmeraldAccent.copy(alpha = 0.2f) else CrimsonRisk.copy(alpha = 0.2f),
                                    RoundedCornerShape(6.dp)
                                )
                                .border(
                                    1.dp,
                                    if (res.marginOfSafetyPct > 0) EmeraldAccent else CrimsonRisk,
                                    RoundedCornerShape(6.dp)
                                )
                                .padding(horizontal = 10.dp, vertical = 4.dp)
                        ) {
                            Text(
                                text = "${if (res.marginOfSafetyPct >= 0) "+" else ""}${String.format("%.1f", res.marginOfSafetyPct)}% MoS",
                                style = MaterialTheme.typography.labelMedium,
                                color = if (res.marginOfSafetyPct > 0) EmeraldAccent else CrimsonRisk
                            )
                        }
                    }
                }
            }
        }

        // Key Cost of Capital Stats
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                MetricStatCard(
                    label = "Calculated WACC",
                    value = "${String.format("%.2f", res.calculatedWaccPct)}%",
                    subValue = "Discount Rate",
                    valueColor = GoldAccent,
                    modifier = Modifier.weight(1f)
                )
                MetricStatCard(
                    label = "Cost of Equity (r_e)",
                    value = "${String.format("%.2f", res.costOfEquityPct)}%",
                    subValue = "CAPM: β=${inp.beta}",
                    valueColor = CyanAccent,
                    modifier = Modifier.weight(1f)
                )
                MetricStatCard(
                    label = "Enterprise Value",
                    value = "$${String.format("%,.0f", res.enterpriseValueMillions)}M",
                    subValue = "Equity: $${String.format("%,.0f", res.equityValueMillions)}M",
                    valueColor = TextPrimary,
                    modifier = Modifier.weight(1f)
                )
            }
        }

        // 2D Sensitivity Heatmap
        item {
            DcfSensitivityHeatmap(
                matrix = res.sensitivityMatrix,
                currentPrice = inp.currentSharePrice
            )
        }

        // Interactive Parameter Controls
        item {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(SlateSurface, RoundedCornerShape(12.dp))
                    .border(1.dp, SlateBorder, RoundedCornerShape(12.dp))
                    .padding(14.dp),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                Text(
                    text = "Adjust Valuation Parameters",
                    style = MaterialTheme.typography.titleMedium,
                    color = TextPrimary
                )

                Text(
                    text = "Years 1–5 FCF Growth Rate: ${String.format("%.1f", inp.fcfGrowthYears1to5Pct)}%",
                    style = MaterialTheme.typography.labelSmall,
                    color = EmeraldAccent
                )
                Slider(
                    value = inp.fcfGrowthYears1to5Pct.toFloat(),
                    onValueChange = { viewModel.updateGrowthStage1(it.toDouble()) },
                    valueRange = 0f..40f,
                    colors = SliderDefaults.colors(thumbColor = EmeraldAccent, activeTrackColor = EmeraldAccent)
                )

                Text(
                    text = "Years 6–10 Terminal Drift Growth: ${String.format("%.1f", inp.fcfGrowthYears6to10Pct)}%",
                    style = MaterialTheme.typography.labelSmall,
                    color = CyanAccent
                )
                Slider(
                    value = inp.fcfGrowthYears6to10Pct.toFloat(),
                    onValueChange = { viewModel.updateGrowthStage2(it.toDouble()) },
                    valueRange = 0f..25f,
                    colors = SliderDefaults.colors(thumbColor = CyanAccent, activeTrackColor = CyanAccent)
                )

                Text(
                    text = "Asset Systematic Risk Beta (β): ${String.format("%.2f", inp.beta)}",
                    style = MaterialTheme.typography.labelSmall,
                    color = GoldAccent
                )
                Slider(
                    value = inp.beta.toFloat(),
                    onValueChange = { viewModel.updateBeta(it.toDouble()) },
                    valueRange = 0.5f..2.5f,
                    colors = SliderDefaults.colors(thumbColor = GoldAccent, activeTrackColor = GoldAccent)
                )

                Text(
                    text = "Perpetual Terminal Growth Rate (g): ${String.format("%.1f", inp.perpetualGrowthRatePct)}%",
                    style = MaterialTheme.typography.labelSmall,
                    color = TextPrimary
                )
                Slider(
                    value = inp.perpetualGrowthRatePct.toFloat(),
                    onValueChange = { viewModel.updatePerpetualGrowth(it.toDouble()) },
                    valueRange = 1.0f..4.0f,
                    colors = SliderDefaults.colors(thumbColor = TextPrimary, activeTrackColor = TextPrimary)
                )
            }
        }

        // Mathematical Breakdown Card
        item {
            MathFormulaCard(
                title = "Multi-Stage DCF & Gordon Terminal Value",
                formula = "EV = Σ [ FCF_t / (1+WACC)^t ] + [ FCF_10*(1+g) / (WACC - g) ] / (1+WACC)^10",
                explanation = "Discounts discrete forecast cash flows at WACC, adding terminal enterprise value discounted from year 10, then adjusting for net debt to yield intrinsic equity value per share."
            )
        }

        item {
            Spacer(modifier = Modifier.height(20.dp))
        }
    }
}
