package com.example.blackbeard.screens.details

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.blackbeard.data.model.MovieItem
import com.example.blackbeard.di.DataModule
import com.example.blackbeard.models.AgeRating
import com.example.blackbeard.models.Credits
import com.example.blackbeard.models.LocalMovie
import com.example.blackbeard.models.StreamingService
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
import java.net.UnknownHostException

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
                    getMovieDetails(FirebaseInstallations.getInstance().id.await())
                } else {
                    mutableDetailsUIState.value = DetailsUIModel.NoConnection
                }
            } catch (e: TimeoutCancellationException) {
                mutableDetailsUIState.value = DetailsUIModel.NoConnection

            } catch (e: UnknownHostException) {
                mutableDetailsUIState.value = DetailsUIModel.NoConnection
            }
        }
    }

    private suspend fun getMovieDetails(installationID: String) {
        val flows = listOf(
            movieRepository.getMovie(movieId),
            movieRepository.getCredits(movieId),
            movieRepository.getVideoLink(movieId),
            movieRepository.getFavorites(),
            movieRepository.getWatchlist(),
            movieRepository.getAgeRating(movieId),
            movieRepository.getStreamingServices(movieId)
        )

        combine(flows) { results ->
            val movie = results[0] as LocalMovie
            val credits = results[1] as Credits
            val videoLink = results[2] as String?
            val favorites = results[3] as List<MovieItem>
            val watchlist = results[4] as List<MovieItem>
            val ageRating = results[5] as AgeRating
            val streamingServices = results[6] as List<StreamingService>

            DetailsUIModel.Data(
                movie,
                credits,
                videoLink,
                favorites.any { it.id == movie.id.toString() },
                watchlist.any { it.id == movie.id.toString() },
                movieRepository.getAverageRating(movieId.toString()),
                installationID,
                ageRating,
                streamingServices
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

    fun getVoterCount(id: String): LiveData<Int> {
        val voterCount = MutableLiveData<Int>()
        viewModelScope.launch {
            val ratingsRef = firestore.collection("ratings").document(id)

            val snapshot = ratingsRef.get().await()
            if (snapshot.exists()) {
                val currentData = snapshot.data
                val currentUserRatings =
                    currentData?.get("userRatings") as? Map<*, *> ?: emptyMap<Any, Any>()
                voterCount.postValue(currentUserRatings.size)
            }
        }
        return voterCount
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
            val installationID: String,
            val ageRating: AgeRating,
            val streamingServices: List<StreamingService>
        ) : DetailsUIModel()
    }
}