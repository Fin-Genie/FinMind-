package com.example.ui.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.selection.SelectionContainer
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Bookmark
import androidx.compose.material.icons.filled.ContentCopy
import androidx.compose.material.icons.filled.ExpandLess
import androidx.compose.material.icons.filled.ExpandMore
import androidx.compose.material.icons.filled.Psychology
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.TabRowDefaults
import androidx.compose.material3.TabRowDefaults.tabIndicatorOffset
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.platform.LocalClipboardManager
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.domain.macro.CladRbMacroCommentary
import com.example.domain.macro.MacroRegime
import com.example.ui.theme.CardGradientEnd
import com.example.ui.theme.CardGradientStart
import com.example.ui.theme.CrimsonRisk
import com.example.ui.theme.CyanAccent
import com.example.ui.theme.EmeraldAccent
import com.example.ui.theme.GoldAccent
import com.example.ui.theme.LilacAccent
import com.example.ui.theme.LilacDeep
import com.example.ui.theme.SlateBorder
import com.example.ui.theme.SlateSurface
import com.example.ui.theme.SlateSurfaceVariant
import com.example.ui.theme.TextPrimary
import com.example.ui.theme.TextSecondary
import com.example.ui.theme.TextTertiary
import com.example.ui.theme.TextWhite

/**
 * Rich, CladRb-persona Card presenting quantitative commentary on macroeconomic trends & market implications
 */
