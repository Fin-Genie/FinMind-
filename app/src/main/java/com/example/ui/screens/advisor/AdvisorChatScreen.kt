package com.example.ui.screens.advisor

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
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.selection.SelectionContainer
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Send
import androidx.compose.material.icons.filled.AutoGraph
import androidx.compose.material.icons.filled.CleaningServices
import androidx.compose.material.icons.filled.Public
import androidx.compose.material.icons.filled.QueryStats
import androidx.compose.material.icons.filled.Security
import androidx.compose.material.icons.filled.ShowChart
import androidx.compose.material.icons.filled.Timeline
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalClipboardManager
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.data.local.ChatMessageEntity
import com.example.ui.components.CladRbAvatarHeader
import com.example.ui.components.ScenarioChip
import com.example.ui.theme.CrimsonRisk
import com.example.ui.theme.CyanAccent
import com.example.ui.theme.EmeraldAccent
import com.example.ui.theme.GoldAccent
import com.example.ui.theme.LilacAccent
import com.example.ui.theme.LilacDeep
import com.example.ui.theme.ObsidianBg
import com.example.ui.theme.SlateBorder
import com.example.ui.theme.SlateSurface
import com.example.ui.theme.SlateSurfaceVariant
import com.example.ui.theme.TextPrimary
import com.example.ui.theme.TextSecondary
import com.example.ui.theme.TextTertiary
import com.example.ui.theme.TextWhite
import com.example.ui.viewmodel.AdvisorViewModel
import kotlinx.coroutines.launch

