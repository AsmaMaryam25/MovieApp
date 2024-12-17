package com.example.movieapp.data.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class CrewDao(
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

    @SerialName("credit_id")
    val creditId: String? = "",

    @SerialName("department")
    val department: String? = "",

    @SerialName("job")
    val job: String? = ""
)