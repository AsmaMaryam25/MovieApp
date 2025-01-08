package com.example.blackbeard.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.runtime.*

@Composable
fun AdvancedSearchScreen(query: String, modifier: Modifier = Modifier, showTopBar: () -> Unit) {
    showTopBar()

    var title by remember { mutableStateOf("") }
    var yearOfRelease by remember { mutableStateOf("") }
    var director by remember { mutableStateOf("") }
    var cast by remember { mutableStateOf("") }
    var genre by remember { mutableStateOf("") }

    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(20.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.SpaceBetween
    ) {
        Column(
            verticalArrangement = Arrangement.spacedBy(70.dp)
        ) {
            TextField(
                value = title,
                onValueChange = { title = it },
                label = { Text("Title") }
            )
            TextField(
                value = yearOfRelease,
                onValueChange = { yearOfRelease = it },
                label = { Text("Year of Release") }
            )
            TextField(
                value = director,
                onValueChange = { director = it },
                label = { Text("Director") }
            )
            TextField(
                value = cast,
                onValueChange = { cast = it },
                label = { Text("Cast") }
            )
            TextField(
                value = genre,
                onValueChange = { genre = it },
                label = { Text("Genre") }
            )
        }

        Row(
            horizontalArrangement = Arrangement.Center,
            modifier = Modifier.fillMaxWidth()
        ) {
            Button(
                onClick = {},
                modifier = Modifier.width(95.dp)
            ) {
                Text("Search")
            }
            Spacer(modifier = Modifier.width(60.dp))
            Button(
                onClick = {
                    title = ""
                    yearOfRelease = ""
                    director = ""
                    cast = ""
                    genre = ""
                },
                modifier = Modifier.width(95.dp)
            ) {
                Text("Clear")
            }
        }
    }
}