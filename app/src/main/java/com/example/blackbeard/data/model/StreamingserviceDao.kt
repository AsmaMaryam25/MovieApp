package com.example.blackbeard.data.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class StreamingDao (
    @SerialName("id")
    val id: Int? = 0,

    @SerialName("name")
    val name: String? = ""
)
