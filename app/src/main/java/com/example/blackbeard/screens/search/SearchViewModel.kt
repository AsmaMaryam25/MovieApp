package com.example.blackbeard.screens.search

import android.util.Log
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateMapOf
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.blackbeard.di.DataModule
import com.example.blackbeard.domain.RecentSearchRepository
import com.example.blackbeard.domain.Result
import com.example.blackbeard.models.CollectionMovie
import com.example.blackbeard.models.Movie
import com.example.blackbeard.models.MovieSearchResult
import com.example.blackbeard.screens.details.DetailsViewModel.DetailsUIModel
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
import retrofit2.HttpException
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

    var isViewingSearchResults = mutableStateOf(false)
    var lastActiveTab = mutableStateOf(0)

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
            } catch (e: HttpException) {
                mutableSearchUIState.value = SearchUIModel.ApiError
            } catch (e: Exception) {
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
            }
        }
    }

    private suspend fun loadPopularMovies() {
        mutableSearchUIState.value = SearchUIModel.Loading

        movieRepository.getPopularMovies().collect { result ->
            when (result) {
                is Result.Error -> {
                    mutableSearchUIState.value = SearchUIModel.ApiError
                }

                is Result.Success -> {
                    popularMovies = result.data
                    mutableSearchUIState.value = SearchUIModel.Data(result.data, 1)
                }
            }
        }

    }

    fun searchMovies(query: String, pageNum: Int, isAdvanced: Boolean = false) {
        viewModelScope.launch {
            if (query.isBlank()) {
                mutableSearchUIState.value = SearchUIModel.Data(popularMovies, 1)
                currentPage.intValue = 1
                totalPages.value = 0
                isViewingSearchResults.value = false
                lastActiveTab.value = 0
                return@launch
            }

            addRecentSearch(query)
            isViewingSearchResults.value = true
            lastActiveTab.value = if (isAdvanced) 1 else 0

            val currentMovies =
                (mutableSearchUIState.value as? SearchUIModel.Data)?.collectionMovies ?: emptyList()

            mutableSearchUIState.value = SearchUIModel.Loading

            movieRepository.searchMovies(query, pageNum).collect { searchResults ->
                if (isAdvanced) {
                    collectAdvancedMovies(searchResults)
                } else {
                    collectMovies(pageNum, searchResults, currentMovies)
                }
            }
        }
    }

    fun discoverMovies(query: String, pageNum: Int) {
        viewModelScope.launch {

            Log.d("SearchViewModel", "SelectedCategories: $selectedCategories")
            if (query.isBlank()) {
                if (selectedCategories.isEmpty()) {
                    mutableSearchUIState.value = SearchUIModel.Data(popularMovies, 1)
                    currentPage.intValue = 1
                    totalPages.value = 0
                    isViewingSearchResults.value = false
                    lastActiveTab.value = 0
                    return@launch
                }

                val currentMovies =
                    (mutableSearchUIState.value as? SearchUIModel.Data)?.collectionMovies
                        ?: emptyList()
                var releaseDateGte: String? = null
                var releaseDateLte: String? = null
                var withGenres: String? = null
                var withWatchProviders: String? = null

                isViewingSearchResults.value = true
                lastActiveTab.value = 1
                if (selectedCategories["Decade"] != null && selectedCategories["Decade"]?.values?.isNotEmpty() == true) {
                    val decade = selectedCategories["Decade"]?.values?.first()
                    releaseDateGte = "$decade-01-01"
                    releaseDateLte = (decade?.toInt()?.plus(9)).toString() + "-01-01"
                }

                if (selectedCategories["Popular Genres"] != null) {
                    withGenres = selectedCategories["Popular Genres"]?.values?.joinToString(",")
                }

                if (selectedCategories["Streaming Services"] != null) {
                    withWatchProviders =
                        selectedCategories["Streaming Services"]?.values?.joinToString("|")
                }

                Log.d(
                    "SearchViewModel",
                    "ReleaseDateGte: $releaseDateGte, ReleaseDateLte: $releaseDateLte, " +
                            "WithGenres: $withGenres"
                )

                mutableSearchUIState.value = SearchUIModel.Loading
                movieRepository.discoverMovies(
                    pageNum,
                    releaseDateGte,
                    releaseDateLte,
                    null,
                    "DK",
                    withGenres,
                    withWatchProviders
                ).collect { searchResults ->
                    collectMovies(pageNum, searchResults, currentMovies)
                }
            } else {

                Log.d("SearchViewModel", "Query: $selectedCategories")
                if (selectedCategories.values.all { it.isEmpty() } || selectedCategories.isEmpty()) {
                    searchMovies(query, pageNum, false)
                } else {
                    searchMovies(query, pageNum, true)
                }
            }

        }
    }

    private fun collectAdvancedMovies(
        searchResults: MovieSearchResult,
    ) {
        val updatedMovies =
            searchResults.movies.filter { movie ->
                var releaseDates: List<Int> = emptyList()
                Log.d("SearchViewModel", "ReleaseDates: $releaseDates")
                movie.genres?.filter { genre ->
                    selectedCategories["Popular Genres"]?.values?.contains(genre.toString()) == true
                }?.isNotEmpty() == true ||
                        selectedCategories["Decade"]?.values?.any { decade ->
                            for (i in 0..9) {
                                releaseDates += decade.toInt() + i
                            }
                            releaseDates.contains(decade.toInt())
                        } == true
            }

        mutableSearchUIState.value = if (updatedMovies.isEmpty()) {
            SearchUIModel.Empty
        } else {
            SearchUIModel.Data(updatedMovies, 0)
        }

        totalPages.value = 0
        currentPage.intValue = 1
    }

    private fun collectMovies(
        pageNum: Int,
        searchResults: MovieSearchResult,
        currentMovies: List<Movie>
    ) {
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


    fun onCategorySelected(categoryTitle: String, key: String, value: String, isSelected: Boolean) {
        val currentItems = selectedCategories[categoryTitle]?.toMutableMap() ?: mutableMapOf()

        if (categoryTitle == "Decade") {
            if (isSelected) {
                currentItems.clear()
                currentItems[key] = value
            } else {
                currentItems.remove(key)
            }
        } else {
            if (isSelected) {
                currentItems[key] = value
            } else {
                currentItems.remove(key)
            }
        }

        selectedCategories[categoryTitle] = currentItems
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
            isViewingSearchResults.value = false
            lastActiveTab.value = 0
        }
    }

    fun resetToPopularMovies() {
        viewModelScope.launch {
            mutableSearchUIState.value = SearchUIModel.Loading
            loadPopularMovies()
        }
    }

    sealed class SearchUIModel {
        data object Empty : SearchUIModel()
        data object Loading : SearchUIModel()
        data object NoConnection : SearchUIModel()
        data object ApiError : SearchUIModel()
        data class Data(
            val collectionMovies: List<Movie>,
            val totalPages: Int?
        ) : SearchUIModel()
    }
}