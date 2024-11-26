package com.example.movieapp.data.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable


@Serializable
data class CollectionMovieDao(
    @SerialName("id")
    val id: Int = 0,
    @SerialName("title")
    val title: String = "",
    @SerialName("overview")
    val overview: String = "",
    @SerialName("poster_path")
    val posterPath: String = "",
    @SerialName("backdrop_path")
    val backdropPath: String = "",
    @SerialName("release_date")
    val releaseDate: String = "",
    @SerialName("vote_average")
    val voteAverage: Float = 0.0f,
    @SerialName("vote_count")
    val voteCount: Int = 0,
    @SerialName("popularity")
    val popularity: Float = 0.0f,
    @SerialName("genre_ids")
    val genreIds: List<Int> = emptyList(),
    @SerialName("original_language")
    val originalLanguage: String = "",
    @SerialName("original_title")
    val originalTitle: String = "",
    @SerialName("adult")
    val adult: Boolean = false,
    @SerialName("video")
    val video: Boolean = false
)