package com.example.blackbeard.models

data class StreamingService (
    val link: String,
    override val providerId: Int,
    override val logoPath: String,
    override val providerName: String,
    override val displayPriority: Int
): Streaming
