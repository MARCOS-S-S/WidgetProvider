package com.marcossilqueira.widgetprovider

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.platform.LocalContext
import androidx.activity.ComponentActivity
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.marcossilqueira.widgetprovider.ui.theme.WidgetProviderTheme

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SpotifyAuthScreen(
    authService: SpotifyAuthService,
    onBackClick: () -> Unit = {},
    onAuthSuccess: () -> Unit = {},
    modifier: Modifier = Modifier
) {
    val authState by authService.authState.collectAsStateWithLifecycle()
    val context = LocalContext.current
    
    // Inicializar o auth launcher quando a tela for criada
    LaunchedEffect(Unit) {
        // Registrar o serviço para capturar callbacks
        (context as MainActivity).setSpotifyAuthService(authService)
        authService.checkAuthStatus()
    }
    
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
                            tint = Color(0xFF1DB954) // Verde do Spotify
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "Conectar ao Spotify",
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
        Column(
            modifier = modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(24.dp)
        ) {
            Spacer(modifier = Modifier.height(32.dp))
            
            // Logo/Ícone do Spotify
            Card(
                modifier = Modifier.size(120.dp),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(
                    containerColor = Color(0xFF1DB954)
                ),
                elevation = CardDefaults.cardElevation(defaultElevation = 8.dp)
            ) {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Filled.AudioFile,
                        contentDescription = "Spotify",
                        modifier = Modifier.size(64.dp),
                        tint = Color.White
                    )
                }
            }
            
            // Título
            Text(
                text = "Conectar ao Spotify",
                style = MaterialTheme.typography.headlineMedium,
                fontWeight = FontWeight.Bold,
                textAlign = TextAlign.Center,
                color = MaterialTheme.colorScheme.onBackground
            )
            
            // Descrição
            Text(
                text = "Conecte sua conta do Spotify para criar widgets personalizados e controlar sua música diretamente da tela inicial.",
                style = MaterialTheme.typography.bodyLarge,
                textAlign = TextAlign.Center,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            
            // Benefícios
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.surfaceVariant
                ),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
            ) {
                Column(
                    modifier = Modifier.padding(20.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Text(
                        text = "O que você pode fazer:",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.SemiBold,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    
                    BenefitItem(
                        icon = Icons.Filled.PlayArrow,
                        text = "Controlar reprodução (play, pause, próximo, anterior)"
                    )
                    
                    BenefitItem(
                        icon = Icons.Filled.MusicNote,
                        text = "Ver informações da música atual"
                    )
                    
                    BenefitItem(
                        icon = Icons.Filled.PlaylistPlay,
                        text = "Acessar suas playlists"
                    )
                    
                    BenefitItem(
                        icon = Icons.Filled.VolumeUp,
                        text = "Controlar volume"
                    )
                }
            }
            
            Spacer(modifier = Modifier.weight(1f))
            
            // Estado da autenticação
            when (authState) {
                is SpotifyAuthState.NotAuthenticated -> {
                    Button(
                        onClick = { 
                            authService.authenticate(context as ComponentActivity)
                        },
                        modifier = Modifier.fillMaxWidth(),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = Color(0xFF1DB954)
                        ),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Filled.Login,
                            contentDescription = null,
                            modifier = Modifier.size(20.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "Conectar ao Spotify",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.SemiBold
                        )
                    }
                }
                
                is SpotifyAuthState.Authenticating -> {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.spacedBy(16.dp)
                    ) {
                        CircularProgressIndicator(
                            modifier = Modifier.size(48.dp),
                            color = Color(0xFF1DB954)
                        )
                        Text(
                            text = "Conectando ao Spotify...",
                            style = MaterialTheme.typography.titleMedium,
                            color = MaterialTheme.colorScheme.onBackground
                        )
                    }
                }
                
                is SpotifyAuthState.Authenticated -> {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.spacedBy(16.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Filled.CheckCircle,
                            contentDescription = "Conectado",
                            modifier = Modifier.size(48.dp),
                            tint = Color(0xFF1DB954)
                        )
                        Text(
                            text = "Conectado com sucesso!",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.SemiBold,
                            color = Color(0xFF1DB954)
                        )
                        Text(
                            text = "Sua conta do Spotify está conectada e pronta para uso.",
                            style = MaterialTheme.typography.bodyMedium,
                            textAlign = TextAlign.Center,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                        
                        Spacer(modifier = Modifier.height(16.dp))
                        
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(12.dp)
                        ) {
                            OutlinedButton(
                                onClick = { authService.logout() },
                                modifier = Modifier.weight(1f),
                                colors = ButtonDefaults.outlinedButtonColors(
                                    contentColor = MaterialTheme.colorScheme.error
                                )
                            ) {
                                Text("Desconectar")
                            }
                            
                            Button(
                                onClick = onAuthSuccess,
                                modifier = Modifier.weight(1f),
                                colors = ButtonDefaults.buttonColors(
                                    containerColor = Color(0xFF1DB954)
                                )
                            ) {
                                Text("Continuar")
                            }
                        }
                    }
                }
                
                is SpotifyAuthState.Error -> {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.spacedBy(16.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Filled.Error,
                            contentDescription = "Erro",
                            modifier = Modifier.size(48.dp),
                            tint = MaterialTheme.colorScheme.error
                        )
                        Text(
                            text = "Erro na conexão",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.SemiBold,
                            color = MaterialTheme.colorScheme.error
                        )
                        Text(
                            text = (authState as SpotifyAuthState.Error).message,
                            style = MaterialTheme.typography.bodyMedium,
                            textAlign = TextAlign.Center,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                        
                        Button(
                            onClick = { 
                                authService.authenticate(context as ComponentActivity)
                            },
                            modifier = Modifier.fillMaxWidth(),
                            colors = ButtonDefaults.buttonColors(
                                containerColor = Color(0xFF1DB954)
                            )
                        ) {
                            Text("Tentar novamente")
                        }
                    }
                }
            }
            
            Spacer(modifier = Modifier.height(16.dp))
            
            // Informações de privacidade
            Text(
                text = "Ao conectar, você concorda com os termos de uso do Spotify. Suas credenciais são armazenadas de forma segura.",
                style = MaterialTheme.typography.bodySmall,
                textAlign = TextAlign.Center,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}

@Composable
fun BenefitItem(
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    text: String
) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Icon(
            imageVector = icon,
            contentDescription = null,
            modifier = Modifier.size(20.dp),
            tint = Color(0xFF1DB954)
        )
        Text(
            text = text,
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
    }
}

@Preview(showBackground = true)
@Composable
fun SpotifyAuthScreenPreview() {
    WidgetProviderTheme {
        // Mock do auth service para preview
        val mockAuthService = object {
            val authState = kotlinx.coroutines.flow.MutableStateFlow(SpotifyAuthState.NotAuthenticated)
        }
        // SpotifyAuthScreen(authService = mockAuthService)
        Text("Preview não disponível - requer SpotifyAuthService")
    }
}
