package com.example.blackbeard.screens.watchlist

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.blackbeard.data.model.MovieItem
import com.example.blackbeard.di.DataModule
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class WatchlistViewModel : ViewModel() {

    private val movieRepository = DataModule.movieRepository

    private val mutableWatchlistUIState = MutableStateFlow<WatchlistUIModel>(WatchlistUIModel.Empty)
    val watchlistUIState: StateFlow<WatchlistUIModel> = mutableWatchlistUIState

    init {
        viewModelScope.launch {
            mutableWatchlistUIState.update {
                WatchlistUIModel.Loading
            }

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

    }

    sealed class WatchlistUIModel {
        data object Empty : WatchlistUIModel()
        data object Loading : WatchlistUIModel()
        data class Data(
            val watchlist: List<MovieItem>
        ) : WatchlistUIModel()
    }
}
