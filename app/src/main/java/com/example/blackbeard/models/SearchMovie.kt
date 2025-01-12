package com.example.blackbeard.models

import java.time.LocalDate

data class SearchMovie(
    override val id: Int,
    override val title: String,
    override val overview: String?,
    override val posterPath: String?,
    val genres: List<Int>? = emptyList(),
    override val backdropPath: String?,
    override val releaseDate: LocalDate,
    override val adult: Boolean,
    override val originalLanguage: String,
    override val originalTitle: String,
    override val popularity: Double,
    override val video: Boolean,
    val voteAverage: Double,
) : Movie