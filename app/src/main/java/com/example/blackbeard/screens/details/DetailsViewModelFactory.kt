package com.example.blackbeard.screens.details

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.blackbeard.utils.ConnectivityObserver

class DetailsViewModelFactory(private val movieId: Int) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(DetailsViewModel::class.java)) {
            return DetailsViewModel(movieId) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}