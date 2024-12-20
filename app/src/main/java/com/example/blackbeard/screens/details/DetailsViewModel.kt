package com.example.blackbeard.screens.details

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.blackbeard.di.DataModule
import com.example.blackbeard.models.Credits
import com.example.blackbeard.models.LocalMovie
import com.example.blackbeard.utils.ConnectivityObserver
import com.example.blackbeard.utils.ConnectivityObserver.isConnected
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await

class DetailsViewModel(val movieId: Int) : ViewModel() {

    private val movieRepository = DataModule.movieRepository
    private val mutableDetailsUIState = MutableStateFlow<DetailsUIModel>(DetailsUIModel.Empty)
    val detailsUIState: StateFlow<DetailsUIModel> = mutableDetailsUIState
    private val firestore = movieRepository.firestore
    val connectivityFlow: Flow<Boolean> = isConnected.stateIn(
        viewModelScope,
        SharingStarted.WhileSubscribed(5000L),
        false
    )

    init {
        viewModelScope.launch {
            mutableDetailsUIState.value = DetailsUIModel.Loading

            connectivityFlow.collect{
                if(it){
                    combine(
                        movieRepository.getMovie(movieId),
                        movieRepository.getCredits(movieId),
                        movieRepository.getVideoLink(movieId),
                        movieRepository.getFavorites(),
                        movieRepository.getWatchlist(),
                    ) { movie, credits, videoLink, favorites, watchlist ->
                        DetailsUIModel.Data(
                            movie,
                            credits,
                            videoLink,
                            favorites.any { it.id == movie.id.toString() },
                            watchlist.any { it.id == movie.id.toString() },
                            movieRepository.getAverageRating(movieId.toString())
                        )
                    }.collect { detailsUIModel ->
                        mutableDetailsUIState.value = detailsUIModel
                    }
                } else {
                    mutableDetailsUIState.value = DetailsUIModel.NoConnection
                }
            }
        }
    }

    fun toggleFavorite(localMovie: LocalMovie) {
        viewModelScope.launch {
            movieRepository.toggleFavorite(
                localMovie.id.toString(),
                localMovie.title,
                localMovie.posterPath,
                localMovie.avgRating
            )
        }
    }

    fun toggleWatchlist(localMovie: LocalMovie) {
        viewModelScope.launch {
            movieRepository.toggleWatchlist(
                localMovie.id.toString(),
                localMovie.title,
                localMovie.posterPath,
                localMovie.avgRating
            )
        }
    }

    fun addRating(id: String, rating: Double) {
        viewModelScope.launch {
            val ratingsRef = firestore.collection("ratings").document(id)

            val snapshot = ratingsRef.get().await()
            if (snapshot.exists()) {
                val currentData = snapshot.data
                val currentRating = currentData?.get("rating") as Double
                val currentTotalRating = currentData["totalRating"] as Double

                val newRating = currentRating + rating
                val newTotalRating = currentTotalRating + 1
                val newAverageRating = newRating / newTotalRating

                ratingsRef.update(
                    "rating",
                    newRating,
                    "totalRating",
                    newTotalRating,
                    "averageRating",
                    newAverageRating
                ).await()
                updateAverageRating(newAverageRating)
            } else {
                val initialData = mapOf(
                    "rating" to rating,
                    "totalRating" to 1.0,
                    "averageRating" to rating
                )
                ratingsRef.set(initialData).await()
                updateAverageRating(rating)
            }
        }
    }

    private fun updateAverageRating(newAverageRating: Double) {
        mutableDetailsUIState.update { currentState ->
            if (currentState is DetailsUIModel.Data) {
                currentState.copy(averageRating = newAverageRating)
            } else {
                currentState
            }
        }
    }

    sealed class DetailsUIModel {
        data object Empty : DetailsUIModel()
        data object Loading : DetailsUIModel()
        data object NoConnection : DetailsUIModel()
        data class Data(
            val localMovie: LocalMovie,
            val credits: Credits,
            val videoLink: String? = null,
            val isFavorite: Boolean,
            val isWatchlist: Boolean,
            val averageRating: Double
        ) : DetailsUIModel()
    }
}