package com.marcossilqueira.widgetprovider

import android.app.PendingIntent
import android.appwidget.AppWidgetManager
import android.appwidget.AppWidgetProvider
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.widget.RemoteViews
import androidx.core.content.ContextCompat

class SpotifyWidgetProvider : AppWidgetProvider() {

    override fun onUpdate(
        context: Context,
        appWidgetManager: AppWidgetManager,
        appWidgetIds: IntArray
    ) {
        for (appWidgetId in appWidgetIds) {
            updateAppWidget(context, appWidgetManager, appWidgetId)
        }
    }
    
    override fun onAppWidgetOptionsChanged(
        context: Context,
        appWidgetManager: AppWidgetManager,
        appWidgetId: Int,
        newOptions: Bundle
    ) {
        // Aplicar configurações salvas quando o widget for redimensionado
        updateAppWidget(context, appWidgetManager, appWidgetId)
    }

    override fun onEnabled(context: Context) {
        // Quando o primeiro widget é criado
    }

    override fun onDisabled(context: Context) {
        // Quando o último widget é removido
    }

    companion object {
        fun updateAppWidget(
            context: Context,
            appWidgetManager: AppWidgetManager,
            appWidgetId: Int
        ) {
            // Obter configurações salvas do widget (primeiro tentar configurações específicas, depois globais)
            val widgetPrefs = context.getSharedPreferences("spotify_widget_$appWidgetId", Context.MODE_PRIVATE)
            val globalPrefs = context.getSharedPreferences("spotify_widget_global", Context.MODE_PRIVATE)
            
            val widgetSize = widgetPrefs.getString("widget_size", null) 
                ?: globalPrefs.getString("widget_size", "SMALL") ?: "SMALL"
            val widgetStyle = widgetPrefs.getString("widget_style", null)
                ?: globalPrefs.getString("widget_style", "MODERN") ?: "MODERN"
            val transparency = if (widgetPrefs.contains("widget_transparency")) {
                widgetPrefs.getFloat("widget_transparency", 1f)
            } else {
                globalPrefs.getFloat("widget_transparency", 1f)
            }

            // Criar RemoteViews baseado no tamanho
            val views = when (widgetSize) {
                "SMALL" -> createSmallWidget(context, widgetStyle, transparency)
                "MEDIUM" -> createMediumWidget(context, widgetStyle, transparency)
                "LARGE" -> createLargeWidget(context, widgetStyle, transparency)
                else -> createSmallWidget(context, widgetStyle, transparency)
            }

            // Configurar intents para os botões
            setupButtonIntents(context, views, appWidgetId)

            // Atualizar o widget
            appWidgetManager.updateAppWidget(appWidgetId, views)
        }

        fun createSmallWidget(context: Context, style: String, transparency: Float): RemoteViews {
            val views = RemoteViews(context.packageName, R.layout.widget_spotify_small)
            
            // Aplicar estilo e transparência
            val backgroundColor = if (style == "MODERN") 0xFF1DB954 else 0xFF191414
            val alpha = (transparency * 255).toInt()
            val finalColor = (backgroundColor.toLong() and 0x00FFFFFF) or ((alpha shl 24).toLong())
            
            views.setInt(R.id.widget_container, "setBackgroundColor", finalColor.toInt())
            
            // Definir informações da música (mock por enquanto)
            views.setTextViewText(R.id.song_title, "Blinding Lights")
            views.setTextViewText(R.id.artist_name, "The Weeknd")
            
            return views
        }

        fun createMediumWidget(context: Context, style: String, transparency: Float): RemoteViews {
            val views = RemoteViews(context.packageName, R.layout.widget_spotify_medium)
            
            // Aplicar estilo e transparência
            val backgroundColor = if (style == "MODERN") 0xFF1DB954 else 0xFF191414
            val alpha = (transparency * 255).toInt()
            val finalColor = (backgroundColor.toLong() and 0x00FFFFFF) or ((alpha shl 24).toLong())
            
            views.setInt(R.id.widget_container, "setBackgroundColor", finalColor.toInt())
            
            // Definir informações da música
            views.setTextViewText(R.id.song_title, "Blinding Lights")
            views.setTextViewText(R.id.artist_name, "The Weeknd")
            
            // Configurar barra de progresso
            views.setProgressBar(R.id.progress_bar, 100, 30, false)
            
            return views
        }

        fun createLargeWidget(context: Context, style: String, transparency: Float): RemoteViews {
            val views = RemoteViews(context.packageName, R.layout.widget_spotify_large)
            
            // Aplicar estilo e transparência
            val backgroundColor = if (style == "MODERN") 0xFF1DB954 else 0xFF191414
            val alpha = (transparency * 255).toInt()
            val finalColor = (backgroundColor.toLong() and 0x00FFFFFF) or ((alpha shl 24).toLong())
            
            views.setInt(R.id.widget_container, "setBackgroundColor", finalColor.toInt())
            
            // Definir informações da música
            views.setTextViewText(R.id.song_title, "Blinding Lights")
            views.setTextViewText(R.id.artist_name, "The Weeknd • After Hours")
            
            // Configurar barra de progresso
            views.setProgressBar(R.id.progress_bar, 100, 30, false)
            
            return views
        }

        fun setupButtonIntents(context: Context, views: RemoteViews, appWidgetId: Int) {
            // Intent para play/pause
            val playPauseIntent = Intent(context, SpotifyWidgetProvider::class.java).apply {
                action = "PLAY_PAUSE"
                putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, appWidgetId)
            }
            val playPausePendingIntent = PendingIntent.getBroadcast(
                context, appWidgetId, playPauseIntent,
                PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
            )
            views.setOnClickPendingIntent(R.id.btn_play_pause, playPausePendingIntent)

            // Intent para próxima música
            val nextIntent = Intent(context, SpotifyWidgetProvider::class.java).apply {
                action = "NEXT"
                putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, appWidgetId)
            }
            val nextPendingIntent = PendingIntent.getBroadcast(
                context, appWidgetId + 1, nextIntent,
                PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
            )
            views.setOnClickPendingIntent(R.id.btn_next, nextPendingIntent)

