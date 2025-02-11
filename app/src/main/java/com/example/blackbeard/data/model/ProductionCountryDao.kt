package com.example.blackbeard.data.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class ProductionCountryDao(
    @SerialName("iso_3166_1")
    val iso31661: String? = "",

    @SerialName("name")
    val name: String? = ""
)