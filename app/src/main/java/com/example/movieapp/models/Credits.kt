package com.example.movieapp.models

data class Credits(
    val id: Int,
    val cast: List<Cast>,
    val crew: List<Crew>
)