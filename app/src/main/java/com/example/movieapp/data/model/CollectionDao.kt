package com.example.movieapp.data.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class CollectionDao(
    @SerialName("dates")
    val datesDao: DatesDao = DatesDao(),
    @SerialName("page")
    val page: Int = 0,
    @SerialName("results")
    val results: List<MovieDao> = emptyList(),
    @SerialName("total_pages")
    val totalPages: Int = 0,
    @SerialName("total_results")
    val totalResults: Int = 0
)