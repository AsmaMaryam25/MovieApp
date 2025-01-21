package com.example.blackbeard.screens.search.tab

import androidx.compose.foundation.layout.Column
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.blackbeard.components.SearchBar
import com.example.blackbeard.screens.EmptyScreen
import com.example.blackbeard.screens.LoadingScreen
import com.example.blackbeard.screens.search.SearchTabs

@Composable
fun AdvancedSearchScreen(
    modifier: Modifier,
    onCancelClicked: () -> Unit,
    onSearchClicked: (String, Boolean) -> Unit
) {
    val tabSearchViewModel = viewModel<TabSearchViewModel>()
    val tabSearchUIModel = tabSearchViewModel.tabSearchUIState.collectAsState().value

    when(tabSearchUIModel) {
        TabSearchViewModel.TabSearchUIModel.Empty -> EmptyScreen(modifier)
        TabSearchViewModel.TabSearchUIModel.Loading -> LoadingScreen(modifier)
        is TabSearchViewModel.TabSearchUIModel.Initialized -> {
            Column(
                modifier = modifier
            ) {
                SearchBar(
                    isFocused = true,
                    onCancelClicked = onCancelClicked,
                    onSearchClicked = onSearchClicked,
                    addToRecent = {query -> tabSearchViewModel.addRecentSearch(query)}
                )
                SearchTabs(
                    onClearRecentSearches = { tabSearchViewModel.clearRecentSearches() },
                    onRecentSearchClick = { query -> onSearchClicked(query, false) },
                    onRemoveRecentSearch = { query -> tabSearchViewModel.removeRecentSearch(query) },
                    recentSearches = tabSearchViewModel.recentSearches.collectAsState().value,
                    onCategorySelected = { categoryTitle, key, value, isSelected -> {tabSearchViewModel.onCategorySelected(categoryTitle, key, value, isSelected)}},
                    selectedCategories = tabSearchViewModel.selectedCategories
                )
            }
        }
    }

}