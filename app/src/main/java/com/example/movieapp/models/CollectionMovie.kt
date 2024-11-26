package com.example.movieapp.models

import java.time.LocalDate

data class CollectionMovie(
    val genres: List<Genre>,
    val adult: Boolean,
    val backdropPath: String?,
    val id: Int,
    val originalLanguage: String,
    val originalTitle: String,
    val overview: String?,
    val posterPath: String?,
    val releaseDate: LocalDate,
    val title: String,
    val video: Boolean,
    val avgRating: Double = 0.0,
    val popularity: Double,
    val category: MovieCategory
)