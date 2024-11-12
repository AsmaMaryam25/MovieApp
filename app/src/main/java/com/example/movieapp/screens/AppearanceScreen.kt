package com.example.movieapp.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun AppearanceScreen(
    showTopBar: () -> Unit,
    toggleDarkTheme: () -> Unit,
    modifier: Modifier = Modifier
) {
    showTopBar()

    var switchIsOn = remember { mutableStateOf(false) }

    Column(modifier = modifier) {
        Row(
            modifier = modifier
                .padding(30.dp),
            horizontalArrangement = Arrangement.spacedBy(LocalConfiguration.current.screenWidthDp.dp / 2),
        ) {
            Text(text = "Dark theme", fontSize = 25.sp, fontWeight = FontWeight.Bold)
            Switch(checked = switchIsOn.value,
                onCheckedChange = {
                    switchIsOn.value = !switchIsOn.value
                    toggleDarkTheme()
                })
        }
    }
}