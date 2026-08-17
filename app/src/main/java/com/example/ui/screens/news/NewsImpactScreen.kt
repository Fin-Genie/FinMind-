package com.example.ui.screens.news

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.BookmarkAdd
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Feed
import androidx.compose.material.icons.filled.FlashOn
import androidx.compose.material.icons.filled.Newspaper
import androidx.compose.material.icons.filled.Public
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.TrendingDown
import androidx.compose.material.icons.filled.TrendingUp
import androidx.compose.material.icons.filled.Tune
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Slider
import androidx.compose.material3.SliderDefaults
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableDoubleStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardCapitalization
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.data.model.AssetImpact
import com.example.data.model.FinancialNews
import com.example.data.model.ImpactDirection
import com.example.data.model.MarketSentiment
import com.example.data.model.NewsCategory
import com.example.data.model.NewsUrgency
import com.example.data.model.PortfolioImpactReport
import com.example.domain.math.Asset
import com.example.ui.theme.CrimsonRisk
import com.example.ui.theme.CyanAccent
import com.example.ui.theme.EmeraldAccent
import com.example.ui.theme.LilacAccent
import com.example.ui.theme.LilacDeep
import com.example.ui.theme.LilacSub
import com.example.ui.theme.ObsidianBg
import com.example.ui.theme.PurpleGreeks
import com.example.ui.theme.SlateBorder
import com.example.ui.theme.SlateBorderLight
import com.example.ui.theme.SlateSurface
import com.example.ui.theme.SlateSurfaceVariant
import com.example.ui.theme.TextPrimary
import com.example.ui.theme.TextSecondary
import com.example.ui.theme.TextTertiary
import com.example.ui.viewmodel.NewsImpactViewModel

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun NewsImpactScreen(
    viewModel: NewsImpactViewModel,
    snackbarHostState: SnackbarHostState,
    modifier: Modifier = Modifier
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    LaunchedEffect(uiState.saveStatusMessage) {
        uiState.saveStatusMessage?.let {
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
        // 1. Screen Title Bar & Live Desk Status
        item {
            Column(modifier = Modifier.fillMaxWidth()) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        Box(
                            modifier = Modifier
                                .size(38.dp)
                                .background(LilacAccent.copy(alpha = 0.15f), RoundedCornerShape(10.dp)),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.Newspaper,
                                contentDescription = null,
                                tint = LilacAccent,
                                modifier = Modifier.size(22.dp)
                            )
                        }

                        Column {
                            Text(
                                text = "Market News & AI Impact",
                                style = MaterialTheme.typography.headlineSmall,
                                fontWeight = FontWeight.Bold,
                                color = TextPrimary
                            )
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(6.dp)
                            ) {
                                Box(
                                    modifier = Modifier
                                        .size(7.dp)
                                        .background(EmeraldAccent, CircleShape)
                                )
                                Text(
                                    text = "GEMINI PORTFOLIO TRANSMISSION DESK",
                                    style = MaterialTheme.typography.labelSmall,
                                    fontSize = 9.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = EmeraldAccent
                                )
                            }
                        }
                    }

                    Row(horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                        IconButton(
                            onClick = { viewModel.refreshHeadlines() },
                            modifier = Modifier.testTag("btn_refresh_news")
                        ) {
                            Icon(
                                imageVector = Icons.Default.Refresh,
                                contentDescription = "Refresh News",
                                tint = TextSecondary
                            )
                        }
                        IconButton(
                            onClick = { viewModel.openCustomNewsDialog(true) },
                            modifier = Modifier.testTag("btn_add_custom_news")
                        ) {
                            Icon(
                                imageVector = Icons.Default.Add,
                                contentDescription = "Add Custom Headline",
                                tint = LilacAccent
                            )
                        }
                    }
                }
            }
        }

        // 2. Active Portfolio Holdings Strip
        item {
            Surface(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                color = SlateSurface,
                border = androidx.compose.foundation.BorderStroke(1.dp, SlateBorder)
            ) {
                Column(modifier = Modifier.padding(14.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "Target Portfolio Holdings for Impact Modeling",
                            style = MaterialTheme.typography.labelMedium,
                            fontWeight = FontWeight.SemiBold,
                            color = TextSecondary
                        )

                        TextButton(
                            onClick = { viewModel.openHoldingsEditor(true) },
                            contentPadding = androidx.compose.foundation.layout.PaddingValues(horizontal = 8.dp, vertical = 2.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.Tune,
                                contentDescription = null,
                                modifier = Modifier.size(14.dp),
                                tint = LilacAccent
                            )
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(
                                text = "Adjust Weights",
                                fontSize = 11.sp,
                                color = LilacAccent,
                                fontWeight = FontWeight.SemiBold
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(8.dp))

                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .horizontalScroll(rememberScrollState()),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        uiState.activeHoldings.forEach { holding ->
                            Box(
                                modifier = Modifier
                                    .background(SlateSurfaceVariant, RoundedCornerShape(10.dp))
                                    .border(1.dp, LilacAccent.copy(alpha = 0.3f), RoundedCornerShape(10.dp))
                                    .padding(horizontal = 10.dp, vertical = 6.dp)
                            ) {
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                                ) {
                                    Text(
                                        text = holding.ticker,
                                        style = MaterialTheme.typography.bodyMedium,
                                        fontWeight = FontWeight.Bold,
                                        color = TextPrimary
                                    )
                                    Text(
                                        text = "${(holding.weight * 100).toInt()}%",
                                        style = MaterialTheme.typography.labelSmall,
                                        fontWeight = FontWeight.SemiBold,
                                        color = LilacAccent
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }

        // 3. Hero Batch Synthesis CTA Banner
        item {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(16.dp))
                    .background(
                        Brush.linearGradient(
                            colors = listOf(LilacDeep, Color(0xFF1E1B4B))
                        )
                    )
                    .border(1.dp, LilacAccent.copy(alpha = 0.4f), RoundedCornerShape(16.dp))
                    .padding(16.dp)
            ) {
                Column {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.AutoAwesome,
                            contentDescription = null,
                            tint = LilacAccent,
                            modifier = Modifier.size(20.dp)
                        )
                        Text(
                            text = "Gemini Portfolio Shock Engine",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            color = TextPrimary
                        )
                    }

                    Spacer(modifier = Modifier.height(6.dp))

                    Text(
                        text = "Synthesize today's macro environment, Fed rate trajectory, and tech capex wave directly against your exact asset allocations.",
                        style = MaterialTheme.typography.bodySmall,
                        color = LilacSub,
                        lineHeight = 18.sp
                    )

                    Spacer(modifier = Modifier.height(12.dp))

                    Button(
                        onClick = { viewModel.analyzeBatchImpact() },
                        enabled = !uiState.isBatchAnalyzing,
                        shape = RoundedCornerShape(12.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = LilacAccent,
                            contentColor = LilacDeep
                        ),
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("btn_analyze_batch")
                    ) {
                        if (uiState.isBatchAnalyzing) {
                            CircularProgressIndicator(
                                modifier = Modifier.size(18.dp),
                                color = LilacDeep,
                                strokeWidth = 2.dp
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text("Synthesizing with Gemini...", fontWeight = FontWeight.Bold)
                        } else {
                            Icon(
                                imageVector = Icons.Default.FlashOn,
                                contentDescription = null,
                                modifier = Modifier.size(16.dp)
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = "Run Multi-Headline AI Synthesis",
                                fontWeight = FontWeight.Bold,
                                fontSize = 13.sp
                            )
                        }
                    }
                }
            }
        }

        // 4. Active Gemini Impact Report Card (if present)
        uiState.currentReport?.let { report ->
            item {
                ActiveReportCard(
                    report = report,
                    onSaveToDossier = { viewModel.saveCurrentReportToDossier() },
                    onDismiss = { viewModel.clearReport() }
                )
            }
        }

        // 5. Category Chips & Search Bar
        item {
            Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                // Search field
                OutlinedTextField(
                    value = uiState.searchQuery,
                    onValueChange = { viewModel.setSearchQuery(it) },
                    placeholder = { Text("Filter headlines by ticker, macro topic, or keyword...", color = TextTertiary, fontSize = 12.sp) },
                    leadingIcon = {
                        Icon(imageVector = Icons.Default.Search, contentDescription = null, tint = TextTertiary, modifier = Modifier.size(18.dp))
                    },
                    trailingIcon = {
                        if (uiState.searchQuery.isNotEmpty()) {
                            IconButton(onClick = { viewModel.setSearchQuery("") }) {
                                Icon(imageVector = Icons.Default.Close, contentDescription = "Clear", tint = TextTertiary, modifier = Modifier.size(16.dp))
                            }
                        }
                    },
                    singleLine = true,
                    shape = RoundedCornerShape(12.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = LilacAccent,
                        unfocusedBorderColor = SlateBorder,
                        focusedContainerColor = SlateSurface,
                        unfocusedContainerColor = SlateSurface,
                        focusedTextColor = TextPrimary,
                        unfocusedTextColor = TextPrimary
                    ),
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("input_search_news")
                )

                // Category scroll row
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .horizontalScroll(rememberScrollState()),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    NewsCategory.entries.forEach { category ->
                        val isSelected = uiState.selectedCategory == category
                        FilterChip(
                            selected = isSelected,
                            onClick = { viewModel.setCategory(category) },
                            label = { Text(category.displayName, fontSize = 11.sp, fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal) },
                            colors = FilterChipDefaults.filterChipColors(
                                selectedContainerColor = LilacAccent,
                                selectedLabelColor = LilacDeep,
                                containerColor = SlateSurface,
                                labelColor = TextSecondary
                            ),
                            border = FilterChipDefaults.filterChipBorder(
                                enabled = true,
                                selected = isSelected,
                                borderColor = if (isSelected) LilacAccent else SlateBorder
                            ),
                            modifier = Modifier.testTag("filter_chip_${category.name}")
                        )
                    }
                }
            }
        }

        // 6. Headlines Section Header
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Daily Market Wire (${uiState.filteredHeadlines.size})",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = TextPrimary
                )
                Text(
                    text = "Tap headline to simulate impact",
                    style = MaterialTheme.typography.labelSmall,
                    color = TextTertiary
                )
            }
        }

        // 7. Headline Cards List
        if (uiState.filteredHeadlines.isEmpty()) {
            item {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(140.dp)
                        .background(SlateSurface, RoundedCornerShape(16.dp))
                        .border(1.dp, SlateBorder, RoundedCornerShape(16.dp)),
                    contentAlignment = Alignment.Center
                ) {
                    Text("No matching financial headlines found.", color = TextTertiary, fontSize = 13.sp)
                }
            }
        } else {
            items(uiState.filteredHeadlines, key = { it.id }) { news ->
                val isAnalyzing = uiState.analyzingNewsId == news.id
                NewsCard(
                    news = news,
                    isAnalyzing = isAnalyzing,
                    onAnalyze = { viewModel.analyzeHeadlineImpact(news) }
                )
            }
        }

        item {
            Spacer(modifier = Modifier.height(24.dp))
        }
    }

    // Custom Headline Dialog
    if (uiState.showCustomNewsDialog) {
        AddCustomHeadlineDialog(
            onDismiss = { viewModel.openCustomNewsDialog(false) },
            onAdd = { headline, source, summary, category, sentiment ->
                viewModel.addCustomHeadline(headline, source, summary, category, sentiment)
            }
        )
    }

    // Holdings Editor Dialog
    if (uiState.showHoldingsEditor) {
        HoldingsEditorDialog(
            holdings = uiState.activeHoldings,
            onUpdateWeight = { id, w -> viewModel.updateHoldingWeight(id, w) },
            onAddHolding = { ticker, name, w -> viewModel.addCustomHolding(ticker, name, w) },
            onDismiss = { viewModel.openHoldingsEditor(false) }
        )
    }
}

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun NewsCard(
    news: FinancialNews,
    isAnalyzing: Boolean,
    onAnalyze: () -> Unit,
    modifier: Modifier = Modifier
) {
    val sentimentColor = when (news.sentiment) {
        MarketSentiment.BULLISH -> EmeraldAccent
        MarketSentiment.BEARISH -> CrimsonRisk
        MarketSentiment.VOLATILE -> PurpleGreeks
        MarketSentiment.NEUTRAL -> CyanAccent
    }

    Surface(
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        color = SlateSurface,
        border = androidx.compose.foundation.BorderStroke(1.dp, if (news.isCustom) LilacAccent.copy(alpha = 0.5f) else SlateBorder)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            // Header Row: Source, Time, Urgency Badge
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    Text(
                        text = news.source,
                        style = MaterialTheme.typography.labelSmall,
                        fontWeight = FontWeight.Bold,
                        color = LilacAccent
                    )
                    Text(
                        text = "•",
                        color = TextTertiary,
                        fontSize = 10.sp
                    )
                    Text(
                        text = news.timeAgo,
                        style = MaterialTheme.typography.labelSmall,
                        color = TextTertiary
                    )
                }

                Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                    if (news.urgency == NewsUrgency.BREAKING) {
                        Box(
                            modifier = Modifier
                                .background(CrimsonRisk.copy(alpha = 0.2f), RoundedCornerShape(6.dp))
                                .border(1.dp, CrimsonRisk, RoundedCornerShape(6.dp))
                                .padding(horizontal = 6.dp, vertical = 2.dp)
                        ) {
                            Text(
                                text = "BREAKING",
                                style = MaterialTheme.typography.labelSmall,
                                fontSize = 9.sp,
                                fontWeight = FontWeight.ExtraBold,
                                color = CrimsonRisk
                            )
                        }
                    }

                    Box(
                        modifier = Modifier
                            .background(sentimentColor.copy(alpha = 0.15f), RoundedCornerShape(6.dp))
                            .padding(horizontal = 6.dp, vertical = 2.dp)
                    ) {
                        Text(
                            text = news.sentiment.name,
                            style = MaterialTheme.typography.labelSmall,
                            fontSize = 9.sp,
                            fontWeight = FontWeight.Bold,
                            color = sentimentColor
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            // Headline Title
            Text(
                text = news.headline,
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                color = TextPrimary,
                lineHeight = 20.sp
            )

            Spacer(modifier = Modifier.height(6.dp))

            // Summary
            Text(
                text = news.summary,
                style = MaterialTheme.typography.bodySmall,
                color = TextSecondary,
                lineHeight = 17.sp
            )

            Spacer(modifier = Modifier.height(10.dp))

            // Affected Sectors
            FlowRow(
                horizontalArrangement = Arrangement.spacedBy(6.dp),
                verticalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                news.affectedSectors.forEach { sector ->
                    Box(
                        modifier = Modifier
                            .background(SlateSurfaceVariant, RoundedCornerShape(6.dp))
                            .padding(horizontal = 6.dp, vertical = 2.dp)
                    ) {
                        Text(
                            text = sector,
                            fontSize = 10.sp,
                            color = TextTertiary,
                            fontWeight = FontWeight.Medium
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            // CTA Button to analyze with Gemini
            Button(
                onClick = onAnalyze,
                enabled = !isAnalyzing,
                shape = RoundedCornerShape(10.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = SlateSurfaceVariant,
                    contentColor = LilacAccent
                ),
                border = androidx.compose.foundation.BorderStroke(1.dp, LilacAccent.copy(alpha = 0.5f)),
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("btn_analyze_${news.id}")
            ) {
                if (isAnalyzing) {
                    CircularProgressIndicator(
                        modifier = Modifier.size(16.dp),
                        color = LilacAccent,
                        strokeWidth = 2.dp
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "Analyzing Holdings Impact...",
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold,
                        color = LilacAccent
                    )
                } else {
                    Icon(
                        imageVector = Icons.Default.AutoAwesome,
                        contentDescription = null,
                        modifier = Modifier.size(14.dp),
                        tint = LilacAccent
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = "Summarize Holdings Impact (Gemini)",
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold,
                        color = LilacAccent
                    )
                }
            }
        }
    }
}

@Composable
fun ActiveReportCard(
    report: PortfolioImpactReport,
    onSaveToDossier: () -> Unit,
    onDismiss: () -> Unit,
    modifier: Modifier = Modifier
) {
    val netColor = if (report.netPortfolioImpactPct >= 0) EmeraldAccent else CrimsonRisk

    Surface(
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(20.dp),
        color = SlateSurface,
        border = androidx.compose.foundation.BorderStroke(1.5.dp, LilacAccent)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            // Header with AI Sparkle & Close
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .size(32.dp)
                            .background(LilacAccent, RoundedCornerShape(8.dp)),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.AutoAwesome,
                            contentDescription = null,
                            tint = LilacDeep,
                            modifier = Modifier.size(18.dp)
                        )
                    }
                    Column {
                        Text(
                            text = "Gemini Impact Assessment",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            color = LilacAccent
                        )
                        Text(
                            text = "Cross-Asset Quantitative Transmission",
                            style = MaterialTheme.typography.labelSmall,
                            fontSize = 10.sp,
                            color = TextTertiary
                        )
                    }
                }

                IconButton(
                    onClick = onDismiss,
                    modifier = Modifier.size(28.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.Close,
                        contentDescription = "Close Report",
                        tint = TextTertiary
                    )
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            // Headline Analyzed Quote
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(SlateSurfaceVariant, RoundedCornerShape(10.dp))
                    .padding(10.dp)
            ) {
                Text(
                    text = "\"${report.headlineTitle}\"",
                    style = MaterialTheme.typography.bodySmall,
                    fontWeight = FontWeight.Medium,
                    color = TextPrimary,
                    fontStyle = androidx.compose.ui.text.font.FontStyle.Italic
                )
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Key Metrics Row: Net Alpha & Volatility Delta
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                // Net Alpha
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .background(netColor.copy(alpha = 0.12f), RoundedCornerShape(12.dp))
                        .border(1.dp, netColor.copy(alpha = 0.4f), RoundedCornerShape(12.dp))
                        .padding(12.dp)
                ) {
                    Column {
                        Text(
                            text = "Portfolio Net Alpha",
                            style = MaterialTheme.typography.labelSmall,
                            fontSize = 10.sp,
                            color = TextTertiary
                        )
                        Spacer(modifier = Modifier.height(2.dp))
                        Text(
                            text = "${if (report.netPortfolioImpactPct >= 0) "+" else ""}${String.format("%.2f", report.netPortfolioImpactPct)}%",
                            style = MaterialTheme.typography.titleLarge,
                            fontWeight = FontWeight.Bold,
                            color = netColor
                        )
                    }
                }

                // Volatility Delta
                Box(
                    modifier = Modifier
                        .weight(1.2f)
                        .background(PurpleGreeks.copy(alpha = 0.12f), RoundedCornerShape(12.dp))
                        .border(1.dp, PurpleGreeks.copy(alpha = 0.4f), RoundedCornerShape(12.dp))
                        .padding(12.dp)
                ) {
                    Column {
                        Text(
                            text = "Estimated Risk Vector",
                            style = MaterialTheme.typography.labelSmall,
                            fontSize = 10.sp,
                            color = TextTertiary
                        )
                        Spacer(modifier = Modifier.height(2.dp))
                        Text(
                            text = report.volatilityDelta,
                            style = MaterialTheme.typography.bodyMedium,
                            fontWeight = FontWeight.Bold,
                            color = PurpleGreeks,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Macro Regime Transmission
            Text(
                text = "Macroeconomic Transmission Channel",
                style = MaterialTheme.typography.labelMedium,
                fontWeight = FontWeight.Bold,
                color = CyanAccent
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = report.macroRegimeSummary,
                style = MaterialTheme.typography.bodySmall,
                color = TextSecondary,
                lineHeight = 18.sp
            )

            Spacer(modifier = Modifier.height(14.dp))

            // Asset Specific Impact Breakdown Header
            Text(
                text = "Asset Holdings Impact Breakdown",
                style = MaterialTheme.typography.titleSmall,
                fontWeight = FontWeight.Bold,
                color = TextPrimary
            )

            Spacer(modifier = Modifier.height(8.dp))

            // Per Asset Breakdown Cards
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                report.assetImpacts.forEach { assetImpact ->
                    AssetImpactItemCard(assetImpact = assetImpact)
                }
            }

            Spacer(modifier = Modifier.height(14.dp))

            // CladRb Executive Action Recommendation
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(LilacAccent.copy(alpha = 0.10f), RoundedCornerShape(12.dp))
                    .border(1.dp, LilacAccent.copy(alpha = 0.3f), RoundedCornerShape(12.dp))
                    .padding(12.dp)
            ) {
                Column {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.AutoAwesome,
                            contentDescription = null,
                            tint = LilacAccent,
                            modifier = Modifier.size(16.dp)
                        )
                        Text(
                            text = "CladRb Tactical Playbook",
                            style = MaterialTheme.typography.labelMedium,
                            fontWeight = FontWeight.Bold,
                            color = LilacAccent
                        )
                    }
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = report.cladrbExecutiveAdvice,
                        style = MaterialTheme.typography.bodySmall,
                        color = TextPrimary,
                        lineHeight = 18.sp
                    )
                }
            }

            Spacer(modifier = Modifier.height(14.dp))

            // Save to Dossier Action Button
            Button(
                onClick = onSaveToDossier,
                shape = RoundedCornerShape(12.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = LilacAccent,
                    contentColor = LilacDeep
                ),
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("btn_save_news_dossier")
            ) {
                Icon(
                    imageVector = Icons.Default.BookmarkAdd,
                    contentDescription = null,
                    modifier = Modifier.size(16.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = "Save Impact Report to Quantitative Dossier",
                    fontWeight = FontWeight.Bold,
                    fontSize = 13.sp
                )
            }
        }
    }
}

