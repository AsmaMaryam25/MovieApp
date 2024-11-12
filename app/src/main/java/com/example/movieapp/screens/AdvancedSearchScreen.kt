package com.example.movieapp.screens

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

@Composable
fun AdvancedSearchScreen(query: String, modifier: Modifier = Modifier) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(20.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.SpaceBetween
    ) {
        Column(
            verticalArrangement = Arrangement.spacedBy(30.dp)
        ) {
            TextField(
                value = "",
                onValueChange = {},
                label = { Text("Title") }
            )
            TextField(
                value = "",
                onValueChange = {},
                label = { Text("Year of Release") }
            )
            TextField(
                value = "",
                onValueChange = {},
                label = { Text("Director") }
            )
            TextField(
                value = "",
                onValueChange = {},
                label = { Text("Director") }
            )
            TextField(
                value = "",
                onValueChange = {},
                label = { Text("Director") }
            )
            TextField(
                value = "",
                onValueChange = {},
                label = { Text("Director") }
            )
            TextField(
                value = "",
                onValueChange = {},
                label = { Text("Director") }
            )
        }

        Row(
            horizontalArrangement = Arrangement.Center,
            modifier = Modifier.fillMaxWidth()
        ) {
            Button(
                onClick = {}
            ) {
                Text("Search")
            }
            Spacer(modifier = Modifier.width(60.dp))
            Button(
                onClick = {}
            ) {
                Text("Clear")
            }
        }
    }
}