package com.example.movieapp.models

import java.util.Date

enum class MovieCategory {
    NOW_PLAYING,
    POPULAR,
    TOP_RATED,
    UPCOMING
}

data class Movie(
    val adult: Boolean,
    val backdropPath: String?,
    val budget: Int,
    val genres: List<Genre>,
    val id: Int,
    val originalLanguage: String,
    val originalTitle: String,
    val overview: String?,
    val posterPath: String?,
    val productionCompanies: List<ProductionCompany>,
    val productionCountries: List<ProductionCountry>,
    val releaseDate: Date,
    val revenue: Long,
    val runtime: Int?,
    val spokenLanguages: List<SpokenLanguage>,
    val status: String,
    val title: String,
    val video: Boolean,
    val avgRating: Double = 0.0,
    val popularity: Double,
    val category: MovieCategory
) {

    data class ProductionCompany(
        val id: Int,
        val logoPath: String?,
        val name: String,
        val originCountry: String
    )

    data class Genre(
        val id: Int,
        val name: String
    )

    data class ProductionCountry(
        val iso31661: String,
        val name: String
    )

    data class SpokenLanguage(
        val iso6391: String,
        val name: String
    )
}