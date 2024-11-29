package com.example.movieapp.data.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class FavoriteMovie (
    @SerialName("id")
    val id: String,

    @SerialName("title")
    val title: String,

    @SerialName("posterPath")
    val posterPath: String,

    @SerialName("rating")
    val rating: Double
)