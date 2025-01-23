package com.example.blackbeard.screens.search

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.blackbeard.di.DataModule
import com.example.blackbeard.domain.RecentSearchRepository
import com.example.blackbeard.models.CollectionMovie
import com.example.blackbeard.utils.ConnectivityObserver.isConnected
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import kotlinx.coroutines.withTimeout
import retrofit2.HttpException

class SearchViewModel() : ViewModel() {

    private val movieRepository = DataModule.movieRepository
    private val mutableSearchUIState = MutableStateFlow<SearchUIModel>(SearchUIModel.Loading)
    val searchUIState: StateFlow<SearchUIModel> = mutableSearchUIState

    var popularMovies: List<CollectionMovie> = emptyList()
    val initialConnectivityFlow: Flow<Boolean> = isConnected

    private val _recentSearches = MutableStateFlow<List<String>>(emptyList())
    val recentSearches: StateFlow<List<String>> = _recentSearches

    private val recentSearchRepository: RecentSearchRepository = DataModule.recentSearchRepository

    init {
        viewModelScope.launch {
            try {

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
            } catch (_: HttpException) {
                mutableSearchUIState.value = SearchUIModel.ApiError
            } catch (_: Exception) {
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
            if (result != null) {
                popularMovies = result
                mutableSearchUIState.value = SearchUIModel.Data(result, 1)
            }
        }
    }

    sealed class SearchUIModel {
        data object Empty : SearchUIModel()
        data object Loading : SearchUIModel()
        data object NoConnection : SearchUIModel()
        data object ApiError : SearchUIModel()
        data class Data(
            val collectionMovies: List<CollectionMovie>,
            val totalPages: Int?
        ) : SearchUIModel()
    }
}