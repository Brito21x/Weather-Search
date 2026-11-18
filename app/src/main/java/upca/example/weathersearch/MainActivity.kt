package upca.example.weathersearch

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import upca.example.weathersearch.navigation.WeatherApp
import upca.example.weathersearch.ui.theme.WeatherSearchTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            WeatherSearchTheme {
                WeatherApp()
            }
        }
    }
}
