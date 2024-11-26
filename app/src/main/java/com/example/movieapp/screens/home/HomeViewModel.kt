package com.example.movieapp.screens.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.movieapp.di.DataModule
import com.example.movieapp.models.CollectionMovie
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.launch

class HomeViewModel : ViewModel() {

    private val movieRepository = DataModule.movieRepository
    private val mutableHomeUIState = MutableStateFlow<HomeUIModel>(HomeUIModel.Empty)
    val homeUIState: StateFlow<HomeUIModel> = mutableHomeUIState

    init {
        viewModelScope.launch {
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
    }

    sealed class HomeUIModel {
        data object Empty : HomeUIModel()
        data object Loading : HomeUIModel()
        data class Data(
            val nowPlayingCollectionMovies: List<CollectionMovie>,
            val popularCollectionMovies: List<CollectionMovie>,
            val topRatedCollectionMovies: List<CollectionMovie>,
            val upcomingCollectionMovies: List<CollectionMovie>
        ) : HomeUIModel()
    }
}