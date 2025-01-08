package com.example.blackbeard.screens.home

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.blackbeard.di.DataModule
import com.example.blackbeard.models.CollectionMovie
import com.example.blackbeard.utils.ConnectivityObserver.isConnected
import kotlinx.coroutines.TimeoutCancellationException
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import kotlinx.coroutines.withTimeout
import java.net.UnknownHostException

class HomeViewModel : ViewModel() {

    private val movieRepository = DataModule.movieRepository
    private val mutableHomeUIState = MutableStateFlow<HomeUIModel>(HomeUIModel.Empty)
    val homeUIState: StateFlow<HomeUIModel> = mutableHomeUIState
    val initialConnectivityFlow: Flow<Boolean> = isConnected

    init {
        viewModelScope.launch {

            try {
                mutableHomeUIState.value = HomeUIModel.Loading

                val isInitiallyConnected = withTimeout(5000L) {
                    initialConnectivityFlow.first()
                }

                if (isInitiallyConnected) {
                    getMovies()
                } else {
                    mutableHomeUIState.value = HomeUIModel.NoConnection
                }


            } catch (e: TimeoutCancellationException) {
                mutableHomeUIState.value = HomeUIModel.NoConnection
            } catch (e: UnknownHostException){
                mutableHomeUIState.value = HomeUIModel.NoConnection
            }
        }
    }

    private suspend fun getMovies() {
        combine(
            movieRepository.getNowPlayingMovies(),
            movieRepository.getPopularMovies(),
            movieRepository.getTopRatedMovies(),
            movieRepository.getUpcomingMovies()
        ) { nowPlaying, popular, topRated, upcoming ->
            HomeUIModel.Data(
                nowPlayingCollectionMovies = nowPlaying,
                popularCollectionMovies = popular,
                topRatedCollectionMovies = topRated,
                upcomingCollectionMovies = upcoming
            )
        }.collect { homeUIModel ->
            mutableHomeUIState.value = homeUIModel
        }
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