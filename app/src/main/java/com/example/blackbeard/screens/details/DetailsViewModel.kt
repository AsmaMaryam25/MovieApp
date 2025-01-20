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
import kotlinx.coroutines.TimeoutCancellationException
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.coroutines.withTimeout
import java.net.UnknownHostException

class DetailsViewModel(private val movieId: Int) : ViewModel() {

    private val movieRepository = DataModule.movieRepository
    private val mutableDetailsUIState = MutableStateFlow<DetailsUIModel>(DetailsUIModel.Empty)
    val detailsUIState: StateFlow<DetailsUIModel> = mutableDetailsUIState
    val initialConnectivityFlow: Flow<Boolean> = isConnected

    init {
        viewModelScope.launch {
            try {
                mutableDetailsUIState.value = DetailsUIModel.Loading

                val isInitiallyConnected = withTimeout(5000L) {
                    initialConnectivityFlow.first()
                }

                if (isInitiallyConnected) {
                    getMovieDetails(movieRepository.getInstallationID())
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
            movieRepository.getMovieRating(movieId.toString(), installationID),
            movieRepository.getAgeRating(movieId),
            movieRepository.getStreamingServices(movieId)
        )

        combine(flows) { results ->
            val movie = results[0] as LocalMovie
            val credits = results[1] as Credits
            val videoLink = results[2] as String?
            val favorites = results[3] as List<MovieItem>
            val watchlist = results[4] as List<MovieItem>
            val movieRating = results[5] as Double?
            val ageRating = results[6] as AgeRating
            val streamingServices = results[7] as List<StreamingService>

            DetailsUIModel.Data(
                movie,
                credits,
                videoLink,
                favorites.any { it.id == movie.id.toString() },
                watchlist.any { it.id == movie.id.toString() },
                movieRepository.getAverageRating(movieId.toString()),
                installationID,
                movieRating,
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
            movieRepository.addRating(id, rating, installationID)
            updateAverageRating(movieRepository.getAverageRating(id))
        }
    }

    fun getVoterCount(id: String): LiveData<Int> {
        val voterCount = MutableLiveData<Int>()
        viewModelScope.launch {
            movieRepository.getVoterCount(id, voterCount)
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
            val movieRating: Double?,
            val ageRating: AgeRating,
            val streamingServices: List<StreamingService>
        ) : DetailsUIModel()
    }
}