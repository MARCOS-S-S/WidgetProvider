package com.marcossilqueira.widgetprovider

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.selection.selectable
import androidx.compose.foundation.selection.selectableGroup
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.marcossilqueira.widgetprovider.ui.theme.WidgetProviderTheme

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SpotifyWidgetScreen(
    onBackClick: () -> Unit = {},
    onSpotifyAuthClick: () -> Unit = {},
    modifier: Modifier = Modifier
) {
    var selectedWidgetType by remember { mutableStateOf(WidgetSize.SMALL) }
    var selectedStyle by remember { mutableStateOf(WidgetStyle.MODERN) }
    var showAdvancedSettings by remember { mutableStateOf(false) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Row(
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = Icons.Filled.AudioFile,
                            contentDescription = null,
                            modifier = Modifier.size(24.dp),
                            tint = MaterialTheme.colorScheme.primary
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "Widget Spotify",
                            style = MaterialTheme.typography.headlineSmall,
                            fontWeight = FontWeight.Bold
                        )
                    }
                },
                navigationIcon = {
                    IconButton(onClick = onBackClick) {
                        Icon(
                            imageVector = Icons.Filled.ArrowBack,
                            contentDescription = "Voltar"
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primaryContainer,
                    titleContentColor = MaterialTheme.colorScheme.onPrimaryContainer,
                    navigationIconContentColor = MaterialTheme.colorScheme.onPrimaryContainer
                )
            )
        }
    ) { innerPadding ->
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
                        containerColor = MaterialTheme.colorScheme.surfaceVariant
                    ),
                    elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
                ) {
                    Column(
                        modifier = Modifier.padding(20.dp)
                    ) {
                        Text(
                            text = "Configure seu Widget Spotify",
                            style = MaterialTheme.typography.headlineSmall,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = "Personalize o tamanho, estilo e funcionalidades do seu widget de música.",
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            }

            item {
                Text(
                    text = "Tamanho do Widget",
                    style = MaterialTheme.typography.headlineSmall,
                    fontWeight = FontWeight.SemiBold,
                    color = MaterialTheme.colorScheme.onBackground
                )
            }

            item {
                Column(
                    modifier = Modifier.selectableGroup()
                ) {
                    WidgetSize.entries.forEach { size ->
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .selectable(
                                    selected = (selectedWidgetType == size),
                                    onClick = { selectedWidgetType = size }
                                )
                                .padding(vertical = 8.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            RadioButton(
                                selected = (selectedWidgetType == size),
                                onClick = { selectedWidgetType = size }
                            )
                            Spacer(modifier = Modifier.width(12.dp))
                            Icon(
                                imageVector = size.icon,
                                contentDescription = null,
                                modifier = Modifier.size(24.dp),
                                tint = MaterialTheme.colorScheme.primary
                            )
                            Spacer(modifier = Modifier.width(12.dp))
                            Column {
                                Text(
                                    text = size.displayName,
                                    style = MaterialTheme.typography.titleMedium,
                                    fontWeight = FontWeight.SemiBold,
                                    color = MaterialTheme.colorScheme.onSurface
                                )
                                Text(
                                    text = size.description,
                                    style = MaterialTheme.typography.bodyMedium,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                        }
                    }
                }
            }

            item {
                Text(
                    text = "Estilo Visual",
                    style = MaterialTheme.typography.headlineSmall,
                    fontWeight = FontWeight.SemiBold,
                    color = MaterialTheme.colorScheme.onBackground
                )
            }

            item {
                LazyRow(
                    horizontalArrangement = Arrangement.spacedBy(12.dp),
                    contentPadding = PaddingValues(horizontal = 4.dp)
                ) {
                    items(WidgetStyle.values()) { style ->
                        StyleCard(
                            style = style,
                            isSelected = selectedStyle == style,
                            onClick = { selectedStyle = style }
                        )
                    }
                }
            }

            item {
                Text(
                    text = "Funcionalidades",
                    style = MaterialTheme.typography.headlineSmall,
                    fontWeight = FontWeight.SemiBold,
                    color = MaterialTheme.colorScheme.onBackground
                )
            }

            items(getSpotifyFeatures()) { feature ->
                FeatureToggleCard(feature = feature)
            }

            item {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Button(
                        onClick = { showAdvancedSettings = !showAdvancedSettings },
                        modifier = Modifier.weight(1f),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = MaterialTheme.colorScheme.secondary
                        )
                    ) {
                        Icon(
                            imageVector = if (showAdvancedSettings) Icons.Filled.ExpandLess else Icons.Filled.ExpandMore,
                            contentDescription = null,
                            modifier = Modifier.size(18.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("Configurações Avançadas")
                    }
                }
            }

            if (showAdvancedSettings) {
                items(getAdvancedSettings()) { setting ->
                    AdvancedSettingCard(
                        setting = setting,
                        onClick = {
                            when (setting.title) {
                                "Conectar ao Spotify" -> onSpotifyAuthClick()
                                else -> { /* TODO: Implementar outras configurações */ }
                            }
                        }
                    )
                }
            }

            item {
                Spacer(modifier = Modifier.height(16.dp))
                
                // Action Buttons
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    OutlinedButton(
                        onClick = onBackClick,
                        modifier = Modifier.weight(1f)
                    ) {
                        Text("Cancelar")
                    }
                    
                    Button(
                        onClick = { /* TODO: Implementar criação do widget */ },
                        modifier = Modifier.weight(1f),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = MaterialTheme.colorScheme.primary
                        )
                    ) {
                        Icon(
                            imageVector = Icons.Filled.Add,
                            contentDescription = null,
                            modifier = Modifier.size(18.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("Criar Widget")
                    }
                }
            }
        }
    }
}

@Composable
fun StyleCard(
    style: WidgetStyle,
    isSelected: Boolean,
    onClick: () -> Unit
) {
    Card(
        modifier = Modifier.width(120.dp),
        colors = CardDefaults.cardColors(
            containerColor = if (isSelected) 
                MaterialTheme.colorScheme.primaryContainer 
            else 
                MaterialTheme.colorScheme.surface
        ),
        elevation = CardDefaults.cardElevation(
            defaultElevation = if (isSelected) 4.dp else 2.dp
        ),
        onClick = onClick
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Icon(
                imageVector = style.icon,
                contentDescription = style.name,
                modifier = Modifier.size(32.dp),
                tint = if (isSelected) 
                    MaterialTheme.colorScheme.onPrimaryContainer 
                else 
                    MaterialTheme.colorScheme.primary
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = style.name,
                style = MaterialTheme.typography.titleSmall,
                fontWeight = FontWeight.SemiBold,
                textAlign = TextAlign.Center,
                color = if (isSelected) 
                    MaterialTheme.colorScheme.onPrimaryContainer 
                else 
                    MaterialTheme.colorScheme.onSurface
            )
        }
    }
}

