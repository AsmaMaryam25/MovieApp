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
import java.net.UnknownHostException

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

                getWatchlist(isInitiallyConnected)


            } catch (e: TimeoutCancellationException) {
                getWatchlist(isInitiallyConnected = false)
            } catch (e: UnknownHostException) {
                getWatchlist(isInitiallyConnected = false)
            }
        }

    }

    private suspend fun getWatchlist(isInitiallyConnected: Boolean) {
        movieRepository.getWatchlist().collect { watchlist ->
            val updatedWatchlist = watchlist.map { movieItem ->
                movieItem.copy(
                    rating = if (isInitiallyConnected) {
                        movieRepository.getAverageRating(movieItem.id)
                    } else {
                        69.0
                    }
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
        data class Data(
            val watchlist: List<MovieItem>
        ) : WatchlistUIModel()
    }
}
