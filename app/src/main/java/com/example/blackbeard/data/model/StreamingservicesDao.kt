package com.example.blackbeard.data.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class StreamingservicesDao (

    @SerialName("results")
    val results: List<StreamingDao>? = emptyList()

)
