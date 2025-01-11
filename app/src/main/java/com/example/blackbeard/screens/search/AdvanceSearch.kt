package com.example.blackbeard.screens.search

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.example.blackbeard.models.Category

@Composable
fun AdvanceSearch() {
    val categories = listOf(
        Category("TMDb Rating", listOf("9+", "8+", "7+", "6+")),
        Category("Popular Genres", listOf("Comedy", "Horror", "Romance", "Thriller")),
        Category("Decade", listOf("2020's", "2010's", "2000's", "1990's", "1980's")),
        Category("Keywords", listOf("Anime", "B-Movie", "Cult Film", "Superhero")),
        Category("Runtime", listOf("30min or less", "1 hour or less", "1 to 2 hours"))
    )

    val selectedItems = remember { mutableSetOf<String>() }
    val textColor = if (isSystemInDarkTheme()) Color.White else Color.Black

    Column(
        modifier = Modifier.fillMaxSize()
    ) {
        Column(
            modifier = Modifier
                .weight(1f)
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 4.dp)
        ) {
            categories.forEach { category ->
                CategorySection(
                    category = category,
                    selectedItems = selectedItems,
                    onItemSelected = { item ->
                        if (selectedItems.contains(item)) {
                            selectedItems.remove(item)
                        } else {
                            selectedItems.add(item)
                        }
                    }
                )
            }
        }

        Box(
            modifier = Modifier
                .fillMaxWidth()
                .background(
                    color = Color(0xFFFFD700),
                    shape = RoundedCornerShape(10)
                )
                .clickable {}
                .padding(16.dp), // Inner padding for the text
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = "See Results" , color = Color.Black

            )
        }
    }
}

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun CategorySection(
    category: Category,
    selectedItems: Set<String>,
    onItemSelected: (String) -> Unit
) {
    Column(
        modifier = Modifier.padding(vertical = 4.dp)
    ) {
        Text(
            text = category.title,
            style = MaterialTheme.typography.titleMedium,
            modifier = Modifier.padding(bottom = 1.dp)
        )

        LazyRow(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            items(category.items.size) { index ->
                val item = category.items[index]
                CategoryItem(
                    item = item,
                    isSelected = selectedItems.contains(item),
                    onSelected = onItemSelected
                )
            }
        }
    }
}

@Composable
fun CategoryItem(
    item: String,
    isSelected: Boolean,
    onSelected: (String) -> Unit
) {
    FilterChip(
        selected = isSelected,
        onClick = { onSelected(item) },
        colors = FilterChipDefaults.filterChipColors(
            selectedContainerColor = Color(0xFFFFD700),
            selectedLabelColor = Color.Black
        ),
        label = { Text(text = item) },
        modifier = Modifier.padding(end = 8.dp)
    )
}