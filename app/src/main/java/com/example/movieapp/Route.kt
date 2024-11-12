package com.example.movieapp

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
    data class DetailsScreen(val movieId: String) : Route("Details")

}