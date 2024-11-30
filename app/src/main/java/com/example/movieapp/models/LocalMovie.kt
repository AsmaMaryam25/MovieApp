package com.example.movieapp.models

import java.time.LocalDate

data class LocalMovie(
    override val id: Int,
    override val title: String,
    override val overview: String?,
    override val posterPath: String?,
    override val backdropPath: String?,
    override val releaseDate: LocalDate,
    override val adult: Boolean,
    override val originalLanguage: String,
    override val originalTitle: String,
    override val popularity: Double,
    override val video: Boolean,
    val budget: Int,
    val genres: List<Genre>,
    val productionCompanies: List<ProductionCompany>,
    val productionCountries: List<ProductionCountry>,
    val revenue: Int,
    val runtime: Int?,
    val spokenLanguages: List<SpokenLanguage>,
    val status: String,
    val avgRating: Double = 0.0,
    val category: MovieCategory
) : Movie