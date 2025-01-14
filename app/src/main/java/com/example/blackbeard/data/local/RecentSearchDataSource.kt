import android.content.Context
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import com.example.blackbeard.data.local.DataStoreSingleton
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json

class RecentSearchDataSource(private val context: Context) {

    private val dataStore = DataStoreSingleton.getInstance(context)
    private val recentSearchesKey = stringPreferencesKey("RECENT_SEARCHES")

    fun getRecentSearches(): Flow<List<String>> =
        dataStore.data.map {
            val jsonString = it[recentSearchesKey].orEmpty()
            try {
                Json.decodeFromString(jsonString)
            } catch (error: Throwable) {
                emptyList()
            }
        }

    suspend fun addRecentSearch(query: String) {
        val currentJsonString = dataStore.data.first()[recentSearchesKey].orEmpty()
        val currentSearches: List<String> = try {
            Json.decodeFromString(currentJsonString)
        } catch (error: Throwable) {
            emptyList()
        }

        val updatedSearches = currentSearches + query
        val updatedJsonString = Json.encodeToString(updatedSearches)
        dataStore.edit {
            it[recentSearchesKey] = updatedJsonString
        }
    }

    suspend fun removeRecentSearch(query: String) {
        val currentJsonString = dataStore.data.first()[recentSearchesKey].orEmpty()
        val currentSearches: List<String> = try {
            Json.decodeFromString(currentJsonString)
        } catch (error: Throwable) {
            emptyList()
        }

        val updatedSearches = currentSearches.filterNot { it == query }
        val updatedJsonString = Json.encodeToString(updatedSearches)
        dataStore.edit {
            it[recentSearchesKey] = updatedJsonString
        }
    }

    suspend fun clearRecentSearches() {
        dataStore.edit {
            it[recentSearchesKey] = ""
        }
    }
}