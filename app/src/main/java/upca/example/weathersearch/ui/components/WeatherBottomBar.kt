package upca.example.weathersearch.ui.components

import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import upca.example.weathersearch.navigation.Screen
import upca.example.weathersearch.ui.theme.WeatherSearchTheme

// Barra de navegação inferior com preservação da rota via rememberSaveable
@Composable
fun WeatherBottomBar(
    currentRoute: String?,
    onNavigateToRoute: (String) -> Unit
) {
    // Preserva a seleção do item ativo entre recomposições
    var selectedRoute by rememberSaveable { mutableStateOf(Screen.Home.route) }

    // Sincroniza a rota ativa atual
    val activeRoute = currentRoute ?: selectedRoute

    NavigationBar(
        containerColor = MaterialTheme.colorScheme.surface,
        tonalElevation = 3.dp
    ) {
        Screen.bottomNavScreens.forEach { screen ->
            val isSelected = activeRoute == screen.route

            NavigationBarItem(
                selected = isSelected,
                onClick = {
                    selectedRoute = screen.route
                    onNavigateToRoute(screen.route)
                },
                icon = {
                    screen.icon?.let { icon ->
                        Icon(
                            imageVector = icon,
                            contentDescription = screen.title
                        )
                    }
                },
                label = {
                    Text(
                        text = screen.title,
                        fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal
                    )
                },
                colors = NavigationBarItemDefaults.colors(
                    selectedIconColor = MaterialTheme.colorScheme.onPrimary,
                    selectedTextColor = MaterialTheme.colorScheme.primary,
                    indicatorColor = MaterialTheme.colorScheme.primary,
                    unselectedIconColor = MaterialTheme.colorScheme.onSurfaceVariant,
                    unselectedTextColor = MaterialTheme.colorScheme.onSurfaceVariant
                )
            )
        }
    }
}

// Pré-visualização da barra de navegação inferior
@Preview(showBackground = true)
@Composable
fun WeatherBottomBarPreview() {
    WeatherSearchTheme {
        WeatherBottomBar(
            currentRoute = Screen.Home.route,
            onNavigateToRoute = {}
        )
    }
}
