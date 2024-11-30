package com.example.movieapp.screens.details

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider

class DetailsViewModelFactory(private val movieId: Int) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(DetailsViewModel::class.java)) {
            return DetailsViewModel(movieId) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}