            // Intent para música anterior
            val previousIntent = Intent(context, SpotifyWidgetProvider::class.java).apply {
                action = "PREVIOUS"
                putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, appWidgetId)
            }
            val previousPendingIntent = PendingIntent.getBroadcast(
                context, appWidgetId + 2, previousIntent,
                PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
            )
            views.setOnClickPendingIntent(R.id.btn_previous, previousPendingIntent)
        }

        fun saveWidgetSettings(
            context: Context,
            appWidgetId: Int,
            widgetSize: String,
            widgetStyle: String,
            transparency: Float
        ) {
            val prefs = context.getSharedPreferences("spotify_widget_$appWidgetId", Context.MODE_PRIVATE)
            prefs.edit()
                .putString("widget_size", widgetSize)
                .putString("widget_style", widgetStyle)
                .putFloat("widget_transparency", transparency)
                .apply()
        }
        
        fun getCustomPreview(context: Context, widgetSize: String): RemoteViews? {
            // Obter configurações globais salvas
            val globalPrefs = context.getSharedPreferences("spotify_widget_global", Context.MODE_PRIVATE)
            val widgetStyle = globalPrefs.getString("widget_style", "MODERN") ?: "MODERN"
            val transparency = globalPrefs.getFloat("widget_transparency", 1f)
            
            // Criar preview baseado no tamanho e configurações salvas
            return when (widgetSize) {
                "SMALL" -> createSmallWidget(context, widgetStyle, transparency)
                "MEDIUM" -> createMediumWidget(context, widgetStyle, transparency)
                "LARGE" -> createLargeWidget(context, widgetStyle, transparency)
                else -> createSmallWidget(context, widgetStyle, transparency)
            }
        }
        
        fun forceWidgetPreviewUpdate(context: Context) {
            try {
                val appWidgetManager = AppWidgetManager.getInstance(context)
                
                // Forçar atualização de todos os providers
                val providers = listOf(
                    ComponentName(context, SpotifyWidgetSmallProvider::class.java),
                    ComponentName(context, SpotifyWidgetMediumProvider::class.java),
                    ComponentName(context, SpotifyWidgetLargeProvider::class.java)
                )
                
                for (provider in providers) {
                    val widgetIds = appWidgetManager.getAppWidgetIds(provider)
                    if (widgetIds.isNotEmpty()) {
                        val intent = Intent(AppWidgetManager.ACTION_APPWIDGET_UPDATE).apply {
                            putExtra(AppWidgetManager.EXTRA_APPWIDGET_IDS, widgetIds)
                            component = provider
                        }
                        context.sendBroadcast(intent)
                    }
                }
                
                // Invalidar cache de previews
                invalidateWidgetPreviewCache(context)
                
            } catch (e: Exception) {
                // Falha silenciosa
            }
        }
        
        private fun invalidateWidgetPreviewCache(context: Context) {
            try {
                // Tentar invalidar o cache de previews do sistema
                val intent = Intent("android.appwidget.action.APPWIDGET_HOST_RESTORED")
                context.sendBroadcast(intent)
                
                // Forçar refresh do AppWidgetManager
                val appWidgetManager = AppWidgetManager.getInstance(context)
                val providers = listOf(
                    ComponentName(context, SpotifyWidgetSmallProvider::class.java),
                    ComponentName(context, SpotifyWidgetMediumProvider::class.java),
                    ComponentName(context, SpotifyWidgetLargeProvider::class.java)
                )
                
                for (provider in providers) {
                    try {
                        appWidgetManager.notifyAppWidgetViewDataChanged(
                            appWidgetManager.getAppWidgetIds(provider),
                            android.R.id.list
                        )
                    } catch (e: Exception) {
                        // Ignorar erros específicos de view
                    }
                }
                
            } catch (e: Exception) {
                // Falha silenciosa
            }
        }
    }

    override fun onReceive(context: Context, intent: Intent) {
        super.onReceive(context, intent)
        
        when (intent.action) {
            "PLAY_PAUSE" -> {
                // TODO: Implementar controle de play/pause do Spotify
                val appWidgetId = intent.getIntExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, -1)
                if (appWidgetId != -1) {
                    val appWidgetManager = AppWidgetManager.getInstance(context)
                    updateAppWidget(context, appWidgetManager, appWidgetId)
                }
            }
            "NEXT" -> {
                // TODO: Implementar próxima música
            }
            "PREVIOUS" -> {
                // TODO: Implementar música anterior
            }
        }
    }
}
