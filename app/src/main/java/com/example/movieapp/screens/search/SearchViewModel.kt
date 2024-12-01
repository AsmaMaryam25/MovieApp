package com.example.movieapp.screens.search

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.movieapp.di.DataModule
import com.example.movieapp.models.CollectionMovie
import com.example.movieapp.models.Movie
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class SearchViewModel() : ViewModel() {

    private val movieRepository = DataModule.movieRepository
    private val mutableSearchUIState = MutableStateFlow<SearchUIModel>(SearchUIModel.Empty)
    val searchUIState: StateFlow<SearchUIModel> = mutableSearchUIState

    init {
        viewModelScope.launch {
            mutableSearchUIState.value = SearchUIModel.Loading
            movieRepository.getPopularMovies().collect { popular ->
                mutableSearchUIState.value = SearchUIModel.Data(popular)
            }
        }
    }

    fun searchMovies(query: String) {
        viewModelScope.launch {
            mutableSearchUIState.value = SearchUIModel.Loading
            movieRepository.searchMovies(query).collect { searchResults ->
                mutableSearchUIState.value = if (searchResults.isEmpty()) {
                    SearchUIModel.Empty
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