package com.example.movieapp.data.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class VideosDao(
    @SerialName("id")
    val id: Int = 0,
    @SerialName("results")
    val results: List<VideoDao> = emptyList()
)