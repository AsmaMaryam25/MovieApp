package com.example.blackbeard.models

import kotlinx.serialization.Serializable

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
    data class AdvancedSearchScreen(val query: String, val isAdvanceSearch: Boolean) : Route("Advanced Search")

    @Serializable
    data class DetailsScreen(val name: String, val movieId: Int) : Route(name)

    @Serializable
    data class SearchContentScreen(val query: String, val isAdvanceSearch: Boolean) : Route("Search Content")
}