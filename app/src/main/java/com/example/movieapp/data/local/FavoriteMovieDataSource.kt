package com.example.movieapp.data.local

import android.content.Context
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json

class FavoriteMovieDataSource(private val context: Context) {

    private val Context.dataStore by preferencesDataStore("favourites")
    private val favoritesKey = stringPreferencesKey("FAVORITE_MOVIES")

    fun getFavourites(): Flow<List<String>> =
        context.dataStore.data.map {
            val jsonString = it[favoritesKey].orEmpty()
            try {
                Json.decodeFromString(jsonString)
            } catch (error: Throwable) {
                emptyList()
            }
        }


    suspend fun toggleFavorite(url: String) {
        val currentJsonString = context.dataStore.data.first()[favoritesKey].orEmpty()
        val currentFavorites: List<String> = try {
            Json.decodeFromString(currentJsonString)
        } catch (error: Throwable) {
            emptyList()
        }

        val isFavorite = currentFavorites.contains(url)
        val updatedFavorites = if (isFavorite) {
            currentFavorites - url
        } else {
            currentFavorites + url
        }

        val updatedJsonString = Json.encodeToString(updatedFavorites)
        context.dataStore.edit {
            it[favoritesKey] = updatedJsonString
        }
    }
}