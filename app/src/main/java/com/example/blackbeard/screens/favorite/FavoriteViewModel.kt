package com.example.blackbeard.screens.favorite

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.blackbeard.data.model.MovieItem
import com.example.blackbeard.di.DataModule
import com.example.blackbeard.utils.ConnectivityObserver.isConnected
import kotlinx.coroutines.TimeoutCancellationException
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.coroutines.withTimeout
import java.net.UnknownHostException

class FavoriteViewModel : ViewModel() {

    private val movieRepository = DataModule.movieRepository

    private val mutableFavoriteUIState = MutableStateFlow<FavoriteUIModel>(FavoriteUIModel.Empty)
    val favoriteUIState: StateFlow<FavoriteUIModel> = mutableFavoriteUIState
    val initialConnectivityFlow: Flow<Boolean> = isConnected

    init {
        viewModelScope.launch {
            mutableFavoriteUIState.update {
                FavoriteUIModel.Loading
            }

            try {
                mutableFavoriteUIState.value = FavoriteUIModel.Loading

                val isInitiallyConnected = withTimeout(5000L) {
                    initialConnectivityFlow.first()
                }

                getFavorites(isInitiallyConnected)


            } catch (e: Exception) {
                getFavorites(isInitiallyConnected = false)
            }
        }
    }

    private suspend fun getFavorites(isInitiallyConnected: Boolean) {
        movieRepository.getFavorites().collect { favorites ->
            val updatedFavorites = favorites.map { movieItem ->
                movieItem.copy(
                    rating = if (isInitiallyConnected) {
                        movieRepository.getAverageRating(movieItem.id)
                    } else {
                        -1.0
                    }
                )
            }
            mutableFavoriteUIState.update {
                FavoriteUIModel.Data(
                    favorites = updatedFavorites
                )
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
