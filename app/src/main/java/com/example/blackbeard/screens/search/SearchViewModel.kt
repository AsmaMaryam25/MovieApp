package com.example.blackbeard.screens.search

import android.annotation.SuppressLint
import androidx.compose.runtime.State
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.blackbeard.di.DataModule
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

class SearchViewModel() : ViewModel() {

    private val movieRepository = DataModule.movieRepository
    private val mutableSearchUIState = MutableStateFlow<SearchUIModel>(SearchUIModel.Empty)
    val searchUIState: StateFlow<SearchUIModel> = mutableSearchUIState
    var popularMovies: List<CollectionMovie> = emptyList()
    val initialConnectivityFlow: Flow<Boolean> = isConnected
    var currentPage = mutableIntStateOf(1)
        private set

    var totalPages = mutableStateOf<Int?>(null)


    init {
        viewModelScope.launch {
            try {
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

    private val _recentSearches = mutableStateOf<List<String>>(emptyList())
    val recentSearches: State<List<String>> = _recentSearches

    @SuppressLint("NewApi")
    fun addRecentSearch(query: String) {
        _recentSearches.value = _recentSearches.value.toMutableList().apply {
            if (!contains(query)) {
                add(0, query)
                if (size > 10) removeAt(size - 1)
            }
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