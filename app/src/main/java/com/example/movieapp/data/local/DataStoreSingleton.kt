package com.example.movieapp.data.local

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.preferencesDataStore

object DataStoreSingleton {
    private val Context.dataStore: DataStore<Preferences> by preferencesDataStore("user_preferences")

    @Volatile
    private var INSTANCE: DataStore<Preferences>? = null

    fun getInstance(context: Context): DataStore<Preferences> {
        return INSTANCE ?: synchronized(this) {
            val instance = context.dataStore
            INSTANCE = instance
            instance
        }
    }
}