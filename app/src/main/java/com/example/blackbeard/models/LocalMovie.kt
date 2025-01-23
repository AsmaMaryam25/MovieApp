package com.example.blackbeard.models

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
    val budget: Long = 0,
    val genres: List<Genre> = emptyList(),
    val productionCompanies: List<ProductionCompany> = emptyList(),
    val productionCountries: List<ProductionCountry> = emptyList(),
    val revenue: Long = 0,
    val runtime: Int? = null,
    val spokenLanguages: List<SpokenLanguage> = emptyList(),
    val status: String = "",
    val avgRating: Double = 0.0,
    val category: MovieCategory = MovieCategory.NOW_PLAYING,
    val voteAverage: Double = 0.0,
    val voteCount: Int = 0
) : Movie

fun isReleaseDateInvalid(releaseDate: LocalDate) = releaseDate == LocalDate.MIN
fun isBudgetInvalid(budget: Long) = budget <= 0
fun isProductionCompaniesInvalid(productionCompanies: List<ProductionCompany>) =
    productionCompanies.isEmpty()

fun isProductionCountriesInvalid(productionCountries: List<ProductionCountry>) =
    productionCountries.isEmpty()

fun isRevenueInvalid(revenue: Long) = revenue <= 0
fun isRuntimeInvalid(runtime: Int?) = runtime == null
fun isSpokenLanguagesInvalid(spokenLanguages: List<SpokenLanguage>) = spokenLanguages.isEmpty()

fun isDetailsInvalid(
    releaseDate: LocalDate,
    budget: Long,
    productionCompanies: List<ProductionCompany>,
    productionCountries: List<ProductionCountry>,
    revenue: Long,
    runtime: Int?,
    spokenLanguages: List<SpokenLanguage>
): Boolean {
    return isReleaseDateInvalid(releaseDate) &&
            isBudgetInvalid(budget) &&
            isProductionCompaniesInvalid(productionCompanies) &&
            isProductionCountriesInvalid(productionCountries) &&
            isRevenueInvalid(revenue) &&
            isRuntimeInvalid(runtime) &&
            isSpokenLanguagesInvalid(spokenLanguages)
}