package com.example.blackbeard.screens.details

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.blackbeard.di.DataModule
import com.example.blackbeard.models.Credits
import com.example.blackbeard.models.LocalMovie
import com.example.blackbeard.utils.ConnectivityObserver.isConnected
import com.google.firebase.installations.FirebaseInstallations
import kotlinx.coroutines.TimeoutCancellationException
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withTimeout

class DetailsViewModel(val movieId: Int) : ViewModel() {

    private val movieRepository = DataModule.movieRepository
    private val mutableDetailsUIState = MutableStateFlow<DetailsUIModel>(DetailsUIModel.Empty)
    val detailsUIState: StateFlow<DetailsUIModel> = mutableDetailsUIState
    private val firestore = movieRepository.firestore
    val initialConnectivityFlow: Flow<Boolean> = isConnected

    init {
        viewModelScope.launch {
            try {
                mutableDetailsUIState.value = DetailsUIModel.Loading

                val isInitiallyConnected = withTimeout(5000L) {
                    initialConnectivityFlow.first()
                }

                if (isInitiallyConnected) {
                    val installationID = FirebaseInstallations.getInstance().id.await()
                    getMovieDetails(installationID)
                } else {
                    mutableDetailsUIState.value = DetailsUIModel.NoConnection
                }
            } catch (e: TimeoutCancellationException) {
                mutableDetailsUIState.value = DetailsUIModel.NoConnection
            }
        }
    }

    private suspend fun getMovieDetails(installationID: String) {
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
                movieRepository.getAverageRating(movieId.toString()),
                installationID
            )
        }.collect { detailsUIModel ->
            mutableDetailsUIState.value = detailsUIModel
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

    fun addRating(id: String, rating: Double, installationID: String) {
        viewModelScope.launch {
            val ratingsRef = firestore.collection("ratings").document(id)

            val snapshot = ratingsRef.get().await()
            if (snapshot.exists()) {
                val currentData = snapshot.data
                val currentRating = currentData?.get("rating") as Double
                val currentTotalRating = currentData["totalRating"] as Double
                val currentUserRatings = currentData["userRatings"] as Map<*, *>

                var newRating: Double
                var newTotalRating: Double
                val newCurrentUserRatings = currentUserRatings.toMutableMap()
                newCurrentUserRatings[installationID] = mapOf("rating" to rating)

                if (currentUserRatings.containsKey(installationID)) {
                    newRating =
                        currentRating + rating - (currentUserRatings[installationID] as Map<*, *>)["rating"] as Double
                    newTotalRating = currentTotalRating
                } else {
                    newRating = currentRating + rating
                    newTotalRating = currentTotalRating + 1
                }
                val newAverageRating = newRating / newTotalRating


                ratingsRef.update(
                    "rating",
                    newRating,
                    "totalRating",
                    newTotalRating,
                    "averageRating",
                    newAverageRating,
                    "userRatings",
                    newCurrentUserRatings
                ).await()
                updateAverageRating(newAverageRating)
            } else {
                val initialData = mapOf(
                    "rating" to rating,
                    "totalRating" to 1.0,
                    "averageRating" to rating,
                    "userRatings" to mapOf(installationID to mapOf("rating" to rating))
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
            val averageRating: Double,
            val installationID: String
        ) : DetailsUIModel()
    }
}