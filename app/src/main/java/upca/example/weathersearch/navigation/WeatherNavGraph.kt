package upca.example.weathersearch.navigation

import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import upca.example.weathersearch.ui.components.WeatherBottomBar
import upca.example.weathersearch.ui.components.WeatherTopBar
import upca.example.weathersearch.ui.home.FavoritesScreen
import upca.example.weathersearch.ui.home.ForecastDetailScreen
import upca.example.weathersearch.ui.home.HomeScreen
import upca.example.weathersearch.ui.home.WeatherUiState
import upca.example.weathersearch.ui.home.WeatherViewModel

// Estrutura de navegação multi-ecrã com Scaffold, TopBar e BottomBar
@Composable
fun WeatherApp(
    navController: NavHostController = rememberNavController(),
    viewModel: WeatherViewModel = viewModel()
) {
    val context = LocalContext.current
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route

    // Distingue ecrãs base de ecrãs de detalhe
    val currentScreen = Screen.fromRoute(currentRoute)
    val isBaseScreen = currentScreen.isBaseScreen

    val uiState by viewModel.uiState.collectAsState()
    val isSuccess = uiState is WeatherUiState.Success
    val isBookmarked = viewModel.isCurrentCityBookmarked()

    Scaffold(
        topBar = {
            WeatherTopBar(
                title = if (!isBaseScreen && currentScreen == Screen.Detail) {
                    val dateArg = navBackStackEntry?.arguments?.getString(Screen.Detail.ARG_DATE)
                    if (dateArg != null) "Previsão: $dateArg" else currentScreen.title
                } else {
                    currentScreen.title
                },
                isBaseScreen = isBaseScreen,
                canShare = isSuccess && isBaseScreen && currentScreen == Screen.Home,
                canBookmark = isSuccess && isBaseScreen && currentScreen == Screen.Home,
                isBookmarked = isBookmarked,
                onBackClick = {
                    navController.popBackStack()
                },
                onShareClick = {
                    viewModel.shareCurrentWeather(context)
                },
                onBookmarkClick = {
                    viewModel.toggleFavoriteCurrentCity()
                }
            )
        },
        bottomBar = {
            // A barra inferior é visível apenas nos ecrãs base
            if (isBaseScreen) {
                WeatherBottomBar(
                    currentRoute = currentRoute,
                    onNavigateToRoute = { route ->
                        navController.navigate(route) {
                            popUpTo(Screen.Home.route) {
                                saveState = true
                            }
                            launchSingleTop = true
                            restoreState = true
                        }
                    }
                )
            }
        }
    ) { innerPadding ->
        NavHost(
            navController = navController,
            startDestination = Screen.Home.route,
            modifier = Modifier.padding(innerPadding)
        ) {
            composable(Screen.Home.route) {
                HomeScreen(
                    viewModel = viewModel,
                    onNavigateToDetail = { day ->
                        viewModel.selectForecastDay(day)
                        navController.navigate(Screen.Detail.createRoute(day.date))
                    }
                )
            }

            composable(Screen.Favorites.route) {
                FavoritesScreen(
                    viewModel = viewModel,
                    onCitySelected = {
                        navController.navigate(Screen.Home.route) {
                            popUpTo(Screen.Home.route) { inclusive = false }
                            launchSingleTop = true
                        }
                    }
                )
            }

            composable(
                route = Screen.Detail.route,
                arguments = listOf(
                    navArgument(Screen.Detail.ARG_DATE) { type = NavType.StringType }
                )
            ) { backStackEntry ->
                val date = backStackEntry.arguments?.getString(Screen.Detail.ARG_DATE) ?: ""
                ForecastDetailScreen(
                    viewModel = viewModel,
                    dayDate = date
                )
            }
        }
    }
}
