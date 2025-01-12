package com.example.blackbeard.data.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class StreamingServicesDao(
    @SerialName("id")
    val id: Int? = 0,
    @SerialName("results")
    val results: Map<String, CountryDao>? = emptyMap()
)