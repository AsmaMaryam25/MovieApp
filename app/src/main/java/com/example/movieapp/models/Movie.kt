package com.example.movieapp.models

import java.time.LocalDate

interface Movie {
    val id: Int
    val title: String
    val overview: String?
    val posterPath: String?
    val backdropPath: String?
    val releaseDate: LocalDate
    val adult: Boolean
    val originalLanguage: String
    val originalTitle: String
    val popularity: Double
    val video: Boolean
}