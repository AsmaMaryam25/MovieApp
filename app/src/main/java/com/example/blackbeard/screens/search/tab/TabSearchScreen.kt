package com.example.blackbeard.screens.search.tab

import androidx.compose.foundation.layout.Column
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.blackbeard.components.SearchBar
import com.example.blackbeard.screens.EmptyScreen
import com.example.blackbeard.screens.LoadingScreen
import com.example.blackbeard.screens.search.SearchTabs

@Composable
fun TabSearchScreen(
    modifier: Modifier,
    onCancelClicked: () -> Unit,
    onSearchClicked: (String, Boolean) -> Unit
) {
    val tabSearchViewModel = viewModel<TabSearchViewModel>()
    val tabSearchUIModel = tabSearchViewModel.tabSearchUIState.collectAsState().value
    var searchBarText by remember { mutableStateOf("") }

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
                    addToRecent = {query -> tabSearchViewModel.addRecentSearch(query)},
                    searchBarText = searchBarText,
                    onValueChanged = { searchBarText = it }
                )
                SearchTabs(
                    onClearRecentSearches = { tabSearchViewModel.clearRecentSearches() },
                    onRecentSearchClick = { query -> searchBarText = query },
                    onRemoveRecentSearch = { query -> tabSearchViewModel.removeRecentSearch(query) },
                    recentSearches = tabSearchViewModel.recentSearches.collectAsState().value,
                    onCategorySelected = { categoryTitle, key, value, isSelected ->
                        run {
                            tabSearchViewModel.onCategorySelected(
                                categoryTitle,
                                key,
                                value,
                                isSelected
                            )
                        }
                    },
                    selectedCategories = tabSearchViewModel.selectedCategories,
                    onSearch = onSearchClicked
                )
            }
        }
    }

}