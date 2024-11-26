package com.example.movieapp.screens.details

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.movieapp.di.DataModule
import com.example.movieapp.models.Movie
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class DetailsViewModel(val movieId: Int) : ViewModel() {

    private val movieRepository = DataModule.movieRepository
    private val mutableDetailsUIState = MutableStateFlow<DetailsUIModel>(DetailsUIModel.Empty)
    val detailsUIState: StateFlow<DetailsUIModel> = mutableDetailsUIState

    init {
        viewModelScope.launch {
            movieRepository.getMovie(movieId).collect { movie ->
                mutableDetailsUIState.value = DetailsUIModel.Data(movie)
            }
        }
    }

    sealed class DetailsUIModel {
        data object Empty : DetailsUIModel()
        data object Loading : DetailsUIModel()
        data class Data(
            val movie: Movie
        ) : DetailsUIModel()
    }
}