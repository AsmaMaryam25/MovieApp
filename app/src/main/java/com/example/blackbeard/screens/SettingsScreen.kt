package com.example.blackbeard.screens

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Info
import androidx.compose.material.icons.outlined.Visibility
import androidx.compose.material3.Card
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.text.font.FontWeight.Companion.Bold
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun SettingsScreen(
    onNavigateToAboutScreen: () -> Unit,
    onNavigateToAppearanceScreen: () -> Unit,
    modifier: Modifier = Modifier
) {
    LazyColumn {
        item {
            Card(
                modifier = modifier.size(
                    width = LocalConfiguration.current.screenWidthDp.dp,
                    height = LocalConfiguration.current.screenHeightDp.dp / 8
                ),
                border = BorderStroke(
                    width = 1.dp,
                    color = MaterialTheme.colorScheme.background
                ),
                shape = RectangleShape,
                onClick = onNavigateToAppearanceScreen
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(10.dp),
                    modifier = modifier
                        .padding(10.dp)
                ) {
                    Icon(
                        imageVector = Icons.Outlined.Visibility,
                        contentDescription = "Visibility",
                        modifier = Modifier.size(70.dp)
                    )
                    Column {
                        Text(
                            text = "Appearance",
                            fontSize = 20.sp,
                            fontWeight = Bold,
                        )
                        Text(
                            text = "Customize the look and feel of your app experience",
                            fontSize = 15.sp,
                        )
                    }
                }
            }
        }

        item {
            Card(
                onClick = onNavigateToAboutScreen,
                modifier = modifier.size(
                    width = LocalConfiguration.current.screenWidthDp.dp,
                    height = LocalConfiguration.current.screenHeightDp.dp / 8
                ),
                border = BorderStroke(
                    width = 1.dp,
                    color = MaterialTheme.colorScheme.background
                ),
                shape = RectangleShape
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(10.dp),
                    modifier = modifier
                        .padding(10.dp)
                ) {
                    Icon(
                        imageVector = Icons.Outlined.Info,
                        contentDescription = "Info",
                        modifier = Modifier.size(70.dp)
                    )
                    Column {
                        Text(
                            text = "About",
                            fontSize = 20.sp,
                            fontWeight = Bold,
                        )
                        Text(
                            text = "Customize the look and feel of your app experience",
                            fontSize = 15.sp,
                        )
                    }
                }
            }
        }
    }
}

