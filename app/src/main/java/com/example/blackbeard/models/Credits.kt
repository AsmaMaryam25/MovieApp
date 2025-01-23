package com.example.blackbeard.models

data class Credits(
    val id: Int = 0,
    val cast: List<Cast> = emptyList(),
    val crew: List<Crew> = emptyList()
)