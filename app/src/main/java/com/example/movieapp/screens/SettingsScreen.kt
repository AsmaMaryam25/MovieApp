package com.example.movieapp.screens
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material3.Card
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.text.font.FontWeight.Companion.Bold
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.material.icons.outlined.Info
import androidx.compose.material.icons.outlined.Visibility

@Composable
fun SettingsScreen() {
    LazyColumn {
        item {
            Card (
                modifier = Modifier.size(
                    width = LocalConfiguration.current.screenWidthDp.dp,
                    height = LocalConfiguration.current.screenHeightDp.dp/8
                ),
                border = BorderStroke(
                    width = 1.dp,
                    color = Color(0xffc6c6c6)
                ),
                shape = RectangleShape
            ) {
                Row (
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.fillMaxSize().padding(10.dp)
                ) {
                    Image(
                        imageVector = Icons.Default.AccountCircle,
                        contentDescription = "Account Circle",
                        modifier = Modifier.size(70.dp)
                    )
                    Text(
                        text = "UltraYugiohFan123",
                        fontSize = 20.sp,
                        fontWeight = Bold,
                        modifier = Modifier.padding(10.dp)
                    )
                }
            }
        }

        item {
            Card (
                modifier = Modifier.size(
                    width = LocalConfiguration.current.screenWidthDp.dp,
                    height = LocalConfiguration.current.screenHeightDp.dp/8
                ),
                border = BorderStroke(
                    width = 1.dp,
                    color = Color(0xffc6c6c6)
                ),
                shape = RectangleShape
            ) {
                Row (
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(10.dp),
                    modifier = Modifier.fillMaxSize().padding(10.dp)
                ) {
                    Image(
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
            Card (
                modifier = Modifier.size(
                    width = LocalConfiguration.current.screenWidthDp.dp,
                    height = LocalConfiguration.current.screenHeightDp.dp/8
                ),
                border = BorderStroke(
                    width = 1.dp,
                    color = Color(0xffc6c6c6)
                ),
                shape = RectangleShape
            ) {
                Row (
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(10.dp),
                    modifier = Modifier.fillMaxSize().padding(10.dp)
                ) {
                    Image(
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