@Composable
fun AssetImpactItemCard(
    assetImpact: AssetImpact,
    modifier: Modifier = Modifier
) {
    val dirColor = when (assetImpact.impactDirection) {
        ImpactDirection.BULLISH -> EmeraldAccent
        ImpactDirection.BEARISH -> CrimsonRisk
        ImpactDirection.MIXED -> PurpleGreeks
        ImpactDirection.NEUTRAL -> CyanAccent
    }

    val dirIcon = when (assetImpact.impactDirection) {
        ImpactDirection.BULLISH -> Icons.Default.TrendingUp
        ImpactDirection.BEARISH -> Icons.Default.TrendingDown
        else -> Icons.Default.Tune
    }

    Surface(
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        color = SlateSurfaceVariant,
        border = androidx.compose.foundation.BorderStroke(1.dp, SlateBorder)
    ) {
        Column(modifier = Modifier.padding(12.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Text(
                        text = assetImpact.ticker,
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = TextPrimary
                    )
                    Text(
                        text = "${String.format("%.0f", assetImpact.weightPct)}% Weight",
                        style = MaterialTheme.typography.labelSmall,
                        color = TextTertiary
                    )
                }

                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .background(dirColor.copy(alpha = 0.15f), RoundedCornerShape(6.dp))
                            .border(1.dp, dirColor.copy(alpha = 0.4f), RoundedCornerShape(6.dp))
                            .padding(horizontal = 8.dp, vertical = 2.dp)
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(4.dp)
                        ) {
                            Icon(
                                imageVector = dirIcon,
                                contentDescription = null,
                                tint = dirColor,
                                modifier = Modifier.size(12.dp)
                            )
                            Text(
                                text = "${assetImpact.impactDirection} (${if (assetImpact.estimatedAlphaPct >= 0) "+" else ""}${String.format("%.1f", assetImpact.estimatedAlphaPct)}%)",
                                style = MaterialTheme.typography.labelSmall,
                                fontWeight = FontWeight.Bold,
                                color = dirColor,
                                fontSize = 10.sp
                            )
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(6.dp))

            // Transmission Channel
            Text(
                text = "Vector: ${assetImpact.transmissionChannel}",
                style = MaterialTheme.typography.labelSmall,
                fontWeight = FontWeight.SemiBold,
                color = LilacAccent
            )

            Spacer(modifier = Modifier.height(2.dp))

            // Rationale
            Text(
                text = assetImpact.rationale,
                style = MaterialTheme.typography.bodySmall,
                color = TextSecondary,
                lineHeight = 16.sp
            )

            Spacer(modifier = Modifier.height(6.dp))

            // Defense / Tactical suggestion
            Row(
                verticalAlignment = Alignment.Top,
                horizontalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                Text(
                    text = "Defense:",
                    style = MaterialTheme.typography.labelSmall,
                    fontWeight = FontWeight.Bold,
                    color = EmeraldAccent,
                    fontSize = 10.sp
                )
                Text(
                    text = assetImpact.tacticalDefense,
                    style = MaterialTheme.typography.bodySmall,
                    fontSize = 11.sp,
                    color = TextPrimary
                )
            }
        }
    }
}

