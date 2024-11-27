package com.example.movieapp.models

data class Cast (
    val id: Int,
    val name: String,
    val originalName: String,
    val popularity: Double,
    val profilePath: String?,
    val character: String,
    val order: Int
)