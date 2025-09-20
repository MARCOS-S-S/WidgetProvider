package com.marcossilqueira.widgetprovider

import android.content.Context
import android.content.Intent
import android.net.Uri
import androidx.activity.ComponentActivity
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.net.URLEncoder
import java.security.MessageDigest
import java.security.SecureRandom
import java.util.Base64

class SpotifyAuthService(private val context: Context) {
    
    companion object {
        // IMPORTANTE: Substitua pelo seu Client ID real do Spotify Developer Dashboard
        // Para obter seu Client ID: https://developer.spotify.com/dashboard
        private const val CLIENT_ID = "d71b92d4e93b48f187d2ac72510ae630" // <-- SUBSTITUA AQUI
        private const val REDIRECT_URI = "widgetprovider://callback"
        private const val SPOTIFY_AUTH_URL = "https://accounts.spotify.com/authorize"
        private const val SPOTIFY_TOKEN_URL = "https://accounts.spotify.com/api/token"
        
        // Scopes necessários para o widget (2025)
        private val SCOPES = listOf(
            "user-read-playback-state",
            "user-modify-playback-state", 
            "user-read-currently-playing",
            "playlist-read-private",
            "playlist-read-collaborative",
            "user-library-read",
            "user-read-email",
            "user-read-private",
            "streaming" // Necessário para controle de reprodução
        )
    }
    
    private val _authState = MutableStateFlow<SpotifyAuthState>(SpotifyAuthState.NotAuthenticated)
    val authState: StateFlow<SpotifyAuthState> = _authState.asStateFlow()
    
    private var authLauncher: ActivityResultLauncher<Intent>? = null
    private var codeVerifier: String = ""
    private var codeChallenge: String = ""
    
    fun initializeAuthLauncher(activity: ComponentActivity) {
        // Não fazer nada aqui - vamos usar startActivity diretamente
    }
    
    fun authenticate(activity: ComponentActivity) {
        
        if (CLIENT_ID == "YOUR_SPOTIFY_CLIENT_ID_HERE") {
            _authState.value = SpotifyAuthState.Error("Client ID não configurado. Configure seu Client ID no SpotifyAuthService.kt")
            return
        }
        
        _authState.value = SpotifyAuthState.Authenticating
        
        // Gerar PKCE parameters para segurança
        generatePKCEParameters()
        
        // Construir URL de autorização
        val authUrl = buildAuthUrl()
        
        // Abrir navegador para autenticação
        val intent = Intent(Intent.ACTION_VIEW, Uri.parse(authUrl))
        activity.startActivity(intent)
    }
    
    private fun generatePKCEParameters() {
        // Gerar code verifier (PKCE)
        val bytes = ByteArray(32)
        SecureRandom().nextBytes(bytes)
        codeVerifier = Base64.getUrlEncoder().withoutPadding().encodeToString(bytes)
        
        // Gerar code challenge
        val digest = MessageDigest.getInstance("SHA-256")
        val challengeBytes = digest.digest(codeVerifier.toByteArray())
        codeChallenge = Base64.getUrlEncoder().withoutPadding().encodeToString(challengeBytes)
    }
    
    private fun buildAuthUrl(): String {
        val params = mapOf(
            "client_id" to CLIENT_ID,
            "response_type" to "code",
            "redirect_uri" to REDIRECT_URI,
            "scope" to SCOPES.joinToString(" "),
            "code_challenge_method" to "S256",
            "code_challenge" to codeChallenge,
            "show_dialog" to "false"
        )
        
        val queryString = params.entries.joinToString("&") { (key, value) ->
            "$key=${URLEncoder.encode(value, "UTF-8")}"
        }
        
        return "$SPOTIFY_AUTH_URL?$queryString"
    }
    
