package com.example.blackbeard.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.luminance
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.blackbeard.R
import com.example.blackbeard.di.DataModule
import kotlinx.coroutines.launch

@Composable
fun AppearanceScreen(
    showTopBar: () -> Unit,
    modifier: Modifier = Modifier
) {
    showTopBar()

    val isDarkTheme = MaterialTheme.colorScheme.background.luminance() < 0.5
    var switchIsOn = remember { mutableStateOf(isDarkTheme) }
    val coroutineScope = rememberCoroutineScope()

    Column(modifier = modifier) {
        Row(
            modifier = modifier
                .padding(30.dp),
            horizontalArrangement = Arrangement.spacedBy(LocalConfiguration.current.screenWidthDp.dp / 2),
        ) {
            Text(text = stringResource(id = R.string.dark_mode), fontSize = 25.sp, fontWeight = FontWeight.Bold)
            Switch(checked = switchIsOn.value,
                onCheckedChange = {
                    switchIsOn.value = !switchIsOn.value
                    coroutineScope.launch {
                        DataModule.movieRepository.setTheme(switchIsOn.value)
                    }
                })
        }
    }
}