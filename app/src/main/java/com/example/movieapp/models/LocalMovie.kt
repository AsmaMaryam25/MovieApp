package com.example.movieapp.models

import java.time.LocalDate

data class LocalMovie(
    override val id: Int = 0,
    override val title: String = "",
    override val overview: String? = null,
    override val posterPath: String? = null,
    override val backdropPath: String? = null,
    override val releaseDate: LocalDate = LocalDate.MIN,
    override val adult: Boolean = false,
    override val originalLanguage: String = "",
    override val originalTitle: String = "",
    override val popularity: Double = 0.0,
    override val video: Boolean = false,
    val budget: Int = 0,
    val genres: List<Genre> = emptyList(),
    val productionCompanies: List<ProductionCompany> = emptyList(),
    val productionCountries: List<ProductionCountry> = emptyList(),
    val revenue: Int = 0,
    val runtime: Int? = null,
    val spokenLanguages: List<SpokenLanguage> = emptyList(),
    val status: String = "",
    val avgRating: Double = 0.0,
    val category: MovieCategory = MovieCategory.NOW_PLAYING
) : Movie