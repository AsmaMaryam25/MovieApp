package com.example.blackbeard.data.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class ReleaseDatesResultDao(
    @SerialName("iso_3166_1")
    val iso31661: String = "",
    @SerialName("release_dates")
    val releaseDates: List<ReleaseDateDao> = emptyList()
)