@Composable
fun AddCustomHeadlineDialog(
    onDismiss: () -> Unit,
    onAdd: (headline: String, source: String, summary: String, category: NewsCategory, sentiment: MarketSentiment) -> Unit
) {
    var headline by remember { mutableStateOf("") }
    var source by remember { mutableStateOf("") }
    var summary by remember { mutableStateOf("") }
    var selectedCategory by remember { mutableStateOf(NewsCategory.MACRO_RATES) }
    var selectedSentiment by remember { mutableStateOf(MarketSentiment.VOLATILE) }

    AlertDialog(
        onDismissRequest = onDismiss,
        containerColor = SlateSurface,
        shape = RoundedCornerShape(20.dp),
        title = {
            Text(
                text = "Add Custom Financial Headline",
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold,
                color = TextPrimary
            )
        },
        text = {
            LazyColumn(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                item {
                    Text(
                        text = "Stress test your portfolio with breaking announcements, earnings calls, or macroeconomic shocks.",
                        style = MaterialTheme.typography.bodySmall,
                        color = TextSecondary
                    )
                }

                item {
                    OutlinedTextField(
                        value = headline,
                        onValueChange = { headline = it },
                        label = { Text("Headline Title *") },
                        placeholder = { Text("e.g. Fed slashes rates by 50 bps in surprise move") },
                        singleLine = false,
                        maxLines = 3,
                        modifier = Modifier.fillMaxWidth()
                    )
                }

                item {
                    OutlinedTextField(
                        value = source,
                        onValueChange = { source = it },
                        label = { Text("Source / Publisher") },
                        placeholder = { Text("e.g. Bloomberg, Reuters, SEC") },
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth()
                    )
                }

                item {
                    OutlinedTextField(
                        value = summary,
                        onValueChange = { summary = it },
                        label = { Text("Context & Details") },
                        placeholder = { Text("Details regarding policy changes, revenue figures, or supply chain...") },
                        singleLine = false,
                        maxLines = 4,
                        modifier = Modifier.fillMaxWidth()
                    )
                }

                item {
                    Text(
                        text = "Category",
                        style = MaterialTheme.typography.labelMedium,
                        color = TextSecondary
                    )
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .horizontalScroll(rememberScrollState()),
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        NewsCategory.entries.filter { it != NewsCategory.ALL }.forEach { cat ->
                            val isSel = selectedCategory == cat
                            FilterChip(
                                selected = isSel,
                                onClick = { selectedCategory = cat },
                                label = { Text(cat.displayName, fontSize = 10.sp) },
                                colors = FilterChipDefaults.filterChipColors(
                                    selectedContainerColor = LilacAccent,
                                    selectedLabelColor = LilacDeep
                                )
                            )
                        }
                    }
                }

                item {
                    Text(
                        text = "Market Sentiment",
                        style = MaterialTheme.typography.labelMedium,
                        color = TextSecondary
                    )
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .horizontalScroll(rememberScrollState()),
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        MarketSentiment.entries.forEach { sent ->
                            val isSel = selectedSentiment == sent
                            FilterChip(
                                selected = isSel,
                                onClick = { selectedSentiment = sent },
                                label = { Text(sent.name, fontSize = 10.sp) },
                                colors = FilterChipDefaults.filterChipColors(
                                    selectedContainerColor = LilacAccent,
                                    selectedLabelColor = LilacDeep
                                )
                            )
                        }
                    }
                }
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    if (headline.isNotBlank()) {
                        onAdd(headline, source, summary, selectedCategory, selectedSentiment)
                    }
                },
                enabled = headline.isNotBlank(),
                colors = ButtonDefaults.buttonColors(containerColor = LilacAccent, contentColor = LilacDeep)
            ) {
                Text("Analyze Impact", fontWeight = FontWeight.Bold)
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancel", color = TextTertiary)
            }
        }
    )
}

