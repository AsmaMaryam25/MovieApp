package com.example.blackbeard.data.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class CreditsDao(
    @SerialName("id")
    val id: Int? = 0,

    @SerialName("cast")
    val cast: List<CastDao>? = emptyList(),

    @SerialName("crew")
    val crew: List<CrewDao>? = emptyList()
)