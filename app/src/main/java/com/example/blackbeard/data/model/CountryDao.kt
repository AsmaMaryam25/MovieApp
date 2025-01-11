package com.example.blackbeard.data.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class CountryDao(
    @SerialName("link")
    val link: String? = "",
    @SerialName("flatrate")
    val flatrate: List<ProviderDao>? = emptyList(),
    @SerialName("rent")
    val rent: List<ProviderDao>? = emptyList(),
    @SerialName("buy")
    val buy: List<ProviderDao>? = emptyList()
)