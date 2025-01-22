package com.example.blackbeard.screens.search.content

import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.blackbeard.di.DataModule
import com.example.blackbeard.models.MovieSearchResult
import com.example.blackbeard.models.SearchMovie
import com.example.blackbeard.utils.ConnectivityObserver.isConnected
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import kotlinx.coroutines.withTimeout
import kotlinx.serialization.json.Json
import retrofit2.HttpException

class SearchContentViewModel(query: String, isAdvancedSearch: Boolean): ViewModel(){

    private val movieRepository = DataModule.movieRepository
    private val mutableSearchContentUIState = MutableStateFlow<SearchContentUIModel>(
        SearchContentUIModel.Empty
    )
    val searchContentUIState: StateFlow<SearchContentUIModel> = mutableSearchContentUIState

    val initialConnectivityFlow: Flow<Boolean> = isConnected
    var currentPage = mutableIntStateOf(1)
        private set

    var totalPages = mutableStateOf<Int?>(null)

    val selectedCategories = (Json.decodeFromString(query) as Map<String, Map<String, String>>)

    init {
        viewModelScope.launch {
            try {
                mutableSearchContentUIState.value = SearchContentUIModel.Loading

                val isInitiallyConnected = withTimeout(5000L) {
                    initialConnectivityFlow.first()
                }

                if (!isAdvancedSearch && isInitiallyConnected) {
                    searchMovies(query, 1)
                } else if (isAdvancedSearch && isInitiallyConnected) {
                    discoverMovies(selectedCategories, 1)
                } else {
                    mutableSearchContentUIState.value = SearchContentUIModel.NoConnection
                }

                initialConnectivityFlow.stateIn(
                    viewModelScope,
                    SharingStarted.WhileSubscribed(5000L),
                    isInitiallyConnected
                ).collect { isConnected ->
                    if (!isAdvancedSearch && isConnected) {
                        searchMovies(query, 1)
                    } else if (isAdvancedSearch && isConnected) {
                        discoverMovies(selectedCategories, 1)
                    } else {
                        mutableSearchContentUIState.value = SearchContentUIModel.NoConnection
                    }
                }
            } catch (e: HttpException) {
                mutableSearchContentUIState.value = SearchContentUIModel.ApiError
            } catch (e: Exception) {
                mutableSearchContentUIState.value = SearchContentUIModel.NoConnection

                initialConnectivityFlow.stateIn(
                    viewModelScope,
                    SharingStarted.WhileSubscribed(5000L),
                    false
                ).collect { isConnected ->
                    if (!isAdvancedSearch && isConnected) {
                        searchMovies(query, 1)
                    } else if (isAdvancedSearch && isConnected) {
                        discoverMovies(selectedCategories, 1)
                    } else {
                        mutableSearchContentUIState.value = SearchContentUIModel.NoConnection
                    }
                }
            }
        }
    }

    private fun collectAdvancedMovies(
        searchResults: MovieSearchResult,
    ) {

        val updatedMovies =
            searchResults.movies
                .filter { movie ->
                    var releaseDates: List<Int> = emptyList()
                    movie.genres?.filter { genre ->
                        selectedCategories["Popular Genres"]?.values?.contains(genre.toString()) == true
                    }?.isNotEmpty() == true ||

                            selectedCategories["Decade"]?.values?.any { decade ->
                                for (i in 0..9) {
                                    releaseDates += decade.toInt() + i
                                }
                                releaseDates.contains(decade.toInt())
                            } == true || selectedCategories["Runtime"]?.values?.any { runtime -> runtime.toIntOrNull() != null } == true


                }


        mutableSearchContentUIState.value = if (updatedMovies.isEmpty()) {
            SearchContentUIModel.Empty
        } else {
            SearchContentUIModel.Data(updatedMovies, 0)
        }

        totalPages.value = 0
        currentPage.intValue = 1
    }

