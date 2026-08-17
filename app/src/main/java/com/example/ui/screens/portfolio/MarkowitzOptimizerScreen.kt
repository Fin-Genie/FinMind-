package com.example.ui.screens.portfolio

import androidx.compose.animation.AnimatedVisibility
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
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.BookmarkBorder
import androidx.compose.material.icons.filled.DeleteOutline
import androidx.compose.material.icons.filled.Tune
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Slider
import androidx.compose.material3.SliderDefaults
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.domain.math.Asset
import com.example.ui.components.EfficientFrontierChart
import com.example.ui.components.MathFormulaCard
import com.example.ui.components.MetricStatCard
import com.example.ui.theme.CrimsonRisk
import com.example.ui.theme.CyanAccent
import com.example.ui.theme.EmeraldAccent
import com.example.ui.theme.GoldAccent
import com.example.ui.theme.ObsidianBg
import com.example.ui.theme.SlateBorder
import com.example.ui.theme.SlateSurface
import com.example.ui.theme.SlateSurfaceVariant
import com.example.ui.theme.TextPrimary
import com.example.ui.theme.TextSecondary
import com.example.ui.theme.TextTertiary
import com.example.ui.viewmodel.MarkowitzViewModel
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MarkowitzOptimizerScreen(
    viewModel: MarkowitzViewModel,
    snackbarHostState: SnackbarHostState,
    modifier: Modifier = Modifier
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val scope = rememberCoroutineScope()
    var showAddAssetDialog by remember { mutableStateOf(false) }

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
                        text = "Markowitz Frontier Lab",
                        style = MaterialTheme.typography.headlineLarge,
                        color = TextPrimary
                    )
                    Text(
                        text = "Mean-Variance Quadratic Optimization & Tangency Allocation",
                        style = MaterialTheme.typography.bodySmall,
                        color = CyanAccent
                    )
                }

                IconButton(
                    onClick = { viewModel.saveToDossier() },
                    modifier = Modifier
                        .size(42.dp)
                        .background(SlateSurface, RoundedCornerShape(10.dp))
                        .border(1.dp, GoldAccent.copy(alpha = 0.5f), RoundedCornerShape(10.dp))
                        .testTag("save_markowitz_button")
                ) {
                    Icon(
                        imageVector = Icons.Default.BookmarkBorder,
                        contentDescription = "Save to Dossier",
                        tint = GoldAccent
                    )
                }
            }
        }

        // Metrics Row
        item {
            val res = uiState.result
            if (res != null) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    MetricStatCard(
                        label = "Tangency Sharpe",
                        value = String.format("%.2f", res.maxSharpePortfolio.sharpeRatio),
                        subValue = "Max Risk-Adjusted",
                        valueColor = GoldAccent,
                        modifier = Modifier.weight(1f)
                    )
                    MetricStatCard(
                        label = "Exp. Return (E[R])",
                        value = "${String.format("%.1f", res.maxSharpePortfolio.expectedReturn * 100)}%",
                        subValue = "Annualized",
                        valueColor = EmeraldAccent,
                        modifier = Modifier.weight(1f)
                    )
                    MetricStatCard(
                        label = "Portfolio Vol (σ)",
                        value = "${String.format("%.1f", res.maxSharpePortfolio.volatility * 100)}%",
                        subValue = "Downside: ${String.format("%.1f", res.minVariancePortfolio.volatility * 100)}%",
                        valueColor = CyanAccent,
                        modifier = Modifier.weight(1f)
                    )
                }
            }
        }

        // Interactive Canvas Chart
        item {
            val res = uiState.result
            if (res != null) {
                EfficientFrontierChart(
                    simulatedPoints = res.simulatedPoints,
                    frontierCurve = res.efficientFrontierCurve,
                    currentPoint = res.currentPortfolio,
                    tangencyPoint = res.maxSharpePortfolio,
                    minVariancePoint = res.minVariancePortfolio
                )
            }
        }

        // Action Buttons to Apply Optimal Allocations
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                Button(
                    onClick = { viewModel.applyOptimalWeights(useMaxSharpe = true) },
                    modifier = Modifier
                        .weight(1f)
                        .testTag("apply_max_sharpe_button"),
                    colors = ButtonDefaults.buttonColors(containerColor = GoldAccent, contentColor = ObsidianBg),
                    shape = RoundedCornerShape(10.dp)
                ) {
                    Icon(imageVector = Icons.Default.AutoAwesome, contentDescription = null, modifier = Modifier.size(16.dp))
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(text = "Max Sharpe Tangency", style = MaterialTheme.typography.labelMedium)
                }

                OutlinedButton(
                    onClick = { viewModel.applyOptimalWeights(useMaxSharpe = false) },
                    modifier = Modifier
                        .weight(1f)
                        .testTag("apply_min_vol_button"),
                    colors = ButtonDefaults.outlinedButtonColors(contentColor = EmeraldAccent),
                    border = androidx.compose.foundation.BorderStroke(1.dp, EmeraldAccent),
                    shape = RoundedCornerShape(10.dp)
                ) {
                    Text(text = "Min Volatility", style = MaterialTheme.typography.labelMedium)
                }
            }
        }

        // Mathematical Formula Breakdown
        item {
            MathFormulaCard(
                title = "Markowitz Quadratic Optimization Formula",
                formula = "max_w (w^T μ - r_f) / sqrt(w^T Σ w)  s.t. Σ w_i = 1, w_i ≥ 0",
                explanation = "Calculates the tangency point on the Efficient Frontier by maximizing excess return per unit of portfolio variance, weighted by the covariance matrix Σ."
            )
        }

        // Asset Allocation Sliders Header
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Asset Basket & Weight Tuning",
                    style = MaterialTheme.typography.titleMedium,
                    color = TextPrimary
                )
                TextButton(onClick = { showAddAssetDialog = true }) {
                    Icon(imageVector = Icons.Default.Add, contentDescription = null, tint = GoldAccent, modifier = Modifier.size(16.dp))
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(text = "Add Asset", color = GoldAccent, style = MaterialTheme.typography.labelMedium)
                }
            }
        }

        // Asset Cards List
        items(uiState.assets, key = { it.id }) { asset ->
            AssetWeightCard(
                asset = asset,
                onWeightChanged = { viewModel.updateAssetWeight(asset.id, it) },
                onRemove = { viewModel.removeAsset(asset.id) },
                canRemove = uiState.assets.size > 2
            )
        }

        item {
            Spacer(modifier = Modifier.height(20.dp))
        }
    }

    if (showAddAssetDialog) {
        AddAssetDialog(
            onDismiss = { showAddAssetDialog = false },
            onAdd = { ticker, name, ret, vol ->
                viewModel.addCustomAsset(ticker, name, ret, vol)
                showAddAssetDialog = false
            }
        )
    }
}

