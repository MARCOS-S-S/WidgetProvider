package com.marcossilqueira.widgetprovider

import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Dashboard
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.AudioFile
import androidx.compose.material.icons.filled.Tune
import androidx.compose.material.icons.filled.Palette
import androidx.compose.material.icons.filled.Sync
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.platform.LocalContext
import com.marcossilqueira.widgetprovider.ui.theme.WidgetProviderTheme

class MainActivity : ComponentActivity() {
    private var spotifyAuthService: SpotifyAuthService? = null
    
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            WidgetProviderTheme {
                AppNavigation()
            }
        }
    }
    
    override fun onNewIntent(intent: Intent?) {
        super.onNewIntent(intent)
        // Processar callback do Spotify
        spotifyAuthService?.handleAuthCallback(intent)
    }
    
    fun setSpotifyAuthService(authService: SpotifyAuthService) {
        this.spotifyAuthService = authService
    }
}

@Composable
fun AppNavigation() {
    var currentScreen by remember { mutableStateOf("home") }
    
    when (currentScreen) {
        "home" -> HomeScreen(
            onGetStartedClick = { currentScreen = "dashboard" }
        )
        "dashboard" -> DashboardScreen(
            onBackClick = { currentScreen = "home" },
            onSpotifyWidgetClick = { currentScreen = "spotify_widget" }
        )
        "spotify_widget" -> SpotifyWidgetScreen(
            onBackClick = { currentScreen = "dashboard" },
            onSpotifyAuthClick = { currentScreen = "spotify_auth" }
        )
        "spotify_auth" -> SpotifyAuthScreen(
            authService = SpotifyAuthService(LocalContext.current),
            onBackClick = { currentScreen = "spotify_widget" },
            onAuthSuccess = { currentScreen = "spotify_widget" }
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    onGetStartedClick: () -> Unit = {},
    modifier: Modifier = Modifier
) {
    Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
        LazyColumn(
            modifier = modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
        item {
            // Header Section
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.primaryContainer
                ),
                elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
            ) {
                Column(
                    modifier = Modifier.padding(24.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Icon(
                        imageVector = Icons.Filled.Dashboard,
                        contentDescription = "Widget Provider",
                        modifier = Modifier.size(64.dp),
                        tint = MaterialTheme.colorScheme.primary
                    )
                    Spacer(modifier = Modifier.height(16.dp))
                    Text(
                        text = "Widget Provider",
                        style = MaterialTheme.typography.headlineMedium,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onPrimaryContainer
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = "Crie widgets inteligentes para seus apps favoritos",
                        style = MaterialTheme.typography.bodyLarge,
                        textAlign = TextAlign.Center,
                        color = MaterialTheme.colorScheme.onPrimaryContainer
                    )
                }
            }
        }

        item {
            Text(
                text = "Recursos Disponíveis",
                style = MaterialTheme.typography.headlineSmall,
                fontWeight = FontWeight.SemiBold,
                color = MaterialTheme.colorScheme.onBackground
            )
        }

        items(getFeatureList()) { feature ->
            FeatureCard(feature = feature)
        }

        item {
            Spacer(modifier = Modifier.height(16.dp))
            Button(
                onClick = onGetStartedClick,
                modifier = Modifier.fillMaxWidth(),
                colors = ButtonDefaults.buttonColors(
                    containerColor = MaterialTheme.colorScheme.primary
                )
            ) {
                Icon(
                    imageVector = Icons.Filled.PlayArrow,
                    contentDescription = null,
                    modifier = Modifier.size(20.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = "Começar Agora",
                    style = MaterialTheme.typography.titleMedium
                )
            }
        }
        }
    }
}

@Composable
fun FeatureCard(feature: Feature) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = feature.icon,
                contentDescription = feature.title,
                modifier = Modifier.size(32.dp),
                tint = MaterialTheme.colorScheme.primary
            )
            Spacer(modifier = Modifier.width(16.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = feature.title,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.SemiBold,
                    color = MaterialTheme.colorScheme.onSurface
                )
                Text(
                    text = feature.description,
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
    }
}

data class Feature(
    val title: String,
    val description: String,
    val icon: ImageVector
)

fun getFeatureList(): List<Feature> {
    return listOf(
        Feature(
            title = "Controle de Música",
            description = "Controle reprodução do Spotify diretamente do widget",
            icon = Icons.Filled.AudioFile
        ),
        Feature(
            title = "Widgets Personalizados",
            description = "Crie widgets únicos para seus apps favoritos",
            icon = Icons.Filled.Tune
        ),
        Feature(
            title = "Interface Intuitiva",
            description = "Design moderno seguindo Material Design",
            icon = Icons.Filled.Palette
        ),
        Feature(
            title = "Atualizações em Tempo Real",
            description = "Widgets sempre sincronizados com seus apps",
            icon = Icons.Filled.Sync
        )
    )
}

@Preview(showBackground = true)
@Composable
fun HomeScreenPreview() {
    WidgetProviderTheme {
        HomeScreen()
    }
}

@Preview(showBackground = true)
@Composable
fun AppNavigationPreview() {
    WidgetProviderTheme {
        AppNavigation()
    }
}