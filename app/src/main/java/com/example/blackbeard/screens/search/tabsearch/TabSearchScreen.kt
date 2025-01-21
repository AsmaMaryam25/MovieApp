package com.example.blackbeard.screens.search.tabsearch

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.blackbeard.components.SearchBar
import com.example.blackbeard.screens.APIErrorScreen
import com.example.blackbeard.screens.EmptyScreen
import com.example.blackbeard.screens.search.SearchTabs
import com.example.blackbeard.screens.search.tabSearch.TabSearchViewModel

@Composable
fun AdvancedSearchScreen(
    modifier: Modifier,
    onCancelClicked: () -> Unit
) {
    val tabSearchViewModel = viewModel<TabSearchViewModel>()
    val tabSearchUIModel = tabSearchViewModel.tabSearchUIState.collectAsState().value

    when(tabSearchUIModel) {
        TabSearchViewModel.TabSearchUIModel.ApiError -> APIErrorScreen(modifier)
        TabSearchViewModel.TabSearchUIModel.Empty -> EmptyScreen(modifier)
        TabSearchViewModel.TabSearchUIModel.Loading -> {

        }
        TabSearchViewModel.TabSearchUIModel.NoConnection -> {

        }
        TabSearchViewModel.TabSearchUIModel.NoResults -> {

        }
        is TabSearchViewModel.TabSearchUIModel.Data -> {
            Column(
                modifier = modifier
            ) {
                SearchBar(
                    isFocused = true,
                    onCancelClicked = onCancelClicked
                )
                SearchTabs(
                    onClearRecentSearches = {},
                    onRecentSearchClick = { _ -> {} },
                    onRemoveRecentSearch = { _ -> {} },
                    recentSearches = emptyList()
                )
            }
        }
    }

}