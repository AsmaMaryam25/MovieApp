package com.example.movieapp.data.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable


@Serializable
data class MovieDao(
    @SerialName("adult")
    val adult: Boolean = true,

    @SerialName("backdrop_path")
    val backdropPath: String = "",

    @SerialName("belongs_to_collection")
    val belongsToCollection: CollectionDao?,

    @SerialName("budget")
    val budget: Int = 0,

    @SerialName("genres")
    val genreDaos: List<GenreDao> = emptyList(),

    @SerialName("homepage")
    val homepage: String = "",

    @SerialName("id")
    val id: Int = 0,

    @SerialName("imdb_id")
    val imdbId: String = "",

    @SerialName("original_language")
    val originalLanguage: String = "",

    @SerialName("original_title")
    val originalTitle: String = "",

    @SerialName("overview")
    val overview: String = "",

    @SerialName("popularity")
    val popularity: Double = 0.0,

    @SerialName("poster_path")
    val posterPath: String = "",

    @SerialName("production_companies")
    val productionCompanies: List<ProductionCompanyDao> = emptyList(),

    @SerialName("production_countries")
    val productionCountries: List<ProductionCountryDao> = emptyList(),

    @SerialName("release_date")
    val releaseDate: String = "",

    @SerialName("revenue")
    val revenue: Int = 0,

    @SerialName("runtime")
    val runtime: Int = 0,

    @SerialName("spoken_languages")
    val spokenLanguageDaos: List<SpokenLanguageDao> = emptyList(),

    @SerialName("status")
    val status: String = "",

    @SerialName("tagline")
    val tagline: String = "",

    @SerialName("title")
    val title: String = "",

    @SerialName("video")
    val video: Boolean = true,

    @SerialName("vote_average")
    val voteAverage: Double = 0.0,

    @SerialName("vote_count")
    val voteCount: Int = 0
)

@Serializable
data class GenreDao(
    @SerialName("id")
    val id: Int = 0,

    @SerialName("name")
    val name: String = ""
)

@Serializable
data class ProductionCompanyDao(
    @SerialName("id")
    val id: Int = 0,

    @SerialName("logo_path")
    val logoPath: String? = null,

    @SerialName("name")
    val name: String = "",

    @SerialName("origin_country")
    val originCountry: String = ""
)

@Serializable
data class ProductionCountryDao(
    @SerialName("iso_3166_1")
    val iso31661: String = "",

    @SerialName("name")
    val name: String = ""
)

@Serializable
data class SpokenLanguageDao(
    @SerialName("english_name")
    val englishName: String = "",

    @SerialName("iso_639_1")
    val iso6391: String = "",

    @SerialName("name")
    val name: String = ""
)