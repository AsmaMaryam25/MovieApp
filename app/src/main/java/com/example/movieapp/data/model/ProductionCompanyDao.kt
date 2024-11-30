package com.example.movieapp.data.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class ProductionCompanyDao(
    @SerialName("id")
    val id: Int = 0,

    @SerialName("logo_path")
    val logoPath: String? = null,

    @SerialName("name")
    val name: String = "",

    @SerialName("origin_country")
    val originCountry: String = ""
)