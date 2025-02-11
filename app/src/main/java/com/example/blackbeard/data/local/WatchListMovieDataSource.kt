package com.example.blackbeard.data.local

import android.content.Context
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import com.example.blackbeard.data.model.MovieItem
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json

class WatchListMovieDataSource(private val context: Context) {

    private val dataStore = DataStoreSingleton.getInstance(context)
    private val watchlistKey = stringPreferencesKey("WATCHLIST_MOVIES")

    fun getWatchlist(): Flow<List<MovieItem>> =
        dataStore.data.map {
            val jsonString = it[watchlistKey].orEmpty()
            try {
                Json.decodeFromString(jsonString)
            } catch (error: Throwable) {
                emptyList()
            }
        }


    suspend fun toggleWatchlist(id: String?, title: String, posterPath: String?, rating: Double) {
        val currentJsonString = dataStore.data.first()[watchlistKey].orEmpty()
        val currentWatchlist: List<MovieItem> = try {
            Json.decodeFromString(currentJsonString)
        } catch (error: Throwable) {
            emptyList()
        }

        val isWatchlist = currentWatchlist.any { it.id == id }
        val updatedWatchlist = if (isWatchlist) {
            currentWatchlist.filterNot { it.id == id }
        } else {
            currentWatchlist + MovieItem(id ?: "", title, posterPath ?: "", rating)
        }

        val updatedJsonString = Json.encodeToString(updatedWatchlist)
        dataStore.edit {
            it[watchlistKey] = updatedJsonString
        }
    }
}