package upca.example.weathersearch.navigation

import android.content.Intent
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
import upca.example.weathersearch.ui.favorites.FavoritesScreen
import upca.example.weathersearch.ui.home.HomeScreen
import upca.example.weathersearch.ui.home.HomeViewModel
import upca.example.weathersearch.ui.home.HomeUiState

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
    val homeUiState by homeViewModel.uiState.collectAsState()
    val isBookmarked by homeViewModel.isCurrentCityBookmarked.collectAsState()
    val canShareOrBookmark = currentScreen == Screen.Home && homeUiState is HomeUiState.Success

    Scaffold(
        topBar = {
            val detailDate = navBackStackEntry?.arguments?.getString(Screen.Detail.ARG_DATE)
            WeatherTopBar(
                title = if (currentScreen == Screen.Detail && detailDate != null) "Previsão: $detailDate" else currentScreen.title,
                isBaseScreen = currentScreen.isBaseScreen,
                canShare = canShareOrBookmark,
                canBookmark = canShareOrBookmark,
                isBookmarked = isBookmarked,
                onBackClick = { navController.popBackStack() },
                onShareClick = {
                    homeViewModel.getShareSummary()?.let { summary ->
                        val sendIntent = Intent().apply {
                            action = Intent.ACTION_SEND
                            putExtra(Intent.EXTRA_TEXT, summary)
                            type = "text/plain"
                        }
                        context.startActivity(Intent.createChooser(sendIntent, "Partilhar Meteorologia"))
                    }
                },
                onBookmarkClick = { homeViewModel.toggleFavoriteCurrentCity() }
            )
        },
        bottomBar = {
            if (currentScreen.isBaseScreen) {
                WeatherBottomBar(
                    currentRoute = currentRoute,
                    onNavigateToRoute = { route ->
                        navController.navigate(route) {
                            popUpTo(Screen.Home.route) { saveState = true }
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
                FavoritesScreen(
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
                val date = backStackEntry.arguments?.getString(Screen.Detail.ARG_DATE).orEmpty()
                ForecastDetailScreen(dayDate = date)
            }
        }
    }
}
