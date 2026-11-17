package upca.example.weathersearch.navigation

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Bookmark
import androidx.compose.material.icons.filled.Cloud
import androidx.compose.ui.graphics.vector.ImageVector

// Rotas da aplicação organizadas através de sealed class
sealed class Screen(
    val route: String,
    val title: String,
    val icon: ImageVector? = null,
    val isBaseScreen: Boolean = true
) {
    // Ecrã principal de pesquisa e previsão meteorológica
    object Home : Screen(
        route = "home",
        title = "Weather Search",
        icon = Icons.Default.Cloud,
        isBaseScreen = true
    )

    // Ecrã com a lista de cidades guardadas
    object Favorites : Screen(
        route = "favorites",
        title = "Cidades Guardadas",
        icon = Icons.Default.Bookmark,
        isBaseScreen = true
    )

    // Ecrã de detalhe para exibição dos dados básicos do dia selecionado
    object Detail : Screen(
        route = "detail/{dayDate}",
        title = "Detalhe do Dia",
        icon = null,
        isBaseScreen = false
    ) {
        const val ARG_DATE = "dayDate"
        fun createRoute(date: String): String = "detail/$date"
    }

    companion object {
        // Ecrãs apresentados na barra de navegação inferior
        val bottomNavScreens = listOf(Home, Favorites)

        // Resolve o ecrã correspondente à rota fornecida
        fun fromRoute(route: String?): Screen {
            return when {
                route == null -> Home
                route == Home.route -> Home
                route == Favorites.route -> Favorites
                route.startsWith("detail/") -> Detail
                else -> Home
            }
        }
    }
}
