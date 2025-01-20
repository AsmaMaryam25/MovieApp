package com.example.blackbeard.screens.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.blackbeard.di.DataModule
import com.example.blackbeard.domain.Result
import com.example.blackbeard.models.CollectionMovie
import com.example.blackbeard.utils.ConnectivityObserver.isConnected
import kotlinx.coroutines.TimeoutCancellationException
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import kotlinx.coroutines.withTimeout
import java.net.UnknownHostException

class HomeViewModel : ViewModel() {

    private val movieRepository = DataModule.movieRepository
    private val mutableHomeUIState = MutableStateFlow<HomeUIModel>(HomeUIModel.Empty)
    val homeUIState: StateFlow<HomeUIModel> = mutableHomeUIState
    val initialConnectivityFlow: Flow<Boolean> = isConnected

    private val nowPlayingCollectionMovies = mutableListOf<CollectionMovie>()
    private val popularCollectionMovies = mutableListOf<CollectionMovie>()
    private val topRatedCollectionMovies = mutableListOf<CollectionMovie>()
    private val upcomingCollectionMovies = mutableListOf<CollectionMovie>()

    private val failedCollections = mutableListOf<String>()

    init {
        viewModelScope.launch {

            try {
                mutableHomeUIState.value = HomeUIModel.Loading

                val isInitiallyConnected = withTimeout(5000L) {
                    initialConnectivityFlow.first()
                }

                if (isInitiallyConnected) {
                    fetchMovies()
                } else {
                    mutableHomeUIState.value = HomeUIModel.NoConnection
                }


            } catch (e: Exception) {
                mutableHomeUIState.value = HomeUIModel.NoConnection
            }
        }
    }

    private suspend fun fetchNowPlayingMovies() {
        movieRepository.getNowPlayingMovies().collect { result ->
            when (result) {
                is Result.Error -> {
                    failedCollections.add("now playing")
                }

                is Result.Success -> {
                    nowPlayingCollectionMovies.addAll(result.data)
                }
            }
        }
    }

    private suspend fun fetchPopularMovies() {
        movieRepository.getPopularMovies().collect { result ->
            when (result) {
                is Result.Error -> {
                    failedCollections.add("popular")
                }

                is Result.Success -> {
                    popularCollectionMovies.addAll(result.data)
                }
            }
        }
    }

    private suspend fun fetchUpcomingMovies() {
        movieRepository.getUpcomingMovies().collect { result ->
            when (result) {
                is Result.Error -> {
                    failedCollections.add("upcoming")
                }

                is Result.Success -> {
                    upcomingCollectionMovies.addAll(result.data)
                }
            }
        }
    }

    private fun fetchTopRatedMovies() {
        viewModelScope.launch {
            movieRepository.getTopRatedMovies().collect { result ->
                when (result) {
                    is Result.Error -> {
                        failedCollections.add("top rated")
                    }

                    is Result.Success -> {
                        topRatedCollectionMovies.addAll(result.data)
                    }
                }
            }
        }
    }

    private suspend fun fetchMovies() {
        fetchNowPlayingMovies()
        fetchUpcomingMovies()
        fetchTopRatedMovies()
        fetchPopularMovies()
        mutableHomeUIState.value = HomeUIModel.Data(
            nowPlayingCollectionMovies = nowPlayingCollectionMovies,
            upcomingCollectionMovies = upcomingCollectionMovies,
            popularCollectionMovies = popularCollectionMovies,
            topRatedCollectionMovies = topRatedCollectionMovies
        )
    }

    sealed class HomeUIModel {
        data object Empty : HomeUIModel()
        data object Loading : HomeUIModel()
        data object NoConnection : HomeUIModel()
        data class Data(
            val nowPlayingCollectionMovies: List<CollectionMovie>,
            val popularCollectionMovies: List<CollectionMovie>,
            val topRatedCollectionMovies: List<CollectionMovie>,
            val upcomingCollectionMovies: List<CollectionMovie>
        ) : HomeUIModel()
    }
}