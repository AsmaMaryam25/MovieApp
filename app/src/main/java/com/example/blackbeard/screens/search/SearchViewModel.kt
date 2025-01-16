package com.example.blackbeard.screens.search

import android.util.Log
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateMapOf
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.blackbeard.di.DataModule
import com.example.blackbeard.di.DataModule.recentSearchRepository
import com.example.blackbeard.domain.RecentSearchRepository
import com.example.blackbeard.models.CollectionMovie
import com.example.blackbeard.models.Movie
import com.example.blackbeard.utils.ConnectivityObserver.isConnected
import kotlinx.coroutines.TimeoutCancellationException
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import kotlinx.coroutines.withTimeout
import java.net.UnknownHostException

class SearchViewModel() : ViewModel() {

    private val movieRepository = DataModule.movieRepository
    private val mutableSearchUIState = MutableStateFlow<SearchUIModel>(SearchUIModel.Empty)
    val searchUIState: StateFlow<SearchUIModel> = mutableSearchUIState

    var popularMovies: List<CollectionMovie> = emptyList()
    val initialConnectivityFlow: Flow<Boolean> = isConnected
    var currentPage = mutableIntStateOf(1)
        private set

    var totalPages = mutableStateOf<Int?>(null)
    var searchType = mutableStateOf(false)
    val selectedCategories = mutableStateMapOf<String, MutableMap<String, String>>()

    private val _recentSearches = MutableStateFlow<List<String>>(emptyList())
    val recentSearches: StateFlow<List<String>> = _recentSearches

    private val recentSearchRepository: RecentSearchRepository = DataModule.recentSearchRepository

    init {
        viewModelScope.launch {
            try {
                searchType.value = false
                mutableSearchUIState.value = SearchUIModel.Loading

                val isInitiallyConnected = withTimeout(5000L) {
                    initialConnectivityFlow.first()
                }

                if (isInitiallyConnected) {
                    loadPopularMovies()
                } else {
                    mutableSearchUIState.value = SearchUIModel.NoConnection
                }

                recentSearchRepository.getRecentSearches().collect {
                    _recentSearches.value = it
                }

                initialConnectivityFlow.stateIn(
                    viewModelScope,
                    SharingStarted.WhileSubscribed(5000L),
                    isInitiallyConnected
                ).collect { isConnected ->
                    if (isConnected) {
                        loadPopularMovies()
                    } else {
                        mutableSearchUIState.value = SearchUIModel.NoConnection
                    }
                }
            } catch (e: TimeoutCancellationException) {
                mutableSearchUIState.value = SearchUIModel.NoConnection

                initialConnectivityFlow.stateIn(
                    viewModelScope,
                    SharingStarted.WhileSubscribed(5000L),
                    false
                ).collect { isConnected ->
                    if (isConnected) {
                        loadPopularMovies()
                    } else {
                        mutableSearchUIState.value = SearchUIModel.NoConnection
                    }
                }
            } catch (e: UnknownHostException) {
                mutableSearchUIState.value = SearchUIModel.NoConnection
            }
        }
    }

    private suspend fun loadPopularMovies() {
        mutableSearchUIState.value = SearchUIModel.Loading
        movieRepository.getPopularMovies().collect { popular ->
            popularMovies = popular
            mutableSearchUIState.value = SearchUIModel.Data(popular, 1)
        }
    }

    fun searchMovies(query: String, pageNum: Int) {
        viewModelScope.launch {
            if (query.isBlank()) {
                mutableSearchUIState.value = SearchUIModel.Data(popularMovies, 1)
                currentPage.intValue = 1
                totalPages.value = 0
                return@launch
            }

            addRecentSearch(query)

            val currentMovies =
                (mutableSearchUIState.value as? SearchUIModel.Data)?.collectionMovies ?: emptyList()

            mutableSearchUIState.value = SearchUIModel.Loading

            movieRepository.searchMovies(query, pageNum).collect { searchResults ->
                val updatedMovies =
                    if (pageNum == 1) {
                        searchResults.movies
                    } else {
                        currentMovies + searchResults.movies
                    }

                mutableSearchUIState.value = if (updatedMovies.isEmpty()) {
                    SearchUIModel.Empty
                } else {
                    SearchUIModel.Data(updatedMovies, searchResults.totalPages)
                }
                totalPages.value = searchResults.totalPages
                currentPage.intValue = pageNum
            }
        }
    }

    fun discoverMovies(query: String, pageNum: Int) {
        viewModelScope.launch{

            Log.d("SearchViewModel", "SelectedCategories: $selectedCategories")
            /*if (query.isBlank()) {
                mutableSearchUIState.value = SearchUIModel.Data(popularMovies, 1)
                currentPage.intValue = 1
                totalPages.value = 0
                return@launch
            }*/

            var releaseDateGte: String? = null
            var releaseDateLte: String? = null
            var withGenres: String? = null

            if (selectedCategories["Decade"] != null) {
                val decade = selectedCategories["Decade"]?.values?.first()
                releaseDateGte = "$decade-01-01"
                releaseDateLte = (decade?.toInt()?.plus(9)).toString() + "-01-01"
            } else if (selectedCategories["Popular Genres"] != null) {
                withGenres = selectedCategories["Popular Genres"]?.values?.joinToString(",")
            }

            Log.d("SearchViewModel", "Discovering movies with releaseDateGte: $releaseDateGte, releaseDateLte: $releaseDateLte, withGenres: $withGenres")

            mutableSearchUIState.value = SearchUIModel.Loading
            movieRepository.discoverMovies(pageNum, releaseDateGte, releaseDateLte, null, null, withGenres, null, null).collect { searchResults ->
                Log.d("SearchResult", searchResults.movies.toString())
                mutableSearchUIState.value = SearchUIModel.Data(searchResults.movies, searchResults.totalPages)
                totalPages.value = searchResults.totalPages
                currentPage.intValue = pageNum
            }
        }
    }


    fun onCategorySelected(categoryTitle: String, item: String, isSelected: Boolean) {
        val currentCategoryItems = selectedCategories[categoryTitle]?.toMutableMap() ?: mutableMapOf()

        if (isSelected) {
            currentCategoryItems[item] = item
        } else {
            currentCategoryItems.remove(item)
        }

        selectedCategories[categoryTitle] = currentCategoryItems
    }

    fun addRecentSearch(query: String) {
        viewModelScope.launch {
            recentSearchRepository.addRecentSearch(query)
        }
    }

    fun removeRecentSearch(query: String) {
        viewModelScope.launch {
            recentSearchRepository.removeRecentSearch(query)
        }
    }

    fun clearRecentSearches() {
        viewModelScope.launch {
            recentSearchRepository.clearRecentSearches()
        }
    }

    sealed class SearchUIModel {
        data object Empty : SearchUIModel()
        data object Loading : SearchUIModel()
        data object NoConnection : SearchUIModel()
        data class Data(
            val collectionMovies: List<Movie>,
            val totalPages: Int?
        ) : SearchUIModel()
    }
}