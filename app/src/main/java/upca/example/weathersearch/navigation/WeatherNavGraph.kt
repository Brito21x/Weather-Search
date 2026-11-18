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
import upca.example.weathersearch.ui.detail.ForecastDetailScreen
import upca.example.weathersearch.ui.detail.ForecastDetailViewModel
import upca.example.weathersearch.ui.favorites.FavoritesScreen
import upca.example.weathersearch.ui.favorites.FavoritesViewModel
import upca.example.weathersearch.ui.home.HomeScreen
import upca.example.weathersearch.ui.home.HomeViewModel
import upca.example.weathersearch.ui.home.WeatherUiState

// Estrutura principal da aplicação: coordena Scaffold, TopBar adaptativa, BottomBar e o grafo de rotas
@Composable
fun WeatherApp(
    navController: NavHostController = rememberNavController(),
    homeViewModel: HomeViewModel = viewModel()
) {
    val context = LocalContext.current
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route

    val currentScreen = Screen.fromRoute(currentRoute)
    val isBaseScreen = currentScreen.isBaseScreen

    val homeUiState by homeViewModel.uiState.collectAsState()
    val isSuccess = homeUiState is WeatherUiState.Success
    val isBookmarked = homeViewModel.isCurrentCityBookmarked()

    Scaffold(
        topBar = {
            // Configura dinamicamente título e ações (partilha, favoritos, retroceder) conforme a rota ativa
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
                    homeViewModel.shareCurrentWeather(context)
                },
                onBookmarkClick = {
                    homeViewModel.toggleFavoriteCurrentCity()
                }
            )
        },
        bottomBar = {
            // A barra inferior é visível apenas nos ecrãs de nível raiz, preservando o estado de navegação
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
                    viewModel = homeViewModel,
                    onNavigateToDetail = { day ->
                        navController.navigate(Screen.Detail.createRoute(day.date))
                    }
                )
            }

            composable(Screen.Favorites.route) {
                val favoritesViewModel: FavoritesViewModel = viewModel()
                FavoritesScreen(
                    viewModel = favoritesViewModel,
                    onCitySelected = { city ->
                        homeViewModel.searchWeather(city)
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
                val detailViewModel: ForecastDetailViewModel = viewModel()
                ForecastDetailScreen(
                    viewModel = detailViewModel,
                    dayDate = date
                )
            }
        }
    }
}
