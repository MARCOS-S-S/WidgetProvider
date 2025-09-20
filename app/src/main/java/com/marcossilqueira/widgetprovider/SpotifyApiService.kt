package com.marcossilqueira.widgetprovider

import retrofit2.Response
import retrofit2.http.*
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor

// Data classes para as respostas da API do Spotify
data class SpotifyUser(
    val id: String,
    val display_name: String,
    val email: String,
    val images: List<SpotifyImage>?
)

data class SpotifyImage(
    val url: String,
    val height: Int?,
    val width: Int?
)

data class SpotifyTrack(
    val id: String,
    val name: String,
    val artists: List<SpotifyArtist>,
    val album: SpotifyAlbum,
    val duration_ms: Long,
    val external_urls: Map<String, String>
)

data class SpotifyArtist(
    val id: String,
    val name: String
)

data class SpotifyAlbum(
    val id: String,
    val name: String,
    val images: List<SpotifyImage>
)

data class SpotifyPlaybackState(
    val is_playing: Boolean,
    val item: SpotifyTrack?,
    val progress_ms: Long?,
    val device: SpotifyDevice?
)

data class SpotifyDevice(
    val id: String,
    val name: String,
    val type: String,
    val volume_percent: Int?
)

data class SpotifyPlaylist(
    val id: String,
    val name: String,
    val description: String?,
    val images: List<SpotifyImage>?,
    val tracks: SpotifyPlaylistTracks
)

data class SpotifyPlaylistTracks(
    val total: Int
)

data class SpotifyPlaylistsResponse(
    val items: List<SpotifyPlaylist>
)

// Interface para as chamadas da API
interface SpotifyApiInterface {
    
    @GET("me")
    suspend fun getCurrentUser(): Response<SpotifyUser>
    
    @GET("me/player")
    suspend fun getCurrentPlaybackState(): Response<SpotifyPlaybackState>
    
    @PUT("me/player/play")
    suspend fun play(): Response<Unit>
    
    @PUT("me/player/pause")
    suspend fun pause(): Response<Unit>
    
    @POST("me/player/next")
    suspend fun nextTrack(): Response<Unit>
    
    @POST("me/player/previous")
    suspend fun previousTrack(): Response<Unit>
    
    @PUT("me/player/volume")
    suspend fun setVolume(@Query("volume_percent") volume: Int): Response<Unit>
    
    @GET("me/playlists")
    suspend fun getUserPlaylists(@Query("limit") limit: Int = 20): Response<SpotifyPlaylistsResponse>
}

class SpotifyApiService(private val accessToken: String) {
    
    companion object {
        private const val BASE_URL = "https://api.spotify.com/v1/"
    }
    
    private val api: SpotifyApiInterface by lazy {
        val loggingInterceptor = HttpLoggingInterceptor().apply {
            level = HttpLoggingInterceptor.Level.BODY
        }
        
        val client = OkHttpClient.Builder()
            .addInterceptor(loggingInterceptor)
            .addInterceptor { chain ->
                val request = chain.request().newBuilder()
                    .addHeader("Authorization", "Bearer $accessToken")
                    .build()
                chain.proceed(request)
            }
            .build()
        
        val retrofit = Retrofit.Builder()
            .baseUrl(BASE_URL)
            .client(client)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
        
        retrofit.create(SpotifyApiInterface::class.java)
    }
    
    suspend fun getCurrentUser(): Result<SpotifyUser> {
        return try {
            val response = api.getCurrentUser()
            if (response.isSuccessful) {
                Result.success(response.body()!!)
            } else {
                Result.failure(Exception("Erro ao obter usuário: ${response.code()} ${response.message()}"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    suspend fun getCurrentPlaybackState(): Result<SpotifyPlaybackState> {
        return try {
            val response = api.getCurrentPlaybackState()
            if (response.isSuccessful) {
                Result.success(response.body()!!)
            } else {
                Result.failure(Exception("Erro ao obter estado de reprodução: ${response.code()} ${response.message()}"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    suspend fun play(): Result<Unit> {
        return try {
            val response = api.play()
            if (response.isSuccessful) {
                Result.success(Unit)
            } else {
                Result.failure(Exception("Erro ao reproduzir: ${response.code()} ${response.message()}"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    suspend fun pause(): Result<Unit> {
        return try {
            val response = api.pause()
            if (response.isSuccessful) {
                Result.success(Unit)
            } else {
                Result.failure(Exception("Erro ao pausar: ${response.code()} ${response.message()}"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    suspend fun nextTrack(): Result<Unit> {
        return try {
            val response = api.nextTrack()
            if (response.isSuccessful) {
                Result.success(Unit)
            } else {
                Result.failure(Exception("Erro ao avançar música: ${response.code()} ${response.message()}"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    suspend fun previousTrack(): Result<Unit> {
        return try {
            val response = api.previousTrack()
            if (response.isSuccessful) {
                Result.success(Unit)
            } else {
                Result.failure(Exception("Erro ao voltar música: ${response.code()} ${response.message()}"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    suspend fun setVolume(volume: Int): Result<Unit> {
        return try {
            val response = api.setVolume(volume)
            if (response.isSuccessful) {
                Result.success(Unit)
            } else {
                Result.failure(Exception("Erro ao alterar volume: ${response.code()} ${response.message()}"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    suspend fun getUserPlaylists(limit: Int = 20): Result<List<SpotifyPlaylist>> {
        return try {
            val response = api.getUserPlaylists(limit)
            if (response.isSuccessful) {
                Result.success(response.body()?.items ?: emptyList())
            } else {
                Result.failure(Exception("Erro ao obter playlists: ${response.code()} ${response.message()}"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}
