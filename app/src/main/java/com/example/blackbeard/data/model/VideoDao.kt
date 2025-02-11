package com.example.blackbeard.data.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class VideoDao(
    @SerialName("iso_639_1")
    val iso6391: String? = "",

    @SerialName("iso_3166_1")
    val iso31661: String? = "",

    @SerialName("name")
    val name: String? = "",

    @SerialName("key")
    val key: String? = "",

    @SerialName("site")
    val site: String? = "",

    @SerialName("size")
    val size: Int? = 0,

    @SerialName("type")
    val type: String? = "",

    @SerialName("official")
    val official: Boolean? = false,

    @SerialName("published_at")
    val publishedAt: String? = "",

    @SerialName("id")
    val id: String? = ""
)