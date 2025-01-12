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

                if (isInitiallyConnected) {
                    getFavorites()
                } else {
                    mutableFavoriteUIState.value = FavoriteUIModel.NoConnection
                }


            } catch (e: TimeoutCancellationException) {
                mutableFavoriteUIState.value = FavoriteUIModel.NoConnection
            } catch (e: UnknownHostException) {
                mutableFavoriteUIState.value = FavoriteUIModel.NoConnection
            }
        }
    }

    private suspend fun getFavorites() {
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

    sealed class FavoriteUIModel {
        data object Empty : FavoriteUIModel()
        data object Loading : FavoriteUIModel()
        data object NoConnection : FavoriteUIModel()
        data class Data(
            val favorites: List<MovieItem>
        ) : FavoriteUIModel()
    }
}
