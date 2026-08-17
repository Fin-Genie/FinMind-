package com.example.ui.navigation

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AutoGraph
import androidx.compose.material.icons.filled.Bookmark
import androidx.compose.material.icons.filled.Newspaper
import androidx.compose.material.icons.filled.Psychology
import androidx.compose.material.icons.filled.Security
import androidx.compose.material.icons.filled.ShowChart
import androidx.compose.material.icons.filled.Timeline
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
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
import com.example.ui.screens.news.NewsImpactScreen
import com.example.ui.screens.portfolio.MarkowitzOptimizerScreen
import com.example.ui.screens.risk.RiskAndGreeksScreen
import com.example.ui.screens.timeseries.TimeSeriesVolatilityScreen
import com.example.ui.screens.valuation.DcfValuationScreen
import com.example.ui.theme.LilacAccent
import com.example.ui.theme.LilacDeep
import com.example.ui.theme.ObsidianBg
import com.example.ui.theme.SlateBorder
import com.example.ui.theme.TextPrimary
import com.example.ui.theme.TextSecondary
import com.example.ui.theme.TextTertiary
import com.example.ui.viewmodel.AdvisorViewModel
import com.example.ui.viewmodel.DossierViewModel
import com.example.ui.viewmodel.MarkowitzViewModel
import com.example.ui.viewmodel.NewsImpactViewModel
import com.example.ui.viewmodel.RiskGreeksViewModel
import com.example.ui.viewmodel.TimeSeriesViewModel
import com.example.ui.viewmodel.ValuationViewModel

sealed class Screen(val route: String, val title: String, val icon: ImageVector) {
    data object Advisor : Screen("advisor", "CladRb", Icons.Default.Psychology)
    data object NewsImpact : Screen("news", "News & AI", Icons.Default.Newspaper)
    data object Markowitz : Screen("markowitz", "Frontier", Icons.Default.AutoGraph)
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
        Screen.NewsImpact,
        Screen.Markowitz,
        Screen.TimeSeries,
        Screen.Valuation,
        Screen.Risk,
        Screen.Dossier
    )

    val snackbarHostState = remember { SnackbarHostState() }
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route ?: Screen.Advisor.route

    val advisorViewModel: AdvisorViewModel = viewModel()
    val newsImpactViewModel: NewsImpactViewModel = viewModel()
    val markowitzViewModel: MarkowitzViewModel = viewModel()
    val timeSeriesViewModel: TimeSeriesViewModel = viewModel()
    val valuationViewModel: ValuationViewModel = viewModel()
    val riskGreeksViewModel: RiskGreeksViewModel = viewModel()
    val dossierViewModel: DossierViewModel = viewModel()

    Scaffold(
        modifier = Modifier.fillMaxSize(),
        containerColor = ObsidianBg,
        snackbarHost = { SnackbarHost(snackbarHostState) },
        bottomBar = {
            NavigationBar(
                containerColor = ObsidianBg,
                modifier = Modifier.border(1.dp, SlateBorder)
            ) {
                items.forEach { screen ->
                    val selected = currentRoute == screen.route
                    NavigationBarItem(
                        icon = {
                            Icon(
                                imageVector = screen.icon,
                                contentDescription = screen.title,
                                modifier = Modifier.size(19.dp)
                            )
                        },
                        label = {
                            Text(
                                text = screen.title,
                                fontSize = 8.5.sp,
                                fontWeight = if (selected) FontWeight.SemiBold else FontWeight.Normal,
                                maxLines = 1
                            )
                        },
                        selected = selected,
                        onClick = {
                            if (currentRoute != screen.route) {
                                navController.navigate(screen.route) {
                                    popUpTo(navController.graph.findStartDestination().id) {
                                        saveState = true
                                    }
                                    launchSingleTop = true
                                    restoreState = true
                                }
                            }
                        },
                        colors = NavigationBarItemDefaults.colors(
                            selectedIconColor = LilacAccent,
                            selectedTextColor = LilacAccent,
                            unselectedIconColor = TextTertiary,
                            unselectedTextColor = TextTertiary,
                            indicatorColor = LilacDeep.copy(alpha = 0.5f)
                        ),
                        modifier = Modifier.testTag("nav_${screen.route}")
                    )
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
