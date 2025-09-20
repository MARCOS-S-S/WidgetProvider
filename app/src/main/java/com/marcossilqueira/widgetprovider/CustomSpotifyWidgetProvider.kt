package com.marcossilqueira.widgetprovider

import android.appwidget.AppWidgetManager
import android.appwidget.AppWidgetProvider
import android.content.Context
import android.widget.RemoteViews

class CustomSpotifyWidgetProvider : AppWidgetProvider() {

    override fun onUpdate(
        context: Context,
        appWidgetManager: AppWidgetManager,
        appWidgetIds: IntArray
    ) {
        for (appWidgetId in appWidgetIds) {
            updateAppWidget(context, appWidgetManager, appWidgetId)
        }
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
            // Obter configurações globais salvas
            val globalPrefs = context.getSharedPreferences("spotify_widget_global", Context.MODE_PRIVATE)
            val widgetSize = globalPrefs.getString("widget_size", "SMALL") ?: "SMALL"
            val widgetStyle = globalPrefs.getString("widget_style", "MODERN") ?: "MODERN"
            val transparency = globalPrefs.getFloat("widget_transparency", 1f)

            // Criar RemoteViews baseado no tamanho e configurações salvas
            val views = when (widgetSize) {
                "SMALL" -> SpotifyWidgetProvider.createSmallWidget(context, widgetStyle, transparency)
                "MEDIUM" -> SpotifyWidgetProvider.createMediumWidget(context, widgetStyle, transparency)
                "LARGE" -> SpotifyWidgetProvider.createLargeWidget(context, widgetStyle, transparency)
                else -> SpotifyWidgetProvider.createSmallWidget(context, widgetStyle, transparency)
            }

            // Configurar intents para os botões
            SpotifyWidgetProvider.setupButtonIntents(context, views, appWidgetId)

            // Atualizar o widget
            appWidgetManager.updateAppWidget(appWidgetId, views)
        }
    }
}