@Composable
fun CladRbMacroAdvisorCard(
    commentary: CladRbMacroCommentary,
    onSaveToDossier: () -> Unit,
    modifier: Modifier = Modifier
) {
    val clipboardManager = LocalClipboardManager.current
    var selectedSectionTab by remember { mutableIntStateOf(0) }
    var isExpanded by remember { mutableStateOf(true) }
    var copyConfirmation by remember { mutableStateOf(false) }

    val regimeColor = when (commentary.regime) {
        MacroRegime.GOLDILOCKS -> EmeraldAccent
        MacroRegime.REFLATION -> GoldAccent
        MacroRegime.STAGFLATION -> CrimsonRisk
        MacroRegime.SLOWDOWN -> CyanAccent
    }

    Column(
        modifier = modifier
            .fillMaxWidth()
            .background(SlateSurface, RoundedCornerShape(24.dp))
            .border(1.dp, SlateBorder, RoundedCornerShape(24.dp))
            .padding(18.dp)
    ) {
        // Top CladRb Badge Header
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                // CladRb Monogram Emblem
                Box(
                    modifier = Modifier
                        .size(38.dp)
                        .clip(CircleShape)
                        .background(LilacAccent),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "C",
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold,
                        color = LilacDeep
                    )
                }

                Column {
                    Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                        Text(
                            text = "CladRb Macro Intelligence",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            color = TextWhite
                        )
                        Text(commentary.flag, fontSize = 16.sp)
                    }
                    Text(
                        text = "QUANTITATIVE MARKET IMPLICATIONS",
                        style = MaterialTheme.typography.labelSmall,
                        fontSize = 9.sp,
                        fontWeight = FontWeight.Bold,
                        letterSpacing = 1.sp,
                        color = LilacAccent
                    )
                }
            }

            // Expand/Collapse toggle
            Box(
                modifier = Modifier
                    .size(32.dp)
                    .background(SlateSurfaceVariant, CircleShape)
                    .clickable { isExpanded = !isExpanded },
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = if (isExpanded) Icons.Default.ExpandLess else Icons.Default.ExpandMore,
                    contentDescription = null,
                    tint = TextSecondary,
                    modifier = Modifier.size(18.dp)
                )
            }
        }

        Spacer(modifier = Modifier.height(14.dp))

        // Regime Classification Gradient Banner
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .background(
                    Brush.linearGradient(
                        listOf(CardGradientStart, CardGradientEnd)
                    ),
                    RoundedCornerShape(16.dp)
                )
                .border(1.dp, regimeColor.copy(alpha = 0.35f), RoundedCornerShape(16.dp))
                .padding(14.dp)
        ) {
            Column {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                        Box(modifier = Modifier.size(8.dp).background(regimeColor, CircleShape))
                        Text(
                            text = commentary.regime.title.uppercase(),
                            style = MaterialTheme.typography.labelSmall,
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Bold,
                            color = regimeColor
                        )
                    }

                    Text(
                        text = commentary.stanceBadge,
                        style = MaterialTheme.typography.labelSmall,
                        fontSize = 9.sp,
                        color = TextWhite.copy(alpha = 0.9f)
                    )
                }

                Spacer(modifier = Modifier.height(4.dp))

                Text(
                    text = commentary.regime.subtitle,
                    style = MaterialTheme.typography.bodySmall,
                    fontWeight = FontWeight.Medium,
                    color = TextWhite
                )

                Spacer(modifier = Modifier.height(10.dp))

                // Quantitative Metrics Strip
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    MetricChip(
                        label = "REAL RATE (r)",
                        value = "${if (commentary.realRate >= 0) "+" else ""}${commentary.realRate}%",
                        color = if (commentary.realRate >= 0) EmeraldAccent else CrimsonRisk,
                        modifier = Modifier.weight(1f)
                    )
                    MetricChip(
                        label = "TAYLOR RULE",
                        value = "${commentary.taylorRuleBenchmark}%",
                        color = LilacAccent,
                        modifier = Modifier.weight(1f)
                    )
                    MetricChip(
                        label = "10Y-2Y SPREAD",
                        value = "${commentary.yieldCurveSpreadBps.toInt()} bps",
                        color = if (commentary.yieldCurveSpreadBps >= 0) CyanAccent else CrimsonRisk,
                        modifier = Modifier.weight(1f)
                    )
                }
            }
        }

        AnimatedVisibility(visible = isExpanded) {
            Column {
                Spacer(modifier = Modifier.height(14.dp))

                // Section Tabs: [Executive, Asset Classes, Tactical Playbook]
                val tabs = listOf("Macro Stance", "Asset Classes", "Tactical Playbook")
                TabRow(
                    selectedTabIndex = selectedSectionTab,
                    containerColor = SlateSurfaceVariant,
                    contentColor = LilacAccent,
                    indicator = { tabPositions ->
                        TabRowDefaults.SecondaryIndicator(
                            Modifier.tabIndicatorOffset(tabPositions[selectedSectionTab]),
                            color = LilacAccent
                        )
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(12.dp))
                ) {
                    tabs.forEachIndexed { index, title ->
                        Tab(
                            selected = selectedSectionTab == index,
                            onClick = { selectedSectionTab = index },
                            text = {
                                Text(
                                    text = title,
                                    fontSize = 11.sp,
                                    fontWeight = if (selectedSectionTab == index) FontWeight.Bold else FontWeight.Normal,
                                    color = if (selectedSectionTab == index) LilacAccent else TextSecondary
                                )
                            }
                        )
                    }
                }

                Spacer(modifier = Modifier.height(14.dp))

                // Section Content
                SelectionContainer {
                    when (selectedSectionTab) {
                        0 -> {
                            // Macro Stance & Executive Summary
                            Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                                Text(
                                    text = commentary.executiveSummary,
                                    style = MaterialTheme.typography.bodyMedium,
                                    color = TextPrimary,
                                    lineHeight = 20.sp
                                )

                                Box(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .background(SlateSurfaceVariant, RoundedCornerShape(12.dp))
                                        .border(1.dp, SlateBorder, RoundedCornerShape(12.dp))
                                        .padding(12.dp)
                                ) {
                                    Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                                        Text(
                                            text = "TAYLOR RULE FORMULATION",
                                            style = MaterialTheme.typography.labelSmall,
                                            fontSize = 9.sp,
                                            fontWeight = FontWeight.Bold,
                                            color = LilacAccent
                                        )
                                        Text(
                                            text = "i* = r* + π + 0.5(π - π*) + 0.5(y - y_pot)",
                                            fontFamily = androidx.compose.ui.text.font.FontFamily.Monospace,
                                            fontSize = 11.sp,
                                            color = TextWhite
                                        )
                                        Text(
                                            text = "Prescribed rate benchmark is ${commentary.taylorRuleBenchmark}%. Actual policy is currently ${if (commentary.taylorRuleGap >= 0) "+${commentary.taylorRuleGap}% tighter" else "${commentary.taylorRuleGap}% looser"} than neutral equilibrium.",
                                            fontSize = 11.sp,
                                            color = TextSecondary
                                        )
                                    }
                                }
                            }
                        }
                        1 -> {
                            // Asset Classes Transmission
                            Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                                AssetClassRow(
                                    emoji = "📈",
                                    assetName = "Equities & Valuation Multiples",
                                    implication = commentary.equitiesImplication,
                                    accentColor = LilacAccent
                                )
                                AssetClassRow(
                                    emoji = "🏛️",
                                    assetName = "Fixed Income & Duration",
                                    implication = commentary.fixedIncomeImplication,
                                    accentColor = CyanAccent
                                )
                                AssetClassRow(
                                    emoji = "💱",
                                    assetName = "Foreign Exchange & Carry",
                                    implication = commentary.fxImplication,
                                    accentColor = EmeraldAccent
                                )
                                AssetClassRow(
                                    emoji = "🪙",
                                    assetName = "Commodities & Real Assets",
                                    implication = commentary.commoditiesImplication,
                                    accentColor = GoldAccent
                                )
                            }
                        }
                        2 -> {
                            // Tactical Playbook
                            Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                                Text(
                                    text = "CladRb Model Portfolio Positioning",
                                    style = MaterialTheme.typography.titleSmall,
                                    fontWeight = FontWeight.SemiBold,
                                    color = TextWhite
                                )
                                Text(
                                    text = commentary.tacticalPortfolioPlaybook,
                                    style = MaterialTheme.typography.bodyMedium,
                                    color = TextPrimary,
                                    lineHeight = 20.sp
                                )
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Action Buttons: Save to Dossier & Copy
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    Button(
                        onClick = onSaveToDossier,
                        colors = ButtonDefaults.buttonColors(containerColor = LilacAccent),
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier
                            .weight(1f)
                            .testTag("save_macro_dossier_btn")
                    ) {
                        Icon(
                            imageVector = Icons.Default.Bookmark,
                            contentDescription = null,
                            tint = LilacDeep,
                            modifier = Modifier.size(16.dp)
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = "Save to Dossier",
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            color = LilacDeep
                        )
                    }

                    OutlinedButton(
                        onClick = {
                            val fullText = buildString {
                                append("### CladRb Macro Commentary: ${commentary.countryName} (${commentary.flag})\n")
                                append("Regime: ${commentary.regime.title}\n")
                                append("Real Rate: ${commentary.realRate}% | Taylor Benchmark: ${commentary.taylorRuleBenchmark}%\n\n")
                                append(commentary.executiveSummary)
                                append("\n\nEquities: ${commentary.equitiesImplication}\n")
                                append("Fixed Income: ${commentary.fixedIncomeImplication}\n")
                                append("FX: ${commentary.fxImplication}\n")
                                append("Commodities: ${commentary.commoditiesImplication}\n\n")
                                append("Tactical Playbook:\n${commentary.tacticalPortfolioPlaybook}")
                            }
                            clipboardManager.setText(AnnotatedString(fullText))
                            copyConfirmation = true
                        },
                        shape = RoundedCornerShape(12.dp),
                        border = androidx.compose.foundation.BorderStroke(1.dp, SlateBorder),
                        modifier = Modifier.testTag("copy_macro_commentary_btn")
                    ) {
                        Icon(
                            imageVector = Icons.Default.ContentCopy,
                            contentDescription = null,
                            tint = if (copyConfirmation) EmeraldAccent else TextPrimary,
                            modifier = Modifier.size(16.dp)
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = if (copyConfirmation) "Copied!" else "Copy",
                            fontSize = 11.sp,
                            color = if (copyConfirmation) EmeraldAccent else TextPrimary
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun MetricChip(
    label: String,
    value: String,
    color: androidx.compose.ui.graphics.Color,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .background(SlateSurfaceVariant.copy(alpha = 0.6f), RoundedCornerShape(10.dp))
            .border(1.dp, SlateBorder.copy(alpha = 0.5f), RoundedCornerShape(10.dp))
            .padding(vertical = 6.dp, horizontal = 8.dp),
        contentAlignment = Alignment.Center
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Text(
                text = label,
                fontSize = 8.sp,
                fontWeight = FontWeight.Bold,
                color = TextTertiary,
                letterSpacing = 0.5.sp
            )
            Text(
                text = value,
                fontSize = 12.sp,
                fontWeight = FontWeight.Bold,
                color = color
            )
        }
    }
}

@Composable
private fun AssetClassRow(
    emoji: String,
    assetName: String,
    implication: String,
    accentColor: androidx.compose.ui.graphics.Color
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .background(SlateSurfaceVariant, RoundedCornerShape(14.dp))
            .border(1.dp, SlateBorder, RoundedCornerShape(14.dp))
            .padding(12.dp)
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Text(emoji, fontSize = 16.sp)
            Text(
                text = assetName,
                style = MaterialTheme.typography.titleSmall,
                fontWeight = FontWeight.SemiBold,
                color = accentColor
            )
        }
        Spacer(modifier = Modifier.height(4.dp))
        Text(
            text = implication,
            style = MaterialTheme.typography.bodySmall,
            color = TextSecondary,
            lineHeight = 18.sp
        )
    }
}
