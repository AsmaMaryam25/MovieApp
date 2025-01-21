package com.example.blackbeard.screens.search.tabSearch

import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateMapOf
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.blackbeard.di.DataModule
import com.example.blackbeard.domain.RecentSearchRepository
import com.example.blackbeard.models.MovieSearchResult
import com.example.blackbeard.models.SearchMovie
import com.example.blackbeard.utils.ConnectivityObserver.isConnected
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class TabSearchViewModel(): ViewModel() {
    
    private val movieRepository = DataModule.movieRepository
    private val mutabletabSearchUIState = MutableStateFlow<TabSearchUIModel>(
        TabSearchUIModel.Empty
    )
    val tabSearchUIState: StateFlow<TabSearchUIModel> = mutabletabSearchUIState

    val initialConnectivityFlow: Flow<Boolean> = isConnected
    var currentPage = mutableIntStateOf(1)
        private set

    var totalPages = mutableStateOf<Int?>(null)
    var searchType = mutableStateOf(false)
    val selectedCategories = mutableStateMapOf<String, MutableMap<String, String>>()

    private val _recentSearches = MutableStateFlow<List<String>>(emptyList())
    val recentSearches: StateFlow<List<String>> = _recentSearches

    private val recentSearchRepository: RecentSearchRepository = DataModule.recentSearchRepository

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


        mutabletabSearchUIState.value = if (updatedMovies.isEmpty()) {
            TabSearchUIModel.Empty
        } else {
            TabSearchUIModel.Data(updatedMovies, 0)
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

        mutabletabSearchUIState.value = if (updatedMovies.isEmpty()) {
            TabSearchUIModel.Empty
        } else {
            TabSearchUIModel.Data(updatedMovies, searchResults.totalPages)
        }
        totalPages.value = searchResults.totalPages
        currentPage.intValue = pageNum
    }

    fun searchMovies(query: String, pageNum: Int, isAdvanced: Boolean = false) {
        viewModelScope.launch {
            if (query.isBlank()) {
                mutabletabSearchUIState.value = TabSearchUIModel.NoResults
                currentPage.intValue = 1
                totalPages.value = 0
                return@launch
            }

            addRecentSearch(query)

            val currentMovies =
                (mutabletabSearchUIState.value as? TabSearchUIModel.Data)?.searchMovies ?: emptyList()

            mutabletabSearchUIState.value = TabSearchUIModel.Loading

            movieRepository.searchMovies(query, pageNum).collect { searchResults ->
                if (isAdvanced) {
                    collectAdvancedMovies(searchResults)
                } else {
                    collectMovies(pageNum, searchResults, currentMovies)
                }
            }
        }
    }

    fun addRecentSearch(query: String) {
        viewModelScope.launch {
            recentSearchRepository.addRecentSearch(query)
        }
    }

    fun discoverMovies(query: String, pageNum: Int) {
        viewModelScope.launch {
            if (query.isBlank()) {
                if (selectedCategories.isEmpty()) {
                    mutabletabSearchUIState.value = TabSearchUIModel.NoResults
                    currentPage.intValue = 1
                    totalPages.value = 0
                    return@launch
                }

                val currentMovies =
                    (mutabletabSearchUIState.value as? TabSearchUIModel.Data)?.searchMovies
                        ?: emptyList()
                var releaseDateGte: String? = null
                var releaseDateLte: String? = null
                var withGenres: String? = null
                var withWatchProviders: String? = null
                var withRuntimeGte: String? = null

                if (selectedCategories["Runtime"] != null && selectedCategories["Runtime"]?.values?.isNotEmpty() == true) {
                    withRuntimeGte = selectedCategories["Runtime"]?.values?.first()
                }

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

                mutabletabSearchUIState.value = TabSearchUIModel.Loading
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
            } else {
                if (selectedCategories.values.all { it.isEmpty() } || selectedCategories.isEmpty()) {
                    searchMovies(query, pageNum, false)
                } else {
                    searchMovies(query, pageNum, true)
                }
            }

        }
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

        }
        else if (categoryTitle == "Runtime") {
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

    sealed class TabSearchUIModel {
        data object Empty : TabSearchUIModel()
        data object Loading : TabSearchUIModel()
        data object NoConnection : TabSearchUIModel()
        data object ApiError : TabSearchUIModel()
        data object NoResults : TabSearchUIModel()
        data class Data(
            val searchMovies: List<SearchMovie>,
            val totalPages: Int?
        ) : TabSearchUIModel()
    }
}