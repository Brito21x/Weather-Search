package upca.example.weathersearch

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import upca.example.weathersearch.navigation.WeatherApp
import upca.example.weathersearch.ui.theme.WeatherSearchTheme

// Atividade principal e ponto de entrada da aplicação Android
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        // Ativa o desenho edge-to-edge nas barras de sistema
        enableEdgeToEdge()
        setContent {
            WeatherSearchTheme {
                // Inicia o grafo de navegação e componentes da aplicação
                WeatherApp()
            }
        }
    }
}
