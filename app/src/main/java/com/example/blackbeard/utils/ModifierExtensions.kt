package com.example.blackbeard.utils

import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.pointer.pointerInput

@Composable
fun Modifier.noDoubleClick(
    timeInterval: Long = 500L,
    onClick: () -> Unit
): Modifier {
    var lastClickTime by remember { mutableStateOf(0L) }
    return pointerInput(Unit) {
        detectTapGestures {
            val currentTime = System.currentTimeMillis()
            if (currentTime - lastClickTime >= timeInterval) {
                lastClickTime = currentTime
                onClick()
            }
        }
    }
}
