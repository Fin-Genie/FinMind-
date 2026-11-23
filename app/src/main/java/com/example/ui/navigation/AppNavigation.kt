package com.example.ui.navigation

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
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AutoGraph
import androidx.compose.material.icons.filled.Bookmark
import androidx.compose.material.icons.filled.Newspaper
import androidx.compose.material.icons.filled.Psychology
import androidx.compose.material.icons.filled.Public
import androidx.compose.material.icons.filled.QueryStats
import androidx.compose.material.icons.filled.Security
import androidx.compose.material.icons.filled.ShowChart
import androidx.compose.material.icons.filled.Timeline
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.example.ui.screens.advisor.AdvisorChatScreen
import com.example.ui.screens.dossier.SavedDossiersScreen
import com.example.ui.screens.macro.MacroDashboardScreen
import com.example.ui.screens.montecarlo.MonteCarloScreen
import com.example.ui.screens.news.NewsImpactScreen
import com.example.ui.screens.portfolio.MarkowitzOptimizerScreen
import com.example.ui.screens.risk.RiskAndGreeksScreen
import com.example.ui.screens.timeseries.TimeSeriesVolatilityScreen
import com.example.ui.screens.valuation.DcfValuationScreen
import com.example.ui.theme.LilacAccent
import com.example.ui.theme.LilacDeep
import com.example.ui.theme.ObsidianBg
import com.example.ui.theme.SlateBorder
import com.example.ui.theme.SlateSurface
import com.example.ui.theme.TextPrimary
import com.example.ui.theme.TextSecondary
import com.example.ui.theme.TextTertiary
import com.example.ui.viewmodel.AdvisorViewModel
import com.example.ui.viewmodel.DossierViewModel
import com.example.ui.viewmodel.MacroViewModel
import com.example.ui.viewmodel.MarkowitzViewModel
import com.example.ui.viewmodel.MonteCarloViewModel
import com.example.ui.viewmodel.NewsImpactViewModel
import com.example.ui.viewmodel.RiskGreeksViewModel
import com.example.ui.viewmodel.TimeSeriesViewModel
import com.example.ui.viewmodel.ValuationViewModel

sealed class Screen(val route: String, val title: String, val icon: ImageVector) {
    data object Advisor : Screen("advisor", "CladRb", Icons.Default.Psychology)
    data object Macro : Screen("macro", "Macro", Icons.Default.Public)
    data object NewsImpact : Screen("news", "News & AI", Icons.Default.Newspaper)
    data object Markowitz : Screen("markowitz", "Frontier", Icons.Default.AutoGraph)
    data object MonteCarlo : Screen("montecarlo", "Monte Carlo", Icons.Default.QueryStats)
    data object TimeSeries : Screen("timeseries", "Econometrics", Icons.Default.Timeline)
    data object Valuation : Screen("valuation", "Valuation", Icons.Default.ShowChart)
    data object Risk : Screen("risk", "Risk", Icons.Default.Security)
    data object Dossier : Screen("dossier", "Dossier", Icons.Default.Bookmark)
}

