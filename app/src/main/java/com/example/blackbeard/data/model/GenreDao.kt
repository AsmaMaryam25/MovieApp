package com.example.blackbeard.data.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class GenreDao(
    @SerialName("id")
    val id: Int? = 0,

    @SerialName("name")
    val name: String? = ""
)