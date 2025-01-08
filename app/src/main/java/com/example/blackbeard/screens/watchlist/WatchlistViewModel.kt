package com.example.blackbeard.screens.watchlist

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

class WatchlistViewModel : ViewModel() {

    private val movieRepository = DataModule.movieRepository

    private val mutableWatchlistUIState = MutableStateFlow<WatchlistUIModel>(WatchlistUIModel.Empty)
    val watchlistUIState: StateFlow<WatchlistUIModel> = mutableWatchlistUIState
    val initialConnectivityFlow: Flow<Boolean> = isConnected

    init {
        viewModelScope.launch {
            mutableWatchlistUIState.update {
                WatchlistUIModel.Loading
            }

            try {
                mutableWatchlistUIState.value = WatchlistUIModel.Loading

                val isInitiallyConnected = withTimeout(5000L) {
                    initialConnectivityFlow.first()
                }

                if (isInitiallyConnected) {
                    getWatchlist()
                } else {
                    mutableWatchlistUIState.value = WatchlistUIModel.NoConnection
                }


            } catch (e: TimeoutCancellationException) {
                mutableWatchlistUIState.value = WatchlistUIModel.NoConnection
            }
        }

    }

    private suspend fun getWatchlist() {
        movieRepository.getWatchlist().collect { watchlist ->
            val updatedWatchlist = watchlist.map { movieItem ->
                movieItem.copy(
                    rating = movieRepository.getAverageRating(movieItem.id)
                )
            }

            mutableWatchlistUIState.update {
                WatchlistUIModel.Data(
                    watchlist = updatedWatchlist
                )
            }
        }
    }

    sealed class WatchlistUIModel {
        data object Empty : WatchlistUIModel()
        data object Loading : WatchlistUIModel()
        data object NoConnection : WatchlistUIModel()
        data class Data(
            val watchlist: List<MovieItem>
        ) : WatchlistUIModel()
    }
}
