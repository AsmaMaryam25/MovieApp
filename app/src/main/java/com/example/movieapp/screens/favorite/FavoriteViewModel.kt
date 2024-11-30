package com.example.movieapp.screens.favorite

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.movieapp.data.model.MovieItem
import com.example.movieapp.di.DataModule
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class FavoriteViewModel : ViewModel() {

    private val movieRepository = DataModule.movieRepository

    private val mutableDetailsUIState = MutableStateFlow<FavoriteUIModel>(FavoriteUIModel.Empty)
    val favoriteUIState: StateFlow<FavoriteUIModel> = mutableDetailsUIState

    init {
        viewModelScope.launch {
            mutableDetailsUIState.update {
                FavoriteUIModel.Loading
            }

            movieRepository.getFavorites().collect { favorites ->
                val updatedFavorites = favorites.map { movieItem ->
                    movieItem.copy(
                        rating = movieRepository.getAverageRating(movieItem.id)
                    )
                }

                mutableDetailsUIState.update {
                    FavoriteUIModel.Data(
                        favorites = updatedFavorites
                    )
                }
            }
        }

    }

    sealed class FavoriteUIModel {
        data object Empty : FavoriteUIModel()
        data object Loading : FavoriteUIModel()
        data class Data(
            val favorites: List<MovieItem>
        ) : FavoriteUIModel()
    }
}
