package com.example.luxcar.data

import android.content.Context
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map

val Context.dataStore by preferencesDataStore("settings")

object LanguageStore {
    private val LANGUAGE_KEY = stringPreferencesKey("app_language")

    suspend fun saveLanguage(context: Context, lang: String) {
        context.dataStore.edit { settings ->
            settings[LANGUAGE_KEY] = lang
        }
    }

    suspend fun loadLanguage(context: Context): String {
        return context.dataStore.data
            .map { it[LANGUAGE_KEY] ?: "pt" }
            .first()
    }
}
