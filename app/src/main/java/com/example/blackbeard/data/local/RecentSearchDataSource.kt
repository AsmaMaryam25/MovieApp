import android.content.Context
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringSetPreferencesKey
import com.example.blackbeard.data.local.DataStoreSingleton
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map

class RecentSearchDataSource(private val context: Context) {

    private val dataStore = DataStoreSingleton.getInstance(context)
    private val recentSearchesKey = stringSetPreferencesKey("RECENT_SEARCHES")

    fun getRecentSearches(): Flow<List<String>> =
        dataStore.data.map {
            it[recentSearchesKey]?.toList() ?: emptyList()
        }

    suspend fun addRecentSearch(query: String) {
        dataStore.edit { preferences ->
            val currentSearches = preferences[recentSearchesKey]?.toMutableSet() ?: mutableSetOf()
            currentSearches.add(query)
            preferences[recentSearchesKey] = currentSearches
        }
    }

    suspend fun removeRecentSearch(query: String) {
        dataStore.edit { preferences ->
            val currentSearches = preferences[recentSearchesKey]?.toMutableSet() ?: mutableSetOf()
            currentSearches.remove(query)
            preferences[recentSearchesKey] = currentSearches
        }
    }

    suspend fun clearRecentSearches() {
        dataStore.edit {
            it[recentSearchesKey] = emptySet()
        }
    }
}