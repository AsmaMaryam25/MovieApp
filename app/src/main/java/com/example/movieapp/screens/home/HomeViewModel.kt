package com.example.movieapp.screens.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.movieapp.di.DataModule
import com.example.movieapp.models.Movie
import com.example.movieapp.models.MovieCategory.*
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class HomeViewModel : ViewModel() {

    private val movieRepository = DataModule.movieRepository
    private val mutableHomeUIState = MutableStateFlow<HomeUIModel>(HomeUIModel.Empty)
    val homeUIState: StateFlow<HomeUIModel> = mutableHomeUIState

    init {
        viewModelScope.launch {
            movieRepository.moviesFlow
                .collect { movies ->
                    mutableHomeUIState.value = HomeUIModel.Data(
                        nowPlayingMovies = movies.filter { it.category == NOW_PLAYING },
                        popularMovies = movies.filter { it.category == POPULAR },
                        topRatedMovies = movies.filter { it.category == TOP_RATED },
                        upcomingMovies = movies.filter { it.category == UPCOMING }
                    )
                }
        }

        getNowPlayingMovies()

        getPopularMovies()

        getTopRatedMovies()

        getUpcomingMovies()

    }

    private fun getNowPlayingMovies() {
        viewModelScope.launch {
            movieRepository.getNowPlayingMovies()
        }
    }

    private fun getPopularMovies() {
        viewModelScope.launch {
            movieRepository.getPopularMovies()
        }
    }

    private fun getTopRatedMovies() {
        viewModelScope.launch {
            movieRepository.getTopRatedMovies()
        }
    }

    private fun getUpcomingMovies() {
        viewModelScope.launch {
            movieRepository.getUpcomingMovies()
        }

    }

    sealed class HomeUIModel {
        data object Empty : HomeUIModel()
        data object Loading : HomeUIModel()
        data class Data(
            val nowPlayingMovies: List<Movie>,
            val popularMovies: List<Movie>,
            val topRatedMovies: List<Movie>,
            val upcomingMovies: List<Movie>
        ) : HomeUIModel()
    }
}