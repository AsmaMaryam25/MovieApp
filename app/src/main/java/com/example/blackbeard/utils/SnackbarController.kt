package com.example.blackbeard.utils

import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.receiveAsFlow

// Credit: https://www.youtube.com/watch?v=KFazs62lIkE

data class SnackbarEvent(
    val message: String,
    val action: SnackbarAction? = null
)

data class SnackbarAction(
    val name: String,
    val action: suspend () -> Unit
)

object SnackbarController {

    private val _event = Channel<SnackbarEvent>()
    val events = _event.receiveAsFlow()

    suspend fun sendEvent(event: SnackbarEvent) {
        _event.send(event)
    }

}