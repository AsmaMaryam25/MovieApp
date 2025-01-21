package com.example.blackbeard.models

import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.blackbeard.screens.search.SearchViewModel
import kotlinx.serialization.Serializable
import androidx.lifecycle.ViewModel

@Serializable
sealed class Route(val title: String) {

    @Serializable
    data object FavoriteScreen : Route("Favorites")

    @Serializable
    data object HomeScreen : Route("Home")

    @Serializable
    data object SearchScreen : Route("Search")

    @Serializable
    data object SettingsScreen : Route("Settings")

    @Serializable
    data object WatchlistScreen : Route("Watchlist")

    @Serializable
    data object AboutScreen : Route("About")

    @Serializable
    data object AppearanceScreen : Route("Appearance")

    @Serializable
    data class DetailsScreen(val name: String, val movieId: Int) : Route(name)

    data class SearchContentScreen(val query: String, val searchMovies: List<SearchMovie> ) : Route("Search Content")

    @Serializable
    data class AdvancedSearchScreen(val query: String) : Route("Advanced Search")
}