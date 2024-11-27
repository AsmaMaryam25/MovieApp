package com.example.movieapp.models

data class Crew (
    val id: Int,
    val name: String,
    val popularity: Double,
    val profilePath: String?,
    val department: String,
    val job: String
)