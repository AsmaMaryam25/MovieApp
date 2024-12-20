package com.example.blackbeard.screens.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.blackbeard.di.DataModule
import com.example.blackbeard.models.CollectionMovie
import com.example.blackbeard.utils.ConnectivityObserver
import com.example.blackbeard.utils.ConnectivityObserver.isConnected
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class HomeViewModel : ViewModel() {

    private val movieRepository = DataModule.movieRepository
    private val mutableHomeUIState = MutableStateFlow<HomeUIModel>(HomeUIModel.Empty)
    val homeUIState: StateFlow<HomeUIModel> = mutableHomeUIState
    val connectivityFlow: Flow<Boolean> = isConnected.stateIn(
        viewModelScope,
        SharingStarted.WhileSubscribed(5000L),
        false
    )

    init {
        viewModelScope.launch {

            mutableHomeUIState.value = HomeUIModel.Loading

            connectivityFlow.collect {
                if (it) {
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
                } else {
                    mutableHomeUIState.value = HomeUIModel.NoConnection
                }
            }
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