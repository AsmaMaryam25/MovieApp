package com.example.blackbeard.screens.favorite

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.blackbeard.data.model.MovieItem
import com.example.blackbeard.di.DataModule
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class FavoriteViewModel : ViewModel() {

    private val movieRepository = DataModule.movieRepository

    private val mutableFavoriteUIState = MutableStateFlow<FavoriteUIModel>(FavoriteUIModel.Empty)
    val favoriteUIState: StateFlow<FavoriteUIModel> = mutableFavoriteUIState

    init {
        viewModelScope.launch {
            mutableFavoriteUIState.update {
                FavoriteUIModel.Loading
            }

            movieRepository.getFavorites().collect { favorites ->
                val updatedFavorites = favorites.map { movieItem ->
                    movieItem.copy(
                        rating = movieRepository.getAverageRating(movieItem.id)
                    )
                }

                mutableFavoriteUIState.update {
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
