package com.example.blackbeard.screens.search

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
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
import androidx.compose.runtime.MutableState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.example.blackbeard.models.Category

@Composable
fun AdvanceSearch(
    searchQuery: MutableState<String>,
    searchViewModel: SearchViewModel,
    updateSearchType: () -> Unit,
) {
    val categories = listOf(
        Category(
            "Rating",
            listOf("9+", "8+", "7+", "6+"),
            listOf("9", "8", "7", "6")
        ),
        Category(
            "Popular Genres",
            listOf(
                "Action",
                "Adventure",
                "Horror",
                "Romance",
                "Comedy",
                "Crime",
                "Drama",
                "Fantasy",
                "Science Fiction",
                "Western",
                "Documentary"
            ),
            listOf("28", "12", "27", "10749", "35", "80", "18", "14", "878", "37", "99")
        ),
        Category(
            "Year",
            listOf("2025", "2024", "2023", "2022", "2021", "2020", "2019", "2018", "2017", "2016", "2015", "2014", "2013", "2012", "2011", "2010", "2009", "2008", "2007", "2006", "2005", "2004", "2003", "2002", "2001", "2000", "1999", "1998", "1997", "1996", "1995", "1994", "1993", "1992", "1991", "1990", "1989", "1988", "1987", "1986", "1985", "1984", "1983", "1982", "1981", "1980"),
            listOf("2025", "2024", "2023", "2022", "2021", "2020", "2019", "2018", "2017", "2016", "2015", "2014", "2013", "2012", "2011", "2010", "2009", "2008", "2007", "2006", "2005", "2004", "2003", "2002", "2001", "2000", "1999", "1998", "1997", "1996", "1995", "1994", "1993", "1992", "1991", "1990", "1989", "1988", "1987", "1986", "1985", "1984", "1983", "1982", "1981", "1980")
        ),
        /*Category(
            "Runtime",
            listOf("30min or less", "1 hour or less", "1 to 2 hours"),
            listOf("30", "60", "120")
        )*/
    )

    Column(
        modifier = Modifier.fillMaxSize()
    ) {
        Column(
            modifier = Modifier
                .weight(1f)
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 4.dp)
        ) {
            categories.forEachIndexed { index, category ->
                CategorySection(
                    index = index,
                    category = category,
                    selectedItems = searchViewModel.selectedItems,
                    onItemSelected = { categoryIndex, item ->
                        val currentSelected =
                            searchViewModel.selectedItems[categoryIndex] ?: emptyList()
                        val updatedSelected = if (category.title == "Rating") {
                            if (currentSelected.contains(item)) {
                                emptyList()
                            } else {
                                listOf(item)
                            }
                        } else {
                            if (currentSelected.contains(item)) {
                                currentSelected - item
                            } else {
                                currentSelected + item
                            }
                        }
                        searchViewModel.selectedItems[categoryIndex] = updatedSelected
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
                .clickable {
                    updateSearchType()
                    searchViewModel.advanceSearchMovies(
                        searchQuery.value,
                        1,
                        searchViewModel.selectedItems.toMap()
                    )
                }
                .padding(16.dp),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = "See Results", color = Color.Black

            )
        }
    }
}

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun CategorySection(
    index: Int,
    category: Category,
    selectedItems: Map<Int, List<String>>,
    onItemSelected: (Int, String) -> Unit
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
            items(category.items.size) { itemIndex ->
                val displayItem = category.displayItems[itemIndex]
                val item = category.items[itemIndex]
                CategoryItem(
                    displayItem = displayItem,
                    isSelected = selectedItems[index]?.contains(item) == true,
                    onSelected = { onItemSelected(index, item) }
                )
            }
        }
    }
}

@Composable
fun CategoryItem(
    displayItem: String,
    isSelected: Boolean,
    onSelected: () -> Unit
) {
    FilterChip(
        selected = isSelected,
        onClick = { onSelected() },
        colors = FilterChipDefaults.filterChipColors(
            selectedContainerColor = MaterialTheme.colorScheme.primary,
            selectedLabelColor = MaterialTheme.colorScheme.onPrimary,
            containerColor = MaterialTheme.colorScheme.surfaceVariant,
            labelColor = MaterialTheme.colorScheme.onSurfaceVariant,
        ),
        label = { Text(text = displayItem) },
        modifier = Modifier.padding(end = 8.dp)
    )
}