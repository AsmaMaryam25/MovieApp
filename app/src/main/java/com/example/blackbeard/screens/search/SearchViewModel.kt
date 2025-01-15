package com.example.blackbeard.screens.search

import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateMapOf
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.blackbeard.di.DataModule
import com.example.blackbeard.domain.RecentSearchRepository
import com.example.blackbeard.models.CollectionMovie
import com.example.blackbeard.models.Movie
import com.example.blackbeard.models.SearchMovie
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
    val selectedItems = mutableStateMapOf<Int, List<String>>()


    private val _recentSearches = MutableStateFlow<List<String>>(emptyList())
    val recentSearches: StateFlow<List<String>> = _recentSearches

    private val recentSearchRepository: RecentSearchRepository = DataModule.recentSearchRepository

    init {
        viewModelScope.launch {
            try {
                searchType.value = false
                selectedItems.clear()
                mutableSearchUIState.value = SearchUIModel.Loading

                val isInitiallyConnected = withTimeout(5000L) {
                    initialConnectivityFlow.first()
                }

                if (isInitiallyConnected) {
                    loadPopularMovies()
                } else {
                    mutableSearchUIState.value = SearchUIModel.NoConnection
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

    fun advanceSearchMovies(query: String, pageNum: Int, selectedItems: Map<Int, List<String>>) {
        viewModelScope.launch {
            if (query.isBlank()) {
                val currentMovies =
                    (mutableSearchUIState.value as? SearchUIModel.AdvanceSearchData)?.collectionMovies
                        ?: emptyList()

                mutableSearchUIState.value = SearchUIModel.Loading
                var ratingGte: String? = null
                var genreStr: String? = null
                for (i in selectedItems.keys) {
                    selectedItems[i]?.let { list ->
                        if (list.isNotEmpty()) {
                            if (i == 0)
                                ratingGte = getSmallestRating(list)
                            else if (i == 1)
                                genreStr = convertListToString(list)
                        }
                    }
                }
                movieRepository.discoverMovies(genreStr, ratingGte, pageNum,selectedItems[2])
                    .collect { searchResults ->
                        val filteredMovies = searchResults.movies

                        val updatedMovies = if (pageNum == 1) {
                            filteredMovies
                        } else {
                            currentMovies + filteredMovies
                        }

                        mutableSearchUIState.value = if (updatedMovies.isEmpty()) {
                            SearchUIModel.Empty
                        } else {
                            SearchUIModel.AdvanceSearchData(updatedMovies, searchResults.totalPages)
                        }

                        totalPages.value = searchResults.totalPages
                        currentPage.intValue = pageNum
                    }
            } else {

                val currentMovies =
                    (mutableSearchUIState.value as? SearchUIModel.AdvanceSearchData)?.collectionMovies
                        ?: emptyList()
                mutableSearchUIState.value = SearchUIModel.Loading

                movieRepository.advanceSearchMovies(query, pageNum,selectedItems[2]).collect { searchResults ->
                    val filteredMovies = searchResults.movies.filter { movie ->
                        selectedItems.all { (categoryIndex, selectedValues) ->
                            when (categoryIndex) {
                                0 -> {
                                    selectedValues.any { selectedValue ->
                                        val selectedRating = selectedValue.toDoubleOrNull() ?: 0.0
                                        movie.voteAverage >= selectedRating
                                    }
                                }

                                1 -> {
                                    selectedValues.any { selectedValue ->
                                        val selectedGenreId = selectedValue.toIntOrNull()
                                        movie.genres?.contains(selectedGenreId) == true
                                    }
                                }

                                else -> true
                            }
                        }
                    }

                    val updatedMovies = if (pageNum == 1) {
                        filteredMovies
                    } else {
                        currentMovies + filteredMovies
                    }

                    mutableSearchUIState.value = if (updatedMovies.isEmpty()) {
                        SearchUIModel.Empty
                    } else {
                        SearchUIModel.AdvanceSearchData(updatedMovies, searchResults.totalPages)
                    }

                    totalPages.value = searchResults.totalPages
                    currentPage.intValue = pageNum
                }
            }
        }
    }

    private fun convertListToString(list: List<String>): String {
        return if (list.isEmpty()) "" else list.joinToString("|")
    }

    private fun getSmallestRating(list: List<String>): String {
        return if (list.isEmpty()) "" else list.minByOrNull {
            it.toDoubleOrNull() ?: Double.MAX_VALUE
        } ?: ""
    }

    init {
        viewModelScope.launch {
            recentSearchRepository.getRecentSearches().collect {
                _recentSearches.value = it
            }
        }
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

        data class AdvanceSearchData(
            val collectionMovies: List<SearchMovie>,
            val totalPages: Int?
        ) : SearchUIModel()
    }
}