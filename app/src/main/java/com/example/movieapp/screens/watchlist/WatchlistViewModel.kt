package com.example.movieapp.screens.watchlist

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.movieapp.data.model.MovieItem
import com.example.movieapp.di.DataModule
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class WatchlistViewModel : ViewModel() {

    private val movieRepository = DataModule.movieRepository

    private val mutableDetailsUIState = MutableStateFlow<WatchlistUIModel>(WatchlistUIModel.Empty)
    val watchlistUIState: StateFlow<WatchlistUIModel> = mutableDetailsUIState

    init {
        viewModelScope.launch {
            mutableDetailsUIState.update {
                WatchlistUIModel.Loading
            }

            movieRepository.getWatchlist().collect { watchlist ->
                val updatedWatchlist = watchlist.map { movieItem ->
                    movieItem.copy(
                        rating = movieRepository.getAverageRating(movieItem.id)
                    )
                }

                mutableDetailsUIState.update {
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
