package com.example.movieapp

import kotlinx.serialization.Serializable

@kotlinx.serialization.Serializable
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

}