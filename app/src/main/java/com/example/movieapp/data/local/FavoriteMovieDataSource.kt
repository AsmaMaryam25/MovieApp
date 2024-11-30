package com.example.movieapp.data.local

import android.content.Context
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import com.example.movieapp.data.model.MovieItem
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json

class FavoriteMovieDataSource(private val context: Context) {

    private val Context.dataStore by preferencesDataStore("favourites")
    private val favoritesKey = stringPreferencesKey("FAVORITE_MOVIES")

    fun getFavorites(): Flow<List<MovieItem>> =
        context.dataStore.data.map {
            val jsonString = it[favoritesKey].orEmpty()
            try {
                Json.decodeFromString(jsonString)
            } catch (error: Throwable) {
                emptyList()
            }
        }


    suspend fun toggleFavorite(id: String?, title: String, posterPath: String?, rating: Double) {
        val currentJsonString = context.dataStore.data.first()[favoritesKey].orEmpty()
        val currentFavorites: List<MovieItem> = try {
            Json.decodeFromString(currentJsonString)
        } catch (error: Throwable) {
            emptyList()
        }

        val isFavorite = currentFavorites.any() { it.id == id }
        val updatedFavorites = if (isFavorite) {
            currentFavorites.filterNot { it.id == id }
        } else {
            currentFavorites + MovieItem(id ?: "", title, posterPath ?: "", rating)
        }

        val updatedJsonString = Json.encodeToString(updatedFavorites)
        context.dataStore.edit {
            it[favoritesKey] = updatedJsonString
        }
    }
}