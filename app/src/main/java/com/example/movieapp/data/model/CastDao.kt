package com.example.movieapp.data.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class CastDao(
    @SerialName("adult")
    val adult: Boolean? = false,
    @SerialName("gender")
    val gender: Int? = 0,
    @SerialName("id")
    val id: Int? = 0,
    @SerialName("known_for_department")
    val knownForDepartment: String? = "",
    @SerialName("name")
    val name: String? = "",
    @SerialName("original_name")
    val originalName: String? = "",
    @SerialName("popularity")
    val popularity: Double? = 0.0,
    @SerialName("profile_path")
    val profilePath: String? = "",
    @SerialName("cast_id")
    val castId: Int? = 0,
    @SerialName("character")
    val character: String? = "",
    @SerialName("credit_id")
    val creditId: String? = "",
    @SerialName("order")
    val order: Int? = 0
)