package com.marcossilqueira.widgetprovider

import android.appwidget.AppWidgetManager
import android.appwidget.AppWidgetProvider
import android.content.Context
import android.widget.RemoteViews

class SpotifyWidgetMediumProvider : AppWidgetProvider() {

    override fun onUpdate(
        context: Context,
        appWidgetManager: AppWidgetManager,
        appWidgetIds: IntArray
    ) {
        for (appWidgetId in appWidgetIds) {
            updateAppWidgetWithSavedSettings(context, appWidgetManager, appWidgetId)
        }
    }
    
    private fun updateAppWidgetWithSavedSettings(
        context: Context,
        appWidgetManager: AppWidgetManager,
        appWidgetId: Int
    ) {
        // Obter configurações globais salvas
        val globalPrefs = context.getSharedPreferences("spotify_widget_global", Context.MODE_PRIVATE)
        val widgetStyle = globalPrefs.getString("widget_style", "MODERN") ?: "MODERN"
        val transparency = globalPrefs.getFloat("widget_transparency", 1f)

        // Criar RemoteViews com configurações salvas
        val views = SpotifyWidgetProvider.createMediumWidget(context, widgetStyle, transparency)
        
        // Configurar intents para os botões
        SpotifyWidgetProvider.setupButtonIntents(context, views, appWidgetId)

        // Atualizar o widget
        appWidgetManager.updateAppWidget(appWidgetId, views)
    }

    override fun onEnabled(context: Context) {
        // Quando o primeiro widget médio é criado
    }

    override fun onDisabled(context: Context) {
        // Quando o último widget médio é removido
    }
}