@Composable
fun AssetWeightCard(
    asset: Asset,
    onWeightChanged: (Double) -> Unit,
    onRemove: () -> Unit,
    canRemove: Boolean,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .background(SlateSurface, RoundedCornerShape(12.dp))
            .border(1.dp, SlateBorder, RoundedCornerShape(12.dp))
            .padding(14.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column {
                Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    Text(text = asset.ticker, style = MaterialTheme.typography.titleMedium, color = GoldAccent)
                    Text(text = "(${String.format("%.1f", asset.weight * 100)}%)", style = MaterialTheme.typography.labelSmall, color = TextPrimary)
                }
                Text(text = asset.name, style = MaterialTheme.typography.bodySmall, color = TextSecondary)
            }

            if (canRemove) {
                IconButton(onClick = onRemove, modifier = Modifier.size(32.dp)) {
                    Icon(imageVector = Icons.Default.DeleteOutline, contentDescription = "Remove Asset", tint = TextTertiary)
                }
            }
        }

        Spacer(modifier = Modifier.height(6.dp))

        // Weight Slider
        Slider(
            value = asset.weight.toFloat(),
            onValueChange = { onWeightChanged(it.toDouble()) },
            valueRange = 0f..1f,
            colors = SliderDefaults.colors(
                thumbColor = GoldAccent,
                activeTrackColor = GoldAccent,
                inactiveTrackColor = SlateBorder
            ),
            modifier = Modifier.fillMaxWidth()
        )

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(text = "E[R]: ${String.format("%.1f", asset.expectedReturn * 100)}%", style = MaterialTheme.typography.labelSmall, color = EmeraldAccent)
            Text(text = "Vol (σ): ${String.format("%.1f", asset.volatility * 100)}%", style = MaterialTheme.typography.labelSmall, color = CyanAccent)
        }
    }
}

@Composable
fun AddAssetDialog(
    onDismiss: () -> Unit,
    onAdd: (ticker: String, name: String, expectedReturnPct: Double, volatilityPct: Double) -> Unit
) {
    var ticker by remember { mutableStateOf("BTC") }
    var name by remember { mutableStateOf("Bitcoin Digital Store of Value") }
    var retText by remember { mutableStateOf("35.0") }
    var volText by remember { mutableStateOf("55.0") }

    AlertDialog(
        onDismissRequest = onDismiss,
        containerColor = SlateSurface,
        title = { Text(text = "Add Custom Asset", style = MaterialTheme.typography.titleLarge, color = TextPrimary) },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                OutlinedTextField(
                    value = ticker,
                    onValueChange = { ticker = it },
                    label = { Text("Ticker (e.g. BTC, AAPL)") },
                    colors = OutlinedTextFieldDefaults.colors(focusedBorderColor = GoldAccent, unfocusedBorderColor = SlateBorder)
                )
                OutlinedTextField(
                    value = name,
                    onValueChange = { name = it },
                    label = { Text("Asset Description") },
                    colors = OutlinedTextFieldDefaults.colors(focusedBorderColor = GoldAccent, unfocusedBorderColor = SlateBorder)
                )
                OutlinedTextField(
                    value = retText,
                    onValueChange = { retText = it },
                    label = { Text("Expected Annual Return (%)") },
                    colors = OutlinedTextFieldDefaults.colors(focusedBorderColor = GoldAccent, unfocusedBorderColor = SlateBorder)
                )
                OutlinedTextField(
                    value = volText,
                    onValueChange = { volText = it },
                    label = { Text("Expected Volatility σ (%)") },
                    colors = OutlinedTextFieldDefaults.colors(focusedBorderColor = GoldAccent, unfocusedBorderColor = SlateBorder)
                )
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    val r = retText.toDoubleOrNull() ?: 12.0
                    val v = volText.toDoubleOrNull() ?: 20.0
                    onAdd(ticker, name, r, v)
                },
                colors = ButtonDefaults.buttonColors(containerColor = GoldAccent, contentColor = ObsidianBg)
            ) {
                Text("Add to Basket")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) { Text("Cancel", color = TextTertiary) }
        }
    )
}
