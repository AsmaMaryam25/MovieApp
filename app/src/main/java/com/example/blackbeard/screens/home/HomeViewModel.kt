package com.example.blackbeard.screens.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.blackbeard.di.DataModule
import com.example.blackbeard.models.CollectionMovie
import com.example.blackbeard.utils.ConnectivityObserver.isConnected
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import kotlinx.coroutines.withTimeout
import retrofit2.HttpException

class HomeViewModel : ViewModel() {

    private val movieRepository = DataModule.movieRepository
    private val mutableHomeUIState = MutableStateFlow<HomeUIModel>(HomeUIModel.Empty)
    val homeUIState: StateFlow<HomeUIModel> = mutableHomeUIState
    private val initialConnectivityFlow: Flow<Boolean> = isConnected

    private val nowPlayingCollectionMovies = mutableListOf<CollectionMovie>()
    private val popularCollectionMovies = mutableListOf<CollectionMovie>()
    private val topRatedCollectionMovies = mutableListOf<CollectionMovie>()
    private val upcomingCollectionMovies = mutableListOf<CollectionMovie>()

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

            } catch (e: HttpException) {
                mutableHomeUIState.value = HomeUIModel.ApiError
            } catch (e: Exception) {
                mutableHomeUIState.value = HomeUIModel.NoConnection
            }
        }
    }

    private suspend fun fetchNowPlayingMovies() {
        movieRepository.getNowPlayingMovies().collect { result ->
            if (result != null) {
                nowPlayingCollectionMovies.addAll(result)
            }
        }
    }

    private suspend fun fetchPopularMovies() {
        movieRepository.getPopularMovies().collect { result ->
            if (result != null) {
                popularCollectionMovies.addAll(result)
            }
        }
    }

    private suspend fun fetchUpcomingMovies() {
        movieRepository.getUpcomingMovies().collect { result ->
            if (result != null) {
                upcomingCollectionMovies.addAll(result)
            }
        }
    }

    private suspend fun fetchTopRatedMovies() {
        movieRepository.getTopRatedMovies().collect { result ->
            if (result != null) {
                topRatedCollectionMovies.addAll(result)
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
        data object ApiError : HomeUIModel()
        data class Data(
            val nowPlayingCollectionMovies: List<CollectionMovie>,
            val popularCollectionMovies: List<CollectionMovie>,
            val topRatedCollectionMovies: List<CollectionMovie>,
            val upcomingCollectionMovies: List<CollectionMovie>
        ) : HomeUIModel()
    }
}