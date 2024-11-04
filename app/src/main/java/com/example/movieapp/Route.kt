package com.example.movieapp

import kotlinx.serialization.Serializable

@kotlinx.serialization.Serializable
sealed class Route(val title: String) {

    @Serializable
    data object FavoriteScreen : Route("FavoriteScreen")

    @Serializable
    data object HomeScreen : Route("HomeScreen")

    @Serializable
    data object SearchScreen : Route("SearchScreen")

    @Serializable
    data object SettingsScreen : Route("SettingsScreen")

    @Serializable
    data object WatchlistScreen : Route("WatchlistScreen")

}