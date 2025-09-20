package com.marcossilqueira.widgetprovider

import android.appwidget.AppWidgetHost
import android.appwidget.AppWidgetManager
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.os.Build
import android.provider.Settings
import android.widget.Toast

class WidgetManager(private val context: Context) {
    
    companion object {
        private const val APPWIDGET_HOST_ID = 1024
    }
    
    private val appWidgetManager = AppWidgetManager.getInstance(context)
    private val appWidgetHost = AppWidgetHost(context, APPWIDGET_HOST_ID)
    private var widgetPickerLauncher: androidx.activity.result.ActivityResultLauncher<Intent>? = null
    
    fun setWidgetPickerLauncher(launcher: androidx.activity.result.ActivityResultLauncher<Intent>) {
        this.widgetPickerLauncher = launcher
    }
    
    fun createWidgetAutomatically(
        widgetSize: WidgetSize,
        widgetStyle: WidgetStyle,
        transparency: Float
    ): Boolean {
        return try {
            // Salvar configurações globais do widget
            val prefs = context.getSharedPreferences("spotify_widget_global", android.content.Context.MODE_PRIVATE)
            prefs.edit()
                .putString("widget_size", widgetSize.name)
                .putString("widget_style", widgetStyle.name)
                .putFloat("widget_transparency", transparency)
                .apply()
            
            // Tentar abrir a tela de seleção de widgets
            openWidgetPicker(widgetSize)
            
            Toast.makeText(
                context,
                "Configurações salvas! Selecione o widget na próxima tela.",
                Toast.LENGTH_LONG
            ).show()
            
            true
            
        } catch (e: Exception) {
            Toast.makeText(
                context,
                "Erro ao preparar widget: ${e.message}",
                Toast.LENGTH_SHORT
            ).show()
            false
        }
    }
    
    private fun openWidgetPicker(widgetSize: WidgetSize) {
        try {
            // Obter o provider correto baseado no tamanho
            val providerClass = when (widgetSize) {
                WidgetSize.SMALL -> SpotifyWidgetSmallProvider::class.java
                WidgetSize.MEDIUM -> SpotifyWidgetMediumProvider::class.java
                WidgetSize.LARGE -> SpotifyWidgetLargeProvider::class.java
            }
            
            val componentName = ComponentName(context, providerClass)
            
            // Criar intent para abrir o seletor de widgets
            val intent = Intent().apply {
                action = AppWidgetManager.ACTION_APPWIDGET_PICK
                putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, AppWidgetManager.INVALID_APPWIDGET_ID)
                putExtra(AppWidgetManager.EXTRA_CUSTOM_INFO, componentName)
            }
            
            // Usar o launcher se disponível, senão usar startActivity
            if (widgetPickerLauncher != null) {
                widgetPickerLauncher?.launch(intent)
            } else {
                intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK
                context.startActivity(intent)
            }
            
        } catch (e: Exception) {
            // Se falhar, mostrar instruções manuais
            showManualInstructions(widgetSize)
        }
    }
    
    private fun showManualInstructions(widgetSize: WidgetSize) {
        val widgetName = when (widgetSize) {
            WidgetSize.SMALL -> "Widget Spotify Pequeno"
            WidgetSize.MEDIUM -> "Widget Spotify Médio"
            WidgetSize.LARGE -> "Widget Spotify Grande"
        }
        
        Toast.makeText(
            context,
            "Vá para a tela inicial, pressione e segure em um espaço vazio, selecione 'Widgets' e adicione o '$widgetName'.",
            Toast.LENGTH_LONG
        ).show()
    }
    
    fun startListening() {
        appWidgetHost.startListening()
    }
    
    fun stopListening() {
        appWidgetHost.stopListening()
    }
}