@Composable
fun AdvisorChatScreen(
    viewModel: AdvisorViewModel,
    snackbarHostState: SnackbarHostState,
    modifier: Modifier = Modifier
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    var inputText by remember { mutableStateOf("") }
    val listState = rememberLazyListState()
    val scope = rememberCoroutineScope()
    val clipboardManager = LocalClipboardManager.current

    LaunchedEffect(uiState.messages.size) {
        if (uiState.messages.isNotEmpty()) {
            listState.animateScrollToItem(uiState.messages.size - 1)
        }
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(ObsidianBg)
            .imePadding()
    ) {
        // Top Header
        Column(modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)) {
            CladRbAvatarHeader(
                subtitle = "Elite Quant Advisor",
                activeQuote = "Precision in stochastic modeling is the prerequisite of enduring alpha."
            )

            Spacer(modifier = Modifier.height(12.dp))

            // Quick Scenario Chips Bar
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .horizontalScroll(rememberScrollState()),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                ScenarioChip(
                    title = "Global Macro Indicators",
                    icon = Icons.Default.Public,
                    accentColor = LilacAccent,
                    onClick = {
                        viewModel.sendMessage(
                            "As CladRb, analyze current global economic indicators across inflation rates, central bank interest rates, real GDP growth, and unemployment figures. What are the potential market implications across asset classes?",
                            "GLOBAL_MACRO"
                        )
                    }
                )
                ScenarioChip(
                    title = "Markowitz Frontier",
                    icon = Icons.Default.AutoGraph,
                    accentColor = LilacAccent,
                    onClick = {
                        viewModel.sendMessage(
                            "Optimize a 4-asset portfolio (NVDA, MSFT, SPY, TLT) using Markowitz Mean-Variance theorem. What is the tangency portfolio weights and maximum Sharpe Ratio?",
                            "MARKOWITZ"
                        )
                    }
                )
                ScenarioChip(
                    title = "Monte Carlo Simulation",
                    icon = Icons.Default.QueryStats,
                    accentColor = LilacAccent,
                    onClick = {
                        viewModel.sendMessage(
                            "Run a 10-year Monte Carlo simulation with Geometric Brownian Motion stochastic drift for a \$100,000 portfolio with \$500/month contributions, 10% expected return, and 18% volatility. What is the probability distribution of terminal outcomes and likelihood of reaching \$350,000?",
                            "MONTE_CARLO"
                        )
                    }
                )
                ScenarioChip(
                    title = "Multi-Stage DCF",
                    icon = Icons.Default.ShowChart,
                    accentColor = LilacAccent,
                    onClick = {
                        viewModel.sendMessage(
                            "Perform a rigorous 10-year Discounted Cash Flow valuation with WACC CAPM breakdown and Gordon Growth terminal value for an enterprise generating \$28B FCF.",
                            "DCF"
                        )
                    }
                )
                ScenarioChip(
                    title = "ADF & GARCH(1,1)",
                    icon = Icons.Default.Timeline,
                    accentColor = LilacAccent,
                    onClick = {
                        viewModel.sendMessage(
                            "Explain how Augmented Dickey-Fuller stationarity testing and GARCH(1,1) volatility clustering parameters (alpha=0.08, beta=0.88) model persistent market shocks.",
                            "TIME_SERIES"
                        )
                    }
                )
                ScenarioChip(
                    title = "99% Fat-Tail VaR",
                    icon = Icons.Default.Security,
                    accentColor = LilacAccent,
                    onClick = {
                        viewModel.sendMessage(
                            "Calculate 99% Value at Risk (VaR) and Expected Shortfall (CVaR) under Student-t leptokurtic distribution for a \$1,000,000 portfolio, including Black-Scholes Greeks.",
                            "RISK_VAR"
                        )
                    }
                )
            }
        }

        // Chat Message Stream
        LazyColumn(
            state = listState,
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth()
                .padding(horizontal = 16.dp),
            verticalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            items(uiState.messages, key = { it.id }) { msg ->
                ChatMessageBubble(
                    message = msg,
                    onSaveReport = {
                        viewModel.saveReportToDossier(
                            title = "Advisor Report (${msg.scenarioTag ?: "Quantitative Insight"})",
                            summary = msg.content.take(120) + "...",
                            metricsJson = "{}",
                            insight = msg.content
                        )
                        scope.launch {
                            snackbarHostState.showSnackbar("Analysis saved to Quantitative Dossier!")
                        }
                    },
                    onCopy = {
                        clipboardManager.setText(AnnotatedString(msg.content))
                        scope.launch {
                            snackbarHostState.showSnackbar("Copied to clipboard")
                        }
                    }
                )
            }

            if (uiState.isLoading) {
                item {
                    CladRbThinkingBadge()
                }
            }

            item {
                Spacer(modifier = Modifier.height(8.dp))
            }
        }

        // Sleek Input Row
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .background(ObsidianBg)
                .padding(horizontal = 16.dp, vertical = 12.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(
                    onClick = { viewModel.clearHistory() },
                    modifier = Modifier
                        .size(42.dp)
                        .background(SlateSurface, CircleShape)
                        .border(1.dp, SlateBorder, CircleShape)
                ) {
                    Icon(
                        imageVector = Icons.Default.CleaningServices,
                        contentDescription = "Clear Chat History",
                        tint = TextTertiary,
                        modifier = Modifier.size(18.dp)
                    )
                }

                Spacer(modifier = Modifier.width(8.dp))

                OutlinedTextField(
                    value = inputText,
                    onValueChange = { inputText = it },
                    placeholder = {
                        Text(
                            text = "Ask CladRb about tail risk...",
                            style = MaterialTheme.typography.bodyMedium,
                            color = TextTertiary,
                            fontSize = 13.sp
                        )
                    },
                    modifier = Modifier
                        .weight(1f)
                        .testTag("advisor_input_field"),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedContainerColor = SlateSurface,
                        unfocusedContainerColor = SlateSurface,
                        focusedBorderColor = LilacAccent,
                        unfocusedBorderColor = SlateBorder,
                        focusedTextColor = TextPrimary,
                        unfocusedTextColor = TextPrimary,
                        cursorColor = LilacAccent
                    ),
                    shape = RoundedCornerShape(24.dp),
                    maxLines = 3
                )

                Spacer(modifier = Modifier.width(8.dp))

                IconButton(
                    onClick = {
                        if (inputText.isNotBlank()) {
                            viewModel.sendMessage(inputText)
                            inputText = ""
                        }
                    },
                    enabled = inputText.isNotBlank() && !uiState.isLoading,
                    modifier = Modifier
                        .size(46.dp)
                        .background(
                            if (inputText.isNotBlank()) LilacAccent else SlateSurface,
                            CircleShape
                        )
                        .border(
                            1.dp,
                            if (inputText.isNotBlank()) LilacAccent else SlateBorder,
                            CircleShape
                        )
                        .testTag("advisor_send_button")
                ) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.Send,
                        contentDescription = "Send prompt to CladRb",
                        tint = if (inputText.isNotBlank()) LilacDeep else TextTertiary,
                        modifier = Modifier.size(20.dp)
                    )
                }
            }
        }
    }
}