@Composable
fun MainAppNavigation(
    navController: NavHostController = rememberNavController()
) {
    val items = listOf(
        Screen.Advisor,
        Screen.Macro,
        Screen.NewsImpact,
        Screen.Markowitz,
        Screen.MonteCarlo,
        Screen.TimeSeries,
        Screen.Valuation,
        Screen.Risk,
        Screen.Dossier
    )

    val snackbarHostState = remember { SnackbarHostState() }
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route ?: Screen.Advisor.route

    val advisorViewModel: AdvisorViewModel = viewModel()
    val macroViewModel: MacroViewModel = viewModel()
    val newsImpactViewModel: NewsImpactViewModel = viewModel()
    val markowitzViewModel: MarkowitzViewModel = viewModel()
    val monteCarloViewModel: MonteCarloViewModel = viewModel()
    val timeSeriesViewModel: TimeSeriesViewModel = viewModel()
    val valuationViewModel: ValuationViewModel = viewModel()
    val riskGreeksViewModel: RiskGreeksViewModel = viewModel()
    val dossierViewModel: DossierViewModel = viewModel()

    Scaffold(
        modifier = Modifier.fillMaxSize(),
        containerColor = ObsidianBg,
        snackbarHost = { SnackbarHost(snackbarHostState) },
        bottomBar = {
            Surface(
                color = ObsidianBg,
                modifier = Modifier
                    .fillMaxWidth()
                    .border(1.dp, SlateBorder)
                    .navigationBarsPadding()
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .horizontalScroll(rememberScrollState())
                        .padding(horizontal = 6.dp, vertical = 6.dp),
                    horizontalArrangement = Arrangement.spacedBy(4.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    items.forEach { screen ->
                        val selected = currentRoute == screen.route
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(12.dp))
                                .background(
                                    if (selected) LilacDeep.copy(alpha = 0.5f) else SlateSurface.copy(alpha = 0.5f)
                                )
                                .border(
                                    1.dp,
                                    if (selected) LilacAccent.copy(alpha = 0.6f) else SlateBorder.copy(alpha = 0.4f),
                                    RoundedCornerShape(12.dp)
                                )
                                .clickable {
                                    if (currentRoute != screen.route) {
                                        navController.navigate(screen.route) {
                                            popUpTo(navController.graph.findStartDestination().id) {
                                                saveState = true
                                            }
                                            launchSingleTop = true
                                            restoreState = true
                                        }
                                    }
                                }
                                .padding(horizontal = 12.dp, vertical = 8.dp)
                                .testTag("nav_${screen.route}"),
                            contentAlignment = Alignment.Center
                        ) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(6.dp)
                            ) {
                                Icon(
                                    imageVector = screen.icon,
                                    contentDescription = screen.title,
                                    tint = if (selected) LilacAccent else TextTertiary,
                                    modifier = Modifier.size(17.dp)
                                )
                                Text(
                                    text = screen.title,
                                    fontSize = 11.sp,
                                    fontWeight = if (selected) FontWeight.Bold else FontWeight.Medium,
                                    color = if (selected) LilacAccent else TextSecondary,
                                    maxLines = 1
                                )
                            }
                        }
                    }
                }
            }
        }
    ) { innerPadding ->
        NavHost(
            navController = navController,
            startDestination = Screen.Advisor.route,
            modifier = Modifier.padding(innerPadding)
        ) {
            composable(Screen.Advisor.route) {
                AdvisorChatScreen(
                    viewModel = advisorViewModel,
                    snackbarHostState = snackbarHostState
                )
            }
            composable(Screen.Macro.route) {
                MacroDashboardScreen(
                    viewModel = macroViewModel,
                    snackbarHostState = snackbarHostState
                )
            }
            composable(Screen.NewsImpact.route) {
                NewsImpactScreen(
                    viewModel = newsImpactViewModel,
                    snackbarHostState = snackbarHostState
                )
            }
            composable(Screen.Markowitz.route) {
                MarkowitzOptimizerScreen(
                    viewModel = markowitzViewModel,
                    snackbarHostState = snackbarHostState
                )
            }
            composable(Screen.MonteCarlo.route) {
                MonteCarloScreen(
                    viewModel = monteCarloViewModel,
                    snackbarHostState = snackbarHostState
                )
            }
            composable(Screen.TimeSeries.route) {
                TimeSeriesVolatilityScreen(
                    viewModel = timeSeriesViewModel,
                    snackbarHostState = snackbarHostState
                )
            }
            composable(Screen.Valuation.route) {
                DcfValuationScreen(
                    viewModel = valuationViewModel,
                    snackbarHostState = snackbarHostState
                )
            }
            composable(Screen.Risk.route) {
                RiskAndGreeksScreen(
                    viewModel = riskGreeksViewModel,
                    snackbarHostState = snackbarHostState
                )
            }
            composable(Screen.Dossier.route) {
                SavedDossiersScreen(
                    viewModel = dossierViewModel
                )
            }
        }
    }
}