@Composable
fun HoldingsEditorDialog(
    holdings: List<Asset>,
    onUpdateWeight: (id: String, newWeightPct: Double) -> Unit,
    onAddHolding: (ticker: String, name: String, weightPct: Double) -> Unit,
    onDismiss: () -> Unit
) {
    var newTicker by remember { mutableStateOf("") }
    var newName by remember { mutableStateOf("") }
    var newWeight by remember { mutableStateOf("15") }

    AlertDialog(
        onDismissRequest = onDismiss,
        containerColor = SlateSurface,
        shape = RoundedCornerShape(20.dp),
        title = {
            Text(
                text = "Target Portfolio Holdings",
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold,
                color = TextPrimary
            )
        },
        text = {
            LazyColumn(
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                item {
                    Text(
                        text = "Customize the asset holdings and percentage allocations evaluated during Gemini headline impact synthesis.",
                        style = MaterialTheme.typography.bodySmall,
                        color = TextSecondary
                    )
                }

                items(holdings, key = { it.id }) { asset ->
                    var currentWeightPct by remember(asset.weight) { mutableDoubleStateOf(asset.weight * 100.0) }

                    Surface(
                        shape = RoundedCornerShape(10.dp),
                        color = SlateSurfaceVariant,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Column(modifier = Modifier.padding(10.dp)) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Column {
                                    Text(
                                        text = asset.ticker,
                                        style = MaterialTheme.typography.titleMedium,
                                        fontWeight = FontWeight.Bold,
                                        color = TextPrimary
                                    )
                                    Text(
                                        text = asset.name,
                                        style = MaterialTheme.typography.labelSmall,
                                        color = TextTertiary,
                                        maxLines = 1,
                                        overflow = TextOverflow.Ellipsis
                                    )
                                }

                                Text(
                                    text = "${currentWeightPct.toInt()}%",
                                    style = MaterialTheme.typography.titleMedium,
                                    fontWeight = FontWeight.Bold,
                                    color = LilacAccent
                                )
                            }

                            Slider(
                                value = currentWeightPct.toFloat(),
                                onValueChange = {
                                    currentWeightPct = it.toDouble()
                                    onUpdateWeight(asset.id, currentWeightPct)
                                },
                                valueRange = 0f..100f,
                                steps = 19,
                                colors = SliderDefaults.colors(
                                    thumbColor = LilacAccent,
                                    activeTrackColor = LilacAccent,
                                    inactiveTrackColor = SlateBorder
                                )
                            )
                        }
                    }
                }

                item {
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = "Add Custom Asset Holding",
                        style = MaterialTheme.typography.labelMedium,
                        fontWeight = FontWeight.Bold,
                        color = CyanAccent
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        OutlinedTextField(
                            value = newTicker,
                            onValueChange = { newTicker = it.uppercase() },
                            label = { Text("Ticker") },
                            placeholder = { Text("AAPL") },
                            singleLine = true,
                            modifier = Modifier.weight(1f)
                        )
                        OutlinedTextField(
                            value = newWeight,
                            onValueChange = { newWeight = it },
                            label = { Text("Weight %") },
                            placeholder = { Text("15") },
                            singleLine = true,
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                            modifier = Modifier.weight(1f)
                        )
                    }

                    Spacer(modifier = Modifier.height(6.dp))

                    OutlinedTextField(
                        value = newName,
                        onValueChange = { newName = it },
                        label = { Text("Asset Description") },
                        placeholder = { Text("Apple Inc (Consumer Hardware/Services)") },
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth()
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    Button(
                        onClick = {
                            if (newTicker.isNotBlank()) {
                                val w = newWeight.toDoubleOrNull() ?: 15.0
                                onAddHolding(newTicker, newName, w)
                                newTicker = ""
                                newName = ""
                                newWeight = "15"
                            }
                        },
                        enabled = newTicker.isNotBlank(),
                        colors = ButtonDefaults.buttonColors(containerColor = CyanAccent, contentColor = Color(0xFF03314B)),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Icon(imageVector = Icons.Default.Add, contentDescription = null, modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("Add Asset to Model", fontWeight = FontWeight.Bold)
                    }
                }
            }
        },
        confirmButton = {
            Button(
                onClick = onDismiss,
                colors = ButtonDefaults.buttonColors(containerColor = LilacAccent, contentColor = LilacDeep)
            ) {
                Text("Done", fontWeight = FontWeight.Bold)
            }
        }
    )
}
