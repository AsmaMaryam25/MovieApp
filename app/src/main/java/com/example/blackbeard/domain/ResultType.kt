package com.example.blackbeard.domain

// Credit: https://www.youtube.com/watch?v=MiLN2vs2Oe0

typealias RootError = Error

sealed interface Result<out D, out E: RootError> {
    data class Success<out D, out E: RootError>(val data: D): Result<D, E>
    data class Error<out D, out E: RootError>(val error: E): Result<D, E>
}