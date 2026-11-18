package upca.example.weathersearch.ui.components

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Bookmark
import androidx.compose.material.icons.filled.BookmarkBorder
import androidx.compose.material.icons.filled.Share
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import upca.example.weathersearch.ui.theme.WeatherSearchTheme

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun WeatherTopBar(
    title: String,
    isBaseScreen: Boolean,
    canShare: Boolean = false,
    isBookmarked: Boolean = false,
    canBookmark: Boolean = false,
    onBackClick: () -> Unit = {},
    onShareClick: () -> Unit = {},
    onBookmarkClick: () -> Unit = {}
) {
    TopAppBar(
        title = {
            Text(
                text = title,
                fontWeight = FontWeight.Bold,
                style = MaterialTheme.typography.titleLarge
            )
        },
        navigationIcon = {
            if (!isBaseScreen) {
                IconButton(onClick = onBackClick) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                        contentDescription = "Voltar"
                    )
                }
            }
        },
        actions = {
            if (canBookmark) {
                IconButton(onClick = onBookmarkClick) {
                    Icon(
                        imageVector = if (isBookmarked) Icons.Default.Bookmark else Icons.Default.BookmarkBorder,
                        contentDescription = if (isBookmarked) "Remover dos Favoritos" else "Guardar nos Favoritos",
                        tint = if (isBookmarked) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurface
                    )
                }
            }

            if (canShare) {
                IconButton(onClick = onShareClick) {
                    Icon(
                        imageVector = Icons.Default.Share,
                        contentDescription = "Partilhar Meteorologia"
                    )
                }
            }
        },
        colors = TopAppBarDefaults.topAppBarColors(
            containerColor = MaterialTheme.colorScheme.surface,
            titleContentColor = MaterialTheme.colorScheme.onSurface,
            navigationIconContentColor = MaterialTheme.colorScheme.onSurface,
            actionIconContentColor = MaterialTheme.colorScheme.onSurface
        )
    )
}

@Preview(showBackground = true)
@Composable
fun WeatherTopBarBasePreview() {
    WeatherSearchTheme {
        WeatherTopBar(
            title = "Weather Search",
            isBaseScreen = true,
            canShare = true,
            canBookmark = true,
            isBookmarked = true
        )
    }
}

@Preview(showBackground = true)
@Composable
fun WeatherTopBarDetailPreview() {
    WeatherSearchTheme {
        WeatherTopBar(
            title = "Detalhe: 2026-11-18",
            isBaseScreen = false
        )
    }
}
