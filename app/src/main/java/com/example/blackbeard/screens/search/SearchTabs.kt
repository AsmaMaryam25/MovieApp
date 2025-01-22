package com.example.blackbeard.screens.search

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.NorthWest
import androidx.compose.material3.AlertDialogDefaults
import androidx.compose.material3.BasicAlertDialog
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.TabRowDefaults.tabIndicatorOffset
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.google.accompanist.pager.ExperimentalPagerApi
import com.google.accompanist.pager.HorizontalPager
import com.google.accompanist.pager.PagerState
import com.google.accompanist.pager.rememberPagerState
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json


@OptIn(ExperimentalPagerApi::class)
@Composable
fun SearchTabs(
    recentSearches: List<String>,
    onRecentSearchClick: (String) -> Unit,
    onClearRecentSearches: () -> Unit,
    onRemoveRecentSearch: (String) -> Unit,
    onCategorySelected: (String, String, String, Boolean) -> Unit,
    selectedCategories: Map<String, Map<String, String>>,
    onAdvancedSearch: (String, Boolean) -> Unit
) {
    val pagerState = rememberPagerState()
    val coroutineScope = rememberCoroutineScope()
    val keyboardController = LocalSoftwareKeyboardController.current

    Column(
        modifier = Modifier.fillMaxSize()
    ) {
        val tabs = listOf("Recent", "Advanced Search")
        TabContent(tabs, pagerState, coroutineScope, onTabSelected = { index ->
            if (index == 1) {
                keyboardController?.hide()
            } else {
                keyboardController?.show()
            }
        })

        HorizontalPager(
            count = tabs.size,
            state = pagerState,
            userScrollEnabled = false,
            modifier = Modifier
                .fillMaxSize()
                .padding(8.dp)
                .weight(1f)
        ) { page ->
            when (page) {
                0 -> {
                    RecentSearchesTab(
                        recentSearches = recentSearches,
                        onClearRecentSearches = onClearRecentSearches,
                        onRecentSearchClick = onRecentSearchClick,
                        onRemoveRecentSearch = onRemoveRecentSearch,
                    )
                }

                1 -> {
                    AdvancedSearchTab(
                        selectedCategories,
                        onCategorySelected,
                        onAdvancedSearch
                    )
                }
            }
        }
    }
}

@Composable
private fun RecentSearchesTab(
    recentSearches: List<String>,
    onClearRecentSearches: () -> Unit,
    onRecentSearchClick: (String) -> Unit,
    onRemoveRecentSearch: (String) -> Unit
) {
    if (recentSearches.isNotEmpty()) {
        val reverseOrderOfSearches = recentSearches.reversed()
        Column(modifier = Modifier.fillMaxSize()) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(8.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Recent searches\t",
                    fontWeight = FontWeight.Bold
                )
                TextButton(onClick = onClearRecentSearches) {
                    Text(text = "Clear All")
                }
            }
            LazyColumn {
                items(reverseOrderOfSearches.size) { index ->
                    val search = reverseOrderOfSearches[index]
                    Row(
                        Modifier
                            .fillMaxWidth()
                            .padding(8.dp)
                            .clickable {
                                onRecentSearchClick(search)
                                //searchContentViewModel.searchMovies(search, 1)
                            },
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = Icons.Default.Clear,
                            contentDescription = "Remove search",
                            modifier = Modifier
                                .padding(end = 8.dp)
                                .clickable { onRemoveRecentSearch(search) }
                        )
                        Text(
                            text = search,
                            modifier = Modifier.weight(1f)
                        )
                        Icon(
                            imageVector = Icons.Default.NorthWest,
                            contentDescription = "Use search",
                            modifier = Modifier
                                .padding(start = 8.dp)
                                .clickable {
                                    onRecentSearchClick(search)
                                }
                        )
                    }
                }
            }
        }
    }
}


@OptIn(ExperimentalPagerApi::class)
@Composable
fun TabContent(
    tabs: List<String>,
    pagerState: PagerState,
    coroutineScope: CoroutineScope,
    onTabSelected: (Int) -> Unit,
) {

    TabRow(
        selectedTabIndex = pagerState.currentPage,
        modifier = Modifier
            .fillMaxWidth()
            .height(48.dp)
            .padding(horizontal = 12.dp),
        contentColor = MaterialTheme.colorScheme.primary,
        indicator = { tabPositions ->
            Box(
                modifier = Modifier
                    .tabIndicatorOffset(tabPositions[pagerState.currentPage])
                    .fillMaxWidth()
                    .height(4.dp)
                    .background(
                        color = MaterialTheme.colorScheme.primary,
                        shape = RoundedCornerShape(topStartPercent = 50, topEndPercent = 50)
                    )
            )
        },
        divider = {}

    )
    {
        tabs.forEachIndexed { index, title ->
            Tab(
                text = {
                    Text(
                        text = title,
                        color = if (pagerState.currentPage == index) {
                            MaterialTheme.colorScheme.onSurface
                        } else {
                            MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
                        }
                    )
                },
                selected = pagerState.currentPage == index,
                onClick = {
                    coroutineScope.launch {
                        pagerState.animateScrollToPage(index)

                    }
                    onTabSelected(index)
                }
            )
        }
    }
}


