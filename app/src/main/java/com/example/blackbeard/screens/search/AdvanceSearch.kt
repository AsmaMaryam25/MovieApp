package com.example.blackbeard.screens.search

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
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

    Column(
        modifier = Modifier
            .fillMaxSize()
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