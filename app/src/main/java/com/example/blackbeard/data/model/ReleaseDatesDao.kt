package com.example.blackbeard.data.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class ReleaseDatesDao(
    @SerialName("id")
    val id: Int = 0,
    @SerialName("results")
    val results: List<ReleaseDatesResultDao> = emptyList()
)