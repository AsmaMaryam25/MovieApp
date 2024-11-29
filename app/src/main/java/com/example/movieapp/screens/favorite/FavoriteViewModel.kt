package com.example.movieapp.screens.favorite

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.movieapp.data.model.FavoriteMovie
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
                mutableDetailsUIState.update {
                    FavoriteUIModel.Data(
                        favorites = favorites
                    )
                }
            }
        }

    }
    sealed class FavoriteUIModel {
        data object Empty : FavoriteUIModel()
        data object Loading : FavoriteUIModel()
        data class Data(
            val favorites: List<FavoriteMovie>
        ) : FavoriteUIModel()
    }
}
