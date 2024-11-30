package com.example.movieapp.data.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class DatesDao(
    @SerialName("maximum")
    val maximum: String = "",
    @SerialName("minimum")
    val minimum: String = ""
)