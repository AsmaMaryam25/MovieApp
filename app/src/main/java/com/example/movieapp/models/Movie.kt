package com.example.movieapp.models

import java.time.LocalDate

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
    val releaseDate: LocalDate,
    val revenue: Int,
    val runtime: Int?,
    val spokenLanguages: List<SpokenLanguage>,
    val status: String,
    val title: String,
    val video: Boolean,
    val avgRating: Double = 0.0,
    val popularity: Double,
    val category: MovieCategory
)