package com.example.blackbeard.data.local

import android.content.Context
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class ThemeDataSource(context: Context) {

    private val dataStore = DataStoreSingleton.getInstance(context)
    private val darkModeKey = booleanPreferencesKey("DARK_MODE_ENABLED")

    fun isDarkModeEnabled(): Flow<Boolean?> =
        dataStore.data.map { preferences ->
            preferences[darkModeKey]
        }

    suspend fun setDarkModeEnabled(enabled: Boolean) {
        dataStore.edit { preferences ->
            preferences[darkModeKey] = enabled
        }
    }
}