@Composable
fun FeatureToggleCard(feature: SpotifyFeature) {
    var isEnabled by remember { mutableStateOf(feature.defaultEnabled) }
    
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = feature.icon,
                contentDescription = feature.title,
                modifier = Modifier.size(24.dp),
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
            Switch(
                checked = isEnabled,
                onCheckedChange = { isEnabled = it }
            )
        }
    }
}

@Composable
fun AdvancedSettingCard(
    setting: AdvancedSetting,
    onClick: () -> Unit = {}
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp),
        onClick = onClick
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = setting.icon,
                contentDescription = setting.title,
                modifier = Modifier.size(24.dp),
                tint = MaterialTheme.colorScheme.primary
            )
            Spacer(modifier = Modifier.width(16.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = setting.title,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.SemiBold,
                    color = MaterialTheme.colorScheme.onSurface
                )
                Text(
                    text = setting.description,
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
            Icon(
                imageVector = Icons.Filled.ChevronRight,
                contentDescription = "Configurar",
                tint = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}

enum class WidgetSize(
    val displayName: String,
    val description: String,
    val icon: ImageVector
) {
    SMALL("Pequeno", "2x1 células", Icons.Filled.ViewModule),
    MEDIUM("Médio", "2x2 células", Icons.Filled.ViewModule),
    LARGE("Grande", "4x2 células", Icons.Filled.ViewModule)
}

enum class WidgetStyle(
    val displayName: String,
    val icon: ImageVector
) {
    MODERN("Moderno", Icons.Filled.Palette),
    MINIMAL("Minimalista", Icons.Filled.CropSquare),
    COLORFUL("Colorido", Icons.Filled.ColorLens)
}

data class SpotifyFeature(
    val title: String,
    val description: String,
    val icon: ImageVector,
    val defaultEnabled: Boolean = true
)

data class AdvancedSetting(
    val title: String,
    val description: String,
    val icon: ImageVector
)

fun getSpotifyFeatures(): List<SpotifyFeature> {
    return listOf(
        SpotifyFeature(
            title = "Controle de Reprodução",
            description = "Play, pause, próximo, anterior",
            icon = Icons.Filled.PlayArrow,
            defaultEnabled = true
        ),
        SpotifyFeature(
            title = "Informações da Música",
            description = "Título, artista e capa do álbum",
            icon = Icons.Filled.MusicNote,
            defaultEnabled = true
        ),
        SpotifyFeature(
            title = "Barra de Progresso",
            description = "Mostrar progresso da música atual",
            icon = Icons.Filled.Timeline,
            defaultEnabled = false
        ),
        SpotifyFeature(
            title = "Volume",
            description = "Controle de volume integrado",
            icon = Icons.Filled.VolumeUp,
            defaultEnabled = false
        ),
        SpotifyFeature(
            title = "Playlist Atual",
            description = "Mostrar nome da playlist",
            icon = Icons.Filled.PlaylistPlay,
            defaultEnabled = true
        )
    )
}

fun getAdvancedSettings(): List<AdvancedSetting> {
    return listOf(
        AdvancedSetting(
            title = "Conectar ao Spotify",
            description = "Configurar acesso à conta Spotify",
            icon = Icons.Filled.Security
        ),
        AdvancedSetting(
            title = "Atualização Automática",
            description = "Frequência de atualização dos dados",
            icon = Icons.Filled.Sync
        ),
        AdvancedSetting(
            title = "Tema Personalizado",
            description = "Cores e estilos customizados",
            icon = Icons.Filled.Palette
        ),
        AdvancedSetting(
            title = "Notificações",
            description = "Alertas e notificações do widget",
            icon = Icons.Filled.Notifications
        )
    )
}

@Preview(showBackground = true)
@Composable
fun SpotifyWidgetScreenPreview() {
    WidgetProviderTheme {
        SpotifyWidgetScreen()
    }
}