@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun AdvancedSearchTab(
    selectedCategories: Map<String, Map<String, String>>,
    onCategorySelected: (String, String, String, Boolean) -> Unit,
    onAdvancedSearch: (String, Boolean) -> Unit
) {
    val openDialog = remember { mutableStateOf(false) }

    // Credit: https://composables.com/material3/basicalertdialog
    if(openDialog.value) {
        BasicAlertDialog(
            onDismissRequest = {
                // Dismiss the dialog when the user clicks outside the dialog or on the back
                // button. If you want to disable that functionality, simply use an empty
                // onDismissRequest.
                openDialog.value = false
            }
        ) {
            Surface(
                modifier = Modifier.wrapContentWidth().wrapContentHeight(),
                shape = MaterialTheme.shapes.large,
                tonalElevation = AlertDialogDefaults.TonalElevation
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(
                        text = "No advanced search options has been selected. Please select at least one option.",
                    )
                    Spacer(modifier = Modifier.height(24.dp))
                    TextButton(
                        onClick = { openDialog.value = false },
                        modifier = Modifier.align(Alignment.End)
                    ) {
                        Text("Confirm")
                    }
                }
            }
        }

    }

    val categories = mapOf(
        "Streaming Services" to mapOf(
            "Disney Plus" to "337",
            "Netflix" to "8",
            "Amazon Prime Video" to "119",
            "Viaplay" to "76",
            "Apple TV" to "2",
            "Google Play Movies" to "3",
            "TV 2" to "383",
            "Microsoft Store" to "68",
            "MUBI" to "11",
            "Apple TV Plus" to "350",
            "GuideDoc" to "100",
            "Netflix Kids" to "175",
            "YouTube Premium" to "188",
            "Rakuten TV" to "35",
            "Filmstriben" to "443",
            "Blockbuster" to "423",
            "SF Anytime" to "426",
            "Curiosity Stream" to "190",
            "DOCSVILLE" to "475",
            "Spamflix" to "521",
            "WOW Presents Plus" to "546",
            "Magellan TV" to "551",
            "BroadwayHD" to "554",
            "Filmzie" to "559",
            "Dekkoo" to "444",
            "True Story" to "567",
            "DocAlliance Films" to "569",
            "Hoichoi" to "315",
            "Dansk Filmskat" to "621",
            "DRTV" to "620",
            "Eventive" to "677"
        ),
        "Popular Genres" to mapOf(
            "Action" to "28",
            "Adventure" to "12",
            "Horror" to "27",
            "Romance" to "10749",
            "Comedy" to "35",
            "Crime" to "80",
            "Drama" to "18",
            "Fantasy" to "14",
            "Science Fiction" to "878",
            "Western" to "37",
            "Documentary" to "99"
        ),
        "Decade" to mapOf(
            "2020's" to "2020",
            "2010's" to "2010",
            "2000's" to "2000",
            "1990's" to "1990",
            "1980's" to "1980",
            "1970's" to "1970",
            "1960's" to "1960",
            "1950's" to "1950",
            "1940's" to "1940",
            "1930's" to "1930",
            "1920's" to "1920",
            "1910's" to "1910",
            "1900's" to "1900"
        ),
        "Runtime" to mapOf(
            "180+" to "180",
            "120+" to "120",
            "90+" to "90",
            "60+" to "60",
        ),
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
            categories.forEach { (categoryTitle, categoryItems) ->
                CategorySection(
                    categoryTitle = categoryTitle,
                    availableCategoryNames = categoryItems.keys.toList(),
                    availableCategoryValues = categoryItems.values.toList(),
                    selectedCategories = selectedCategories,
                    onCategorySelected = { key, value, isSelected ->
                        onCategorySelected(categoryTitle, key, value, isSelected)
                    }
                )
            }
        }
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .background(
                    color = MaterialTheme.colorScheme.primary,
                    shape = RoundedCornerShape(10)
                )
                .clickable {
                    if(selectedCategories.isEmpty()) {
                        openDialog.value = true
                        return@clickable
                    }
                    onAdvancedSearch(
                        Json.encodeToString(selectedCategories),
                        true
                    )
                }
                .padding(16.dp),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = "See Results", color = MaterialTheme.colorScheme.onPrimary
            )
        }
    }
}

@Composable
private fun CategorySection(
    categoryTitle: String,
    availableCategoryNames: List<String>,
    availableCategoryValues: List<String>,
    selectedCategories: Map<String, Map<String, String>>,
    onCategorySelected: (String, String, Boolean) -> Unit
) {
    Column {
        Text(
            text = categoryTitle,
            style = MaterialTheme.typography.titleMedium,
            modifier = Modifier.padding(vertical = 8.dp)
        )

        LazyRow {
            availableCategoryNames.forEachIndexed { index, categoryName ->
                val isSelected =
                    selectedCategories[categoryTitle]?.containsKey(categoryName) == true
                item {
                    CategoryItem(
                        displayItem = categoryName,
                        isSelected = isSelected,
                        onSelected = {
                            onCategorySelected(
                                categoryName,
                                availableCategoryValues[index],
                                !isSelected
                            )
                        }
                    )
                }
            }
        }
    }
}

@Composable
private fun CategoryItem(
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