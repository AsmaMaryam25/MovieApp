package com.example.blackbeard.data.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class ReleaseDateDao(
    @SerialName("certification")
    val certification: String = "",
    @SerialName("descriptors")
    val descriptors: List<String> = emptyList(),
    @SerialName("iso_639_1")
    val iso6391: String = "",
    @SerialName("note")
    val note: String = "",
    @SerialName("release_date")
    val releaseDate: String = "",
    @SerialName("type")
    val type: Int = 0
)