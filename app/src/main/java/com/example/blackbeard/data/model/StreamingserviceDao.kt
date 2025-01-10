package com.example.blackbeard.data.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class StreamingDao (

    @SerialName("link")
    val link: String? = "",

    @SerialName("display_priority")
    val displayPriority: Int? = 0,

    @SerialName("logo_path")
    val logoPath: String? = "",

    @SerialName("provider_name")
    val providerName: String? = "",

    @SerialName("provider_id")
    val providerId: Int? = 0
)
