package com.example.blackbeard.screens.search.tab

import androidx.compose.runtime.mutableStateMapOf
import androidx.compose.ui.text.input.TextFieldValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.blackbeard.di.DataModule
import com.example.blackbeard.domain.RecentSearchRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class TabSearchViewModel(): ViewModel() {

    private val mutableTabSearchUIState = MutableStateFlow<TabSearchUIModel>(
        TabSearchUIModel.Empty
    )
    val tabSearchUIState: StateFlow<TabSearchUIModel> = mutableTabSearchUIState

    val selectedCategories = mutableStateMapOf<String, MutableMap<String, String>>()

    private val _recentSearches = MutableStateFlow<List<TextFieldValue>>(emptyList())
    val recentSearches: StateFlow<List<TextFieldValue>> = _recentSearches

    private val recentSearchRepository: RecentSearchRepository = DataModule.recentSearchRepository

    init {
        viewModelScope.launch {
            mutableTabSearchUIState.value = TabSearchUIModel.Initialized

            recentSearchRepository.getRecentSearches().collect { recentSearches ->
                _recentSearches.value = recentSearches.map { TextFieldValue(it) }
            }
        }
    }

    fun addRecentSearch(query: String) {
        viewModelScope.launch {
            recentSearchRepository.addRecentSearch(query)
        }
    }

    fun onCategorySelected(categoryTitle: String, key: String, value: String, isSelected: Boolean) {
        val currentItems = selectedCategories[categoryTitle]?.toMutableMap() ?: mutableMapOf()

        if (categoryTitle == "Decade") {
            if (isSelected) {
                currentItems.clear()
                currentItems[key] = value
            } else {
                currentItems.remove(key)
            }

        }
        else if (categoryTitle == "Runtime") {
            if (isSelected) {
                currentItems.clear()
                currentItems[key] = value
            } else {
                currentItems.remove(key)
            }
        } else {
            if (isSelected) {
                currentItems[key] = value
            } else {
                currentItems.remove(key)
            }
        }

        selectedCategories[categoryTitle] = currentItems
    }

    fun removeRecentSearch(query: String) {
        viewModelScope.launch {
            recentSearchRepository.removeRecentSearch(query)
        }
    }

    fun clearRecentSearches() {
        viewModelScope.launch {
            recentSearchRepository.clearRecentSearches()
        }
    }

    sealed class TabSearchUIModel {
        data object Empty : TabSearchUIModel()
        data object Loading : TabSearchUIModel()
        data object Initialized : TabSearchUIModel()
    }
}