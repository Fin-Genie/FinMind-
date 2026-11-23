package com.example.ui.screens.macro

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
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.CompareArrows
import androidx.compose.material.icons.filled.Public
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.ScrollableTabRow
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Tab
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.domain.macro.MacroDataSet
import com.example.domain.macro.MacroIndicatorType
import com.example.domain.macro.TimeHorizon
import com.example.ui.components.CladRbAvatarHeader
import com.example.ui.components.CladRbMacroAdvisorCard
import com.example.ui.components.DualMacroOverlayChart
import com.example.ui.components.GlobalPeerComparisonBarChart
import com.example.ui.components.InteractiveMacroLineChart
import com.example.ui.components.MacroCycleQuadrantRadar
import com.example.ui.components.MacroKpiCard
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
import com.example.ui.viewmodel.MacroDashboardTab
import com.example.ui.viewmodel.MacroViewModel

@Composable
fun MacroDashboardScreen(
    viewModel: MacroViewModel,
    snackbarHostState: SnackbarHostState,
    modifier: Modifier = Modifier
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val currentCountry = uiState.currentCountry
    val commentary = uiState.commentary

    var customQuestionText by remember { mutableStateOf("") }

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
                subtitle = "Global Economic Indicators",
                activeQuote = "Macroeconomic variables dictate the systemic discount rate of all cash flows."
            )
        }

        // 2. Country Selection Carousel
        item {
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "SELECT GLOBAL ECONOMY",
                        style = MaterialTheme.typography.labelSmall,
                        fontSize = 9.sp,
                        fontWeight = FontWeight.Bold,
                        letterSpacing = 1.sp,
                        color = LilacAccent
                    )
                    Text(
                        text = "${currentCountry.centralBank} • ${currentCountry.currency}",
                        style = MaterialTheme.typography.labelSmall,
                        fontSize = 9.sp,
                        color = TextTertiary
                    )
                }

                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .horizontalScroll(rememberScrollState()),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    MacroDataSet.allCountries.forEach { country ->
                        val isSelected = country.id == uiState.selectedCountryId
                        CountryPill(
                            flag = country.flag,
                            name = country.name,
                            isSelected = isSelected,
                            onClick = { viewModel.selectCountry(country.id) },
                            modifier = Modifier.testTag("country_pill_${country.id}")
                        )
                    }
                }
            }
        }

        // 3. Time Horizon Selector
        item {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(SlateSurface, RoundedCornerShape(16.dp))
                    .border(1.dp, SlateBorder, RoundedCornerShape(16.dp))
                    .padding(4.dp),
                horizontalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                TimeHorizon.entries.forEach { horizon ->
                    val isSelected = horizon == uiState.selectedHorizon
                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .clip(RoundedCornerShape(12.dp))
                            .background(
                                if (isSelected) LilacAccent else Color.Transparent
                            )
                            .clickable { viewModel.selectHorizon(horizon) }
                            .padding(vertical = 8.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Text(
                                text = horizon.label,
                                fontSize = 12.sp,
                                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                                color = if (isSelected) LilacDeep else TextSecondary
                            )
                        }
                    }
                }
            }
            Text(
                text = "${uiState.selectedHorizon.fullLabel} • ${uiState.selectedHorizon.description}",
                fontSize = 10.sp,
                color = TextTertiary,
                modifier = Modifier.padding(top = 4.dp, start = 4.dp)
            )
        }

        // 4. View Mode Tabs (Overview, Inflation, Rates, GDP, Unemployment, Quadrant, Peers)
        item {
            ScrollableTabRow(
                selectedTabIndex = uiState.activeTab.ordinal,
                containerColor = Color.Transparent,
                contentColor = LilacAccent,
                edgePadding = 0.dp,
                divider = {},
                indicator = { tabPositions ->
                    TabRowDefaults.SecondaryIndicator(
                        Modifier.tabIndicatorOffset(tabPositions[uiState.activeTab.ordinal]),
                        color = LilacAccent
                    )
                }
            ) {
                MacroDashboardTab.entries.forEach { tab ->
                    Tab(
                        selected = uiState.activeTab == tab,
                        onClick = { viewModel.selectTab(tab) },
                        text = {
                            Text(
                                text = tab.label,
                                fontSize = 11.sp,
                                fontWeight = if (uiState.activeTab == tab) FontWeight.Bold else FontWeight.Normal,
                                color = if (uiState.activeTab == tab) LilacAccent else TextSecondary
                            )
                        },
                        modifier = Modifier.testTag("tab_${tab.name.lowercase()}")
                    )
                }
            }
        }

        // 5. Active Visualization Body based on Tab
        item {
            when (uiState.activeTab) {
                MacroDashboardTab.OVERVIEW -> {
                    Column(verticalArrangement = Arrangement.spacedBy(14.dp)) {
                        // 4 KPI Stat Cards Grid (2x2)
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(10.dp)
                        ) {
                            MacroKpiCard(
                                indicatorType = MacroIndicatorType.INFLATION,
                                currentValue = currentCountry.getLatestValue(MacroIndicatorType.INFLATION),
                                delta = currentCountry.getLatestValue(MacroIndicatorType.INFLATION) - currentCountry.getPreviousValue(MacroIndicatorType.INFLATION),
                                benchmarkLabel = "Target ${currentCountry.targetInflation}%",
                                accentColor = LilacAccent,
                                onClick = { viewModel.selectTab(MacroDashboardTab.INFLATION) },
                                modifier = Modifier.weight(1f).testTag("kpi_inflation")
                            )
                            MacroKpiCard(
                                indicatorType = MacroIndicatorType.INTEREST_RATE,
                                currentValue = currentCountry.getLatestValue(MacroIndicatorType.INTEREST_RATE),
                                delta = currentCountry.getLatestValue(MacroIndicatorType.INTEREST_RATE) - currentCountry.getPreviousValue(MacroIndicatorType.INTEREST_RATE),
                                benchmarkLabel = "Neutral ${currentCountry.neutralRateEstimate}%",
                                accentColor = CyanAccent,
                                onClick = { viewModel.selectTab(MacroDashboardTab.RATES) },
                                modifier = Modifier.weight(1f).testTag("kpi_rates")
                            )
                        }

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(10.dp)
                        ) {
                            MacroKpiCard(
                                indicatorType = MacroIndicatorType.GDP_GROWTH,
                                currentValue = currentCountry.getLatestValue(MacroIndicatorType.GDP_GROWTH),
                                delta = currentCountry.getLatestValue(MacroIndicatorType.GDP_GROWTH) - currentCountry.getPreviousValue(MacroIndicatorType.GDP_GROWTH),
                                benchmarkLabel = "Real YoY",
                                accentColor = EmeraldAccent,
                                onClick = { viewModel.selectTab(MacroDashboardTab.GDP) },
                                modifier = Modifier.weight(1f).testTag("kpi_gdp")
                            )
                            MacroKpiCard(
                                indicatorType = MacroIndicatorType.UNEMPLOYMENT,
                                currentValue = currentCountry.getLatestValue(MacroIndicatorType.UNEMPLOYMENT),
                                delta = currentCountry.getLatestValue(MacroIndicatorType.UNEMPLOYMENT) - currentCountry.getPreviousValue(MacroIndicatorType.UNEMPLOYMENT),
                                benchmarkLabel = "Jobless %",
                                accentColor = CrimsonRisk,
                                onClick = { viewModel.selectTab(MacroDashboardTab.JOBS) },
                                modifier = Modifier.weight(1f).testTag("kpi_unemployment")
                            )
                        }

                        // Overlay Switch bar
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .background(SlateSurface, RoundedCornerShape(14.dp))
                                .border(1.dp, SlateBorder, RoundedCornerShape(14.dp))
                                .padding(horizontal = 14.dp, vertical = 8.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                Icon(
                                    imageVector = Icons.Default.CompareArrows,
                                    contentDescription = null,
                                    tint = LilacAccent,
                                    modifier = Modifier.size(18.dp)
                                )
                                Column {
                                    Text(
                                        text = "Dual Transmission Overlay",
                                        style = MaterialTheme.typography.bodySmall,
                                        fontWeight = FontWeight.SemiBold,
                                        color = TextWhite
                                    )
                                    Text(
                                        text = "Compare Inflation vs Central Bank Policy Rate",
                                        style = MaterialTheme.typography.labelSmall,
                                        fontSize = 9.sp,
                                        color = TextTertiary
                                    )
                                }
                            }

                            Box(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(8.dp))
                                    .background(
                                        if (uiState.showOverlay) LilacAccent else SlateSurfaceVariant
                                    )
                                    .clickable { viewModel.toggleOverlay(!uiState.showOverlay) }
                                    .padding(horizontal = 10.dp, vertical = 6.dp)
                            ) {
                                Text(
                                    text = if (uiState.showOverlay) "Active" else "Enable",
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = if (uiState.showOverlay) LilacDeep else TextSecondary
                                )
                            }
                        }

                        if (uiState.showOverlay) {
                            DualMacroOverlayChart(
                                series1 = currentCountry.getSeries(uiState.selectedHorizon, MacroIndicatorType.INFLATION),
                                series2 = currentCountry.getSeries(uiState.selectedHorizon, MacroIndicatorType.INTEREST_RATE),
                                name1 = "Inflation",
                                name2 = "Policy Rate",
                                color1 = LilacAccent,
                                color2 = CyanAccent
                            )
                        } else {
                            InteractiveMacroLineChart(
                                dataPoints = currentCountry.getSeries(uiState.selectedHorizon, MacroIndicatorType.INFLATION),
                                indicatorType = MacroIndicatorType.INFLATION,
                                lineColor = LilacAccent,
                                targetBenchmark = currentCountry.targetInflation
                            )
                        }
                    }
                }
                MacroDashboardTab.INFLATION -> {
                    InteractiveMacroLineChart(
                        dataPoints = currentCountry.getSeries(uiState.selectedHorizon, MacroIndicatorType.INFLATION),
                        indicatorType = MacroIndicatorType.INFLATION,
                        lineColor = LilacAccent,
                        targetBenchmark = currentCountry.targetInflation
                    )
                }
                MacroDashboardTab.RATES -> {
                    InteractiveMacroLineChart(
                        dataPoints = currentCountry.getSeries(uiState.selectedHorizon, MacroIndicatorType.INTEREST_RATE),
                        indicatorType = MacroIndicatorType.INTEREST_RATE,
                        lineColor = CyanAccent,
                        targetBenchmark = currentCountry.neutralRateEstimate
                    )
                }
                MacroDashboardTab.GDP -> {
                    InteractiveMacroLineChart(
                        dataPoints = currentCountry.getSeries(uiState.selectedHorizon, MacroIndicatorType.GDP_GROWTH),
                        indicatorType = MacroIndicatorType.GDP_GROWTH,
                        lineColor = EmeraldAccent
                    )
                }
                MacroDashboardTab.JOBS -> {
                    InteractiveMacroLineChart(
                        dataPoints = currentCountry.getSeries(uiState.selectedHorizon, MacroIndicatorType.UNEMPLOYMENT),
                        indicatorType = MacroIndicatorType.UNEMPLOYMENT,
                        lineColor = CrimsonRisk
                    )
                }
                MacroDashboardTab.QUADRANT -> {
                    MacroCycleQuadrantRadar(profile = currentCountry)
                }
                MacroDashboardTab.PEERS -> {
                    GlobalPeerComparisonBarChart(
                        allCountries = MacroDataSet.allCountries,
                        activeIndicator = MacroIndicatorType.INFLATION,
                        selectedCountryId = currentCountry.id,
                        onSelectCountry = { viewModel.selectCountry(it) }
                    )
                }
            }
        }

        // 6. CladRb Commentary & Quantitative Market Implications Card
        item {
            CladRbMacroAdvisorCard(
                commentary = commentary,
                onSaveToDossier = { viewModel.saveMacroReportToDossier() }
            )
        }

        // 7. Interactive CladRb AI Deep Dive Scenario Query
        item {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(SlateSurface, RoundedCornerShape(20.dp))
                    .border(1.dp, SlateBorder, RoundedCornerShape(20.dp))
                    .padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.AutoAwesome,
                        contentDescription = null,
                        tint = LilacAccent,
                        modifier = Modifier.size(18.dp)
                    )
                    Text(
                        text = "Consult CladRb on Macro Scenarios",
                        style = MaterialTheme.typography.titleSmall,
                        fontWeight = FontWeight.SemiBold,
                        color = TextWhite
                    )
                }

                Text(
                    text = "Request custom stress tests, currency carry trade dynamics, or rate cut impact modeling on ${currentCountry.name}.",
                    fontSize = 11.sp,
                    color = TextSecondary
                )

                // Quick Question Prompt Chips
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .horizontalScroll(rememberScrollState()),
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    val samplePrompts = listOf(
                        "Simulate 50 bps Rate Cut",
                        "Evaluate Currency Carry Risk",
                        "Equities Valuation Sensitivity",
                        "Stagflation Stress Test"
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

                // Query text input & submit
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    OutlinedTextField(
                        value = customQuestionText,
                        onValueChange = { customQuestionText = it },
                        placeholder = {
                            Text("Ask CladRb a macro question...", fontSize = 12.sp, color = TextTertiary)
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
                        modifier = Modifier
                            .weight(1f)
                            .testTag("macro_ai_query_input")
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
                            .testTag("macro_ai_query_btn")
                    ) {
                        if (uiState.isAiGenerating) {
                            CircularProgressIndicator(
                                modifier = Modifier.size(18.dp),
                                color = LilacDeep,
                                strokeWidth = 2.dp
                            )
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

                // Result display
                uiState.customAiAnalysis?.let { analysis ->
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(SlateSurfaceVariant, RoundedCornerShape(12.dp))
                            .border(1.dp, LilacAccent.copy(alpha = 0.4f), RoundedCornerShape(12.dp))
                            .padding(12.dp)
                    ) {
                        Text(
                            text = analysis,
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
private fun CountryPill(
    flag: String,
    name: String,
    isSelected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier
            .background(
                if (isSelected) LilacDeep.copy(alpha = 0.6f) else SlateSurface,
                RoundedCornerShape(20.dp)
            )
            .border(
                1.dp,
                if (isSelected) LilacAccent else SlateBorder,
                RoundedCornerShape(20.dp)
            )
            .clickable(onClick = onClick)
            .padding(horizontal = 12.dp, vertical = 7.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(6.dp)
    ) {
        Text(flag, fontSize = 15.sp)
        Text(
            text = name,
            style = MaterialTheme.typography.labelMedium,
            fontSize = 11.sp,
            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
            color = if (isSelected) LilacAccent else TextPrimary
        )
    }
}