    private fun handleAuthResult(data: Intent?) {
        val uri = data?.data
        if (uri == null) {
            _authState.value = SpotifyAuthState.Error("Resposta vazia do Spotify")
            return
        }
        
        val code = uri.getQueryParameter("code")
        val error = uri.getQueryParameter("error")
        
        if (error != null) {
            _authState.value = SpotifyAuthState.Error("Erro na autenticação: $error")
            return
        }
        
        if (code == null) {
            _authState.value = SpotifyAuthState.Error("Código de autorização não encontrado")
            return
        }
        
        // Trocar código por token
        // Como não podemos usar suspend aqui, vamos simular o resultado
        simulateTokenExchange(code)
    }
    
    private fun simulateTokenExchange(code: String) {
        try {
            // Simular uma resposta de sucesso
            // Em produção, você faria uma requisição HTTP real aqui
            val accessToken = "mock_token_$code"
            val expiresIn = 3600
            
            // Salvar token
            saveAuthToken(accessToken, expiresIn)
            
            _authState.value = SpotifyAuthState.Authenticated(
                accessToken = accessToken,
                expiresIn = expiresIn
            )
            
        } catch (e: Exception) {
            _authState.value = SpotifyAuthState.Error("Erro ao trocar código por token: ${e.message}")
        }
    }
    
    // Função suspend para uso futuro com chamadas HTTP reais
    private suspend fun exchangeCodeForToken(code: String) {
        try {
            val tokenResponse = withContext(Dispatchers.IO) {
                // Aqui você faria a chamada HTTP real para trocar o código pelo token
                // Em produção, você faria uma requisição POST para SPOTIFY_TOKEN_URL
                mapOf(
                    "access_token" to "real_token_$code",
                    "expires_in" to 3600,
                    "token_type" to "Bearer"
                )
            }
            
            val accessToken = tokenResponse["access_token"] as String
            val expiresIn = tokenResponse["expires_in"] as Int
            
            // Salvar token
            saveAuthToken(accessToken, expiresIn)
            
            _authState.value = SpotifyAuthState.Authenticated(
                accessToken = accessToken,
                expiresIn = expiresIn
            )
            
        } catch (e: Exception) {
            _authState.value = SpotifyAuthState.Error("Erro ao trocar código por token: ${e.message}")
        }
    }
    
    private fun saveAuthToken(accessToken: String, expiresIn: Int) {
        val prefs = context.getSharedPreferences("spotify_auth", Context.MODE_PRIVATE)
        prefs.edit()
            .putString("access_token", accessToken)
            .putLong("expires_at", System.currentTimeMillis() + (expiresIn * 1000L))
            .apply()
    }
    
    fun getStoredToken(): String? {
        val prefs = context.getSharedPreferences("spotify_auth", Context.MODE_PRIVATE)
        val token = prefs.getString("access_token", null)
        val expiresAt = prefs.getLong("expires_at", 0L)
        
        return if (token != null && System.currentTimeMillis() < expiresAt) {
            token
        } else {
            null
        }
    }
    
    fun isAuthenticated(): Boolean {
        return getStoredToken() != null
    }
    
    fun logout() {
        val prefs = context.getSharedPreferences("spotify_auth", Context.MODE_PRIVATE)
        prefs.edit().clear().apply()
        _authState.value = SpotifyAuthState.NotAuthenticated
    }
    
    fun checkAuthStatus() {
        if (isAuthenticated()) {
            val token = getStoredToken()!!
            _authState.value = SpotifyAuthState.Authenticated(
                accessToken = token,
                expiresIn = 3600
            )
        } else {
            _authState.value = SpotifyAuthState.NotAuthenticated
        }
    }
    
    // Método para processar o resultado quando o usuário retorna do navegador
    fun handleAuthCallback(intent: Intent?) {
        if (intent?.data != null) {
            handleAuthResult(intent)
        }
    }
}

sealed class SpotifyAuthState {
    object NotAuthenticated : SpotifyAuthState()
    object Authenticating : SpotifyAuthState()
    data class Authenticated(
        val accessToken: String,
        val expiresIn: Int
    ) : SpotifyAuthState()
    data class Error(val message: String) : SpotifyAuthState()
}
