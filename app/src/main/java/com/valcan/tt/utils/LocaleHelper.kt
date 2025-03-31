package com.valcan.tt.utils

import android.content.Context
import android.content.SharedPreferences
import android.content.res.Configuration
import android.os.Build
import androidx.preference.PreferenceManager
import java.util.Locale

object LocaleHelper {
    private const val SELECTED_LANGUAGE = "Locale.Helper.Selected.Language"

    // Lingue supportate
    val supportedLanguages = listOf(
        "it" to "Italiano",
        "en" to "English",
        "fr" to "Français",
        "de" to "Deutsch",
        "es" to "Español"
    )

    // Ottieni la lingua corrente del dispositivo
    fun getDeviceLanguage(context: Context): String {
        val deviceLocale = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            context.resources.configuration.locales.get(0)
        } else {
            @Suppress("DEPRECATION")
            context.resources.configuration.locale
        }
        val languageCode = deviceLocale.language

        // Verifica se la lingua è supportata, altrimenti usa l'inglese come default
        return if (supportedLanguages.any { it.first == languageCode }) {
            languageCode
        } else {
            "en"
        }
    }

    // Ottieni la lingua correntemente selezionata dall'utente, o usa quella del dispositivo come default
    fun getSelectedLanguage(context: Context): String {
        val preferences = PreferenceManager.getDefaultSharedPreferences(context)
        val deviceLanguage = getDeviceLanguage(context)
        return preferences.getString(SELECTED_LANGUAGE, deviceLanguage) ?: deviceLanguage
    }

    // Cambia la lingua dell'app
    fun setLocale(context: Context, languageCode: String): Context {
        saveSelectedLanguage(context, languageCode)
        
        val locale = Locale(languageCode)
        Locale.setDefault(locale)

        val resources = context.resources
        val configuration = Configuration(resources.configuration)

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            configuration.setLocale(locale)
            return context.createConfigurationContext(configuration)
        } else {
            @Suppress("DEPRECATION")
            configuration.locale = locale
            @Suppress("DEPRECATION")
            resources.updateConfiguration(configuration, resources.displayMetrics)
            return context
        }
    }

    // Applica la lingua salvata
    fun applyLanguage(context: Context): Context {
        val selectedLanguage = getSelectedLanguage(context)
        return setLocale(context, selectedLanguage)
    }

    // Salva la lingua selezionata nelle preferenze
    private fun saveSelectedLanguage(context: Context, languageCode: String) {
        val preferences = PreferenceManager.getDefaultSharedPreferences(context)
        preferences.edit().putString(SELECTED_LANGUAGE, languageCode).apply()
    }

    // Ottieni il nome della lingua dal codice
    fun getLanguageName(languageCode: String): String {
        return supportedLanguages.firstOrNull { it.first == languageCode }?.second ?: "English"
    }
} 