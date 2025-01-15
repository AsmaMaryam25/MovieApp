package com.example.blackbeard.screens.search

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp

@Composable
fun InterimSearchScreen(
    onNavigateToRecentSearches: () -> Unit,
    onNavigateToAdvancedSearch: () -> Unit,
    onCancelSearch: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.Center
    ) {
        Button(
            onClick = onNavigateToRecentSearches,
            modifier = Modifier.padding(bottom = 16.dp)
        ) {
            Text(text = "Recent Searches")
        }
        Button(
            onClick = onNavigateToAdvancedSearch,
            modifier = Modifier.padding(bottom = 16.dp)
        ) {
            Text(text = "Advanced Search")
        }
        Button(
            onClick = onCancelSearch
        ) {
            Text(text = "Cancel")
        }
    }
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.Red) // so it's obvious
    ) {
        Text("INTERIM SCREEN", color = Color.White)
    }
}

