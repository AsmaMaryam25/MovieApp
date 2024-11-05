package com.example.movieapp

import kotlinx.serialization.Serializable

@kotlinx.serialization.Serializable
sealed class Route(val title: String) {

    @Serializable
    data object FavoriteScreen : Route("Favorite Screen")

    @Serializable
    data object HomeScreen : Route("Home Screen")

    @Serializable
    data object SearchScreen : Route("Search Screen")

    @Serializable
    data object SettingsScreen : Route("Settings Screen")

    @Serializable
    data object WatchlistScreen : Route("Watchlist Screen")

    @Serializable
    data object AboutScreen : Route("About Screen")

}