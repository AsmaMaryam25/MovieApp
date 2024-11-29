package com.example.movieapp.screens.details

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.movieapp.di.DataModule
import com.example.movieapp.models.Credits
import com.example.movieapp.models.Movie
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.launch

class DetailsViewModel(val movieId: Int) : ViewModel() {

    private val movieRepository = DataModule.movieRepository
    private val mutableDetailsUIState = MutableStateFlow<DetailsUIModel>(DetailsUIModel.Empty)
    val detailsUIState: StateFlow<DetailsUIModel> = mutableDetailsUIState

    init {
        viewModelScope.launch {
            mutableDetailsUIState.value = DetailsUIModel.Loading
            
            combine(
                movieRepository.getMovie(movieId),
                movieRepository.getCredits(movieId),
                movieRepository.getVideoLink(movieId)
            ) { movie, credits, videoLink ->
                DetailsUIModel.Data(movie, credits, videoLink)
            }.collect { detailsUIModel ->
                mutableDetailsUIState.value = detailsUIModel
            }
        }
    }

    sealed class DetailsUIModel {
        data object Empty : DetailsUIModel()
        data object Loading : DetailsUIModel()
        data class Data(
            val movie: Movie,
            val credits: Credits,
            val videoLink: String? = null
        ) : DetailsUIModel()
    }
}