    private fun collectMovies(
        pageNum: Int,
        searchResults: MovieSearchResult,
        currentMovies: List<SearchMovie>
    ) {
        val updatedMovies =
            if (pageNum == 1) {
                searchResults.movies
            } else {
                currentMovies + searchResults.movies
            }

        mutableSearchContentUIState.value = if (updatedMovies.isEmpty()) {
            SearchContentUIModel.Empty
        } else {
            SearchContentUIModel.Data(updatedMovies, searchResults.totalPages)
        }
        totalPages.value = searchResults.totalPages
        currentPage.intValue = pageNum
    }

    fun searchMovies(query: String, pageNum: Int, isAdvanced: Boolean = false) {
        viewModelScope.launch {
            if (query.isBlank()) {
                mutableSearchContentUIState.value = SearchContentUIModel.NoResults
                currentPage.intValue = 1
                totalPages.value = 0
                return@launch
            }

            val currentMovies =
                (mutableSearchContentUIState.value as? SearchContentUIModel.Data)?.searchMovies ?: emptyList()

            mutableSearchContentUIState.value = SearchContentUIModel.Loading

            movieRepository.searchMovies(query, pageNum).collect { searchResults ->
                if (isAdvanced) {
                    collectAdvancedMovies(searchResults)
                } else {
                    collectMovies(pageNum, searchResults, currentMovies)
                }
            }
        }
    }

    fun discoverMovies(query: Map<String, Map<String, String>>, pageNum: Int) {
        viewModelScope.launch {
            //if (query.isBlank()) {
                if (query.isEmpty()) {
                    mutableSearchContentUIState.value = SearchContentUIModel.NoResults
                    currentPage.intValue = 1
                    totalPages.value = 0
                    return@launch
                }

                val currentMovies =
                    (mutableSearchContentUIState.value as? SearchContentUIModel.Data)?.searchMovies
                        ?: emptyList()
                var releaseDateGte: String? = null
                var releaseDateLte: String? = null
                var withGenres: String? = null
                var withWatchProviders: String? = null
                var withRuntimeGte: String? = null

                if (query["Runtime"] != null && query["Runtime"]?.values?.isNotEmpty() == true) {
                    withRuntimeGte = query["Runtime"]?.values?.first()
                }

                if (query["Decade"] != null && query["Decade"]?.values?.isNotEmpty() == true) {
                    val decade = query["Decade"]?.values?.first()
                    releaseDateGte = "$decade-01-01"
                    releaseDateLte = (decade?.toInt()?.plus(9)).toString() + "-01-01"
                }

                if (query["Popular Genres"] != null) {
                    withGenres = query["Popular Genres"]?.values?.joinToString(",")
                }

                if (query["Streaming Services"] != null) {
                    withWatchProviders =
                        query["Streaming Services"]?.values?.joinToString("|")
                }

                mutableSearchContentUIState.value = SearchContentUIModel.Loading
                movieRepository.discoverMovies(
                    pageNum,
                    releaseDateGte,
                    releaseDateLte,
                    null,
                    "DK",
                    withGenres,
                    withWatchProviders,
                    withRuntimeGte
                ).collect { searchResults ->
                    collectMovies(pageNum, searchResults, currentMovies)
                }
            //} else {
            /*
                if (selectedCategories.values.all { it.isEmpty() } || selectedCategories.isEmpty()) {
                    searchMovies(query, pageNum, false)
                } else {
                    searchMovies(query, pageNum, true)
                }
            }
            */

        }
    }

    sealed class SearchContentUIModel {
        data object Empty : SearchContentUIModel()
        data object Loading : SearchContentUIModel()
        data object NoConnection : SearchContentUIModel()
        data object ApiError : SearchContentUIModel()
        data object NoResults : SearchContentUIModel()
        data class Data(
            val searchMovies: List<SearchMovie>,
            val totalPages: Int?
        ) : SearchContentUIModel()
    }
    
    
}