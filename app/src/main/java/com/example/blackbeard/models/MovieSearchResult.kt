package com.example.blackbeard.models

data class MovieSearchResult(
    val movies: List<SearchMovie>,
    val totalPages: Int?
)
