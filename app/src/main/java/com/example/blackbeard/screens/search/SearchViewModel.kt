package com.example.blackbeard.screens.search

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.blackbeard.di.DataModule
import com.example.blackbeard.models.CollectionMovie
import com.example.blackbeard.models.Movie
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class SearchViewModel() : ViewModel() {

    private val movieRepository = DataModule.movieRepository
    private val mutableSearchUIState = MutableStateFlow<SearchUIModel>(SearchUIModel.Empty)
    val searchUIState: StateFlow<SearchUIModel> = mutableSearchUIState
    var popularMovies: List<CollectionMovie> = emptyList()

    init {
        viewModelScope.launch {
            loadPopularMovies()
        }
    }

    private suspend fun loadPopularMovies() {
        mutableSearchUIState.value = SearchUIModel.Loading
        movieRepository.getPopularMovies().collect { popular ->
            popularMovies = popular
            mutableSearchUIState.value = SearchUIModel.Data(popular)
        }
    }

    fun searchMovies(query: String) {
        viewModelScope.launch {
            mutableSearchUIState.value = SearchUIModel.Loading
            movieRepository.searchMovies(query).collect { searchResults ->
                mutableSearchUIState.value = if (searchResults.isEmpty()) {
                    SearchUIModel.Data(popularMovies)
                } else {
                    SearchUIModel.Data(searchResults)
                }
            }
        }
    }

    sealed class SearchUIModel {
        data object Empty : SearchUIModel()
        data object Loading : SearchUIModel()
        data class Data(
            val collectionMovies: List<Movie>
        ) : SearchUIModel()
    }
}