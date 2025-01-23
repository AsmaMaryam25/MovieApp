package com.example.blackbeard.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeContentPadding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.luminance
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.blackbeard.R
import com.example.blackbeard.components.onDebounceClick
import com.example.blackbeard.di.DataModule
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AppearanceScreen(
    modifier: Modifier = Modifier.safeContentPadding(),
    popBackStack: () -> Unit
) {

    val isDarkTheme = MaterialTheme.colorScheme.background.luminance() < 0.5
    var switchIsOn = remember { mutableStateOf(isDarkTheme) }
    val coroutineScope = rememberCoroutineScope()


    Scaffold(
        modifier = modifier,
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = "Appearance",
                        style = MaterialTheme.typography.titleLarge.copy(
                            fontWeight = FontWeight.Bold
                        ),
                        modifier = Modifier.fillMaxWidth(),
                        textAlign = TextAlign.Center
                    )
                },
                navigationIcon = {
                    IconButton(onClick = onDebounceClick {
                            popBackStack()
                    }) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Arrow back"
                        )
                    }
                },
                // To center the title an empty IconButton was created
                actions = {
                    IconButton(onClick = {}) {}
                }

            )
        },
    ) {
        Column(modifier = Modifier.padding(it)) {
            Row(
                modifier = modifier
                    .padding(30.dp),
                horizontalArrangement = Arrangement.spacedBy(LocalConfiguration.current.screenWidthDp.dp / 2),
            ) {
                Text(
                    text = stringResource(id = R.string.dark_mode),
                    fontSize = 25.sp,
                    fontWeight = FontWeight.Bold
                )
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
}