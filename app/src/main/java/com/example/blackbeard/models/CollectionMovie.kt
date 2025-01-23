package com.example.blackbeard.models

import java.time.LocalDate

data class CollectionMovie(
    val genres: List<Genre>,
    override val adult: Boolean,
    override val backdropPath: String?,
    override val id: Int,
    override val originalLanguage: String,
    override val originalTitle: String,
    override val overview: String?,
    override val posterPath: String?,
    override val releaseDate: LocalDate,
    override val title: String,
    override val video: Boolean,
    val avgRating: Double = 0.0,
    override val popularity: Double,
    val category: MovieCategory
) : Movie