@Composable
fun ChatMessageBubble(
    message: ChatMessageEntity,
    onSaveReport: () -> Unit,
    onCopy: () -> Unit,
    modifier: Modifier = Modifier
) {
    val isUser = message.sender == "USER"

    Column(
        modifier = modifier.fillMaxWidth(),
        horizontalAlignment = if (isUser) Alignment.End else Alignment.Start
    ) {
        if (!isUser) {
            Row(
                modifier = Modifier.fillMaxWidth(0.92f),
                horizontalArrangement = Arrangement.spacedBy(10.dp),
                verticalAlignment = Alignment.Top
            ) {
                // Sleek CR circular avatar badge
                Box(
                    modifier = Modifier
                        .size(32.dp)
                        .clip(CircleShape)
                        .background(LilacAccent),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "CR",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        color = LilacDeep
                    )
                }

                Column {
                    Box(
                        modifier = Modifier
                            .background(
                                SlateSurface,
                                RoundedCornerShape(
                                    topStart = 16.dp,
                                    topEnd = 16.dp,
                                    bottomEnd = 16.dp,
                                    bottomStart = 2.dp
                                )
                            )
                            .border(
                                1.dp,
                                SlateBorder,
                                RoundedCornerShape(
                                    topStart = 16.dp,
                                    topEnd = 16.dp,
                                    bottomEnd = 16.dp,
                                    bottomStart = 2.dp
                                )
                            )
                            .padding(14.dp)
                    ) {
                        SelectionContainer {
                            Text(
                                text = message.content,
                                style = MaterialTheme.typography.bodyMedium,
                                color = TextPrimary,
                                lineHeight = 20.sp,
                                fontSize = 13.sp
                            )
                        }
                    }

                    // Sleek Action Toolbar
                    Row(
                        modifier = Modifier.padding(top = 6.dp, start = 4.dp),
                        horizontalArrangement = Arrangement.spacedBy(16.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "Save to Dossier",
                            style = MaterialTheme.typography.labelSmall,
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Medium,
                            color = LilacAccent,
                            modifier = Modifier
                                .clip(RoundedCornerShape(4.dp))
                                .clickable { onSaveReport() }
                                .padding(vertical = 2.dp, horizontal = 4.dp)
                        )
                        Text(
                            text = "Copy Analysis",
                            style = MaterialTheme.typography.labelSmall,
                            fontSize = 11.sp,
                            color = TextSecondary,
                            modifier = Modifier
                                .clip(RoundedCornerShape(4.dp))
                                .clickable { onCopy() }
                                .padding(vertical = 2.dp, horizontal = 4.dp)
                        )
                    }
                }
            }
        } else {
            // User Bubble
            Box(
                modifier = Modifier
                    .widthIn(max = 300.dp)
                    .background(
                        SlateSurfaceVariant,
                        RoundedCornerShape(
                            topStart = 16.dp,
                            topEnd = 16.dp,
                            bottomStart = 16.dp,
                            bottomEnd = 2.dp
                        )
                    )
                    .border(
                        1.dp,
                        SlateBorder,
                        RoundedCornerShape(
                            topStart = 16.dp,
                            topEnd = 16.dp,
                            bottomStart = 16.dp,
                            bottomEnd = 2.dp
                        )
                    )
                    .padding(14.dp)
            ) {
                SelectionContainer {
                    Text(
                        text = message.content,
                        style = MaterialTheme.typography.bodyMedium,
                        color = TextWhite,
                        lineHeight = 19.sp,
                        fontSize = 13.sp
                    )
                }
            }
        }
    }
}

@Composable
fun CladRbThinkingBadge() {
    Row(
        modifier = Modifier
            .background(SlateSurface, RoundedCornerShape(20.dp))
            .border(1.dp, LilacAccent.copy(alpha = 0.5f), RoundedCornerShape(20.dp))
            .padding(horizontal = 14.dp, vertical = 8.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        CircularProgressIndicator(
            modifier = Modifier.size(16.dp),
            color = LilacAccent,
            strokeWidth = 2.dp
        )
        Text(
            text = "CladRb is computing stochastic vectors & risk models...",
            style = MaterialTheme.typography.bodySmall,
            color = TextSecondary,
            fontSize = 11.sp
        )
    }
}
