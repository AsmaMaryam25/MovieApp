package com.example.blackbeard.screens.search

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.GridItemSpan
import androidx.compose.foundation.lazy.grid.LazyGridState
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.rememberLazyGridState
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.NorthWest
import androidx.compose.material3.Button
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.TabRowDefaults.tabIndicatorOffset
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.painter.ColorPainter
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextRange
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import coil3.compose.AsyncImage
import com.example.blackbeard.R
import com.example.blackbeard.components.SearchBar
import com.example.blackbeard.models.Movie
import com.example.blackbeard.screens.LoadingScreen
import com.example.blackbeard.screens.NoConnectionScreen
import com.example.blackbeard.screens.home.TitleText
import com.example.blackbeard.screens.search.SearchViewModel.SearchUIModel
import com.google.accompanist.pager.ExperimentalPagerApi
import com.google.accompanist.pager.HorizontalPager
import com.google.accompanist.pager.PagerState
import com.google.accompanist.pager.rememberPagerState
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@OptIn(ExperimentalPagerApi::class)
@Composable
fun SearchScreen(
    modifier: Modifier = Modifier,
    onNavigateToDetailsScreen: (String, Int) -> Unit
) {

    val searchViewModel = viewModel<SearchViewModel>()
    val searchUIModel = searchViewModel.searchUIState.collectAsState().value
    searchViewModel.recentSearches.collectAsState().value

    val posterWidth = 170.dp
    val searchQuery = remember { mutableStateOf(TextFieldValue("")) }
    val gridState = rememberSaveable(saver = LazyGridState.Saver) { LazyGridState() }
    val isBoxClicked = remember { mutableStateOf(false) }


    when (searchUIModel) {
        SearchUIModel.Empty -> SearchContent(
            modifier,
            searchQuery,
            posterWidth,
            onNavigateToDetailsScreen,
            emptyList(),
            searchViewModel,
            gridState,
            isBoxClicked
        )

        SearchUIModel.Loading -> LoadingScreen()

        SearchUIModel.NoConnection -> NoConnectionScreen()

        is SearchUIModel.Data -> {
            SearchContent(
                modifier,
                searchQuery,
                posterWidth,
                onNavigateToDetailsScreen,
                searchUIModel.collectionMovies,
                searchViewModel,
                gridState,
                isBoxClicked
            )
        }
    }
}

@OptIn(ExperimentalLayoutApi::class, ExperimentalPagerApi::class)
@Composable
private fun SearchContent(
    modifier: Modifier,
    searchQuery: MutableState<TextFieldValue>,
    posterWidth: Dp,
    onNavigateToDetailsScreen: (String, Int) -> Unit,
    collectionMovies: List<Movie>,
    searchViewModel: SearchViewModel,
    gridState: LazyGridState = rememberLazyGridState(),
    isBoxClicked: MutableState<Boolean>
) {
    val coroutineScope = rememberCoroutineScope()
    val pagerState = rememberPagerState()
    val recentSearches = searchViewModel.recentSearches.collectAsState().value
    var isSearchQueryCleared by remember { mutableStateOf(false) }
    var isAdvancedSearchPressed by remember { mutableStateOf(false) }
    val previousSearchQuery = remember { mutableStateOf(searchQuery.value.text) }
    val popularTitle = stringResource(id = R.string.popular)
    val searchResultsTitle = stringResource(id = R.string.search_results)
    var titleText by remember { mutableStateOf(popularTitle) }

    LaunchedEffect(searchQuery.value.text) {
        if (previousSearchQuery.value.isNotEmpty() && searchQuery.value.text.isEmpty()) {
            isSearchQueryCleared = true
        } else {
            isSearchQueryCleared = false
        }
        previousSearchQuery.value = searchQuery.value.text
    }

    LaunchedEffect(isBoxClicked.value) {
        if (isBoxClicked.value) {
            isAdvancedSearchPressed = true
        } else {
            isAdvancedSearchPressed = false
        }
    }

    LaunchedEffect(searchQuery.value.text, isAdvancedSearchPressed) {
        titleText = if (searchQuery.value.text.isEmpty() && !isSearchQueryCleared && !isAdvancedSearchPressed) {
            popularTitle
        } else {
            searchResultsTitle
        }
    }

    Column(
        modifier = modifier.fillMaxSize()
    ) {
        var isSearchBarFocused by remember { mutableStateOf(false) }
        val tabs = listOf("Recent", "Advanced Search")

        SearchBar(
            searchQuery = searchQuery,
            searchViewModel = searchViewModel,
            onSearchQueryChange = { query, typeOfSearch ->
                searchViewModel.searchType.value = typeOfSearch
                if (searchViewModel.searchType.value) {
                    searchViewModel.discoverMovies(
                        searchQuery.value.text,
                        searchViewModel.currentPage.intValue,
                    )
                } else {
                    searchViewModel.searchMovies(query, 1)
                }
            },
            currentTabIndex = pagerState.currentPage,
            isSearchBarFocused = isSearchBarFocused,
            onSearchBarFocusChange = { isFocused ->
                isSearchBarFocused = isFocused
                if (isFocused) {
                    coroutineScope.launch {
                        pagerState.animateScrollToPage(0)
                    }
                }
            },
        )

        if (!isSearchBarFocused) {
            if (collectionMovies.isEmpty()) {
                Box(
                    modifier = Modifier.fillMaxSize()
                ) {
                    Text(
                        text = "No results found",
                        fontSize = 30.sp,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.padding(10.dp).align(Alignment.Center)
                    )
                }
            } else {
                LazyVerticalGrid(
                    state = gridState,
                    columns = GridCells.Adaptive(posterWidth),
                    modifier = Modifier.fillMaxSize(),
                    horizontalArrangement = Arrangement.Center,
                ) {
                    item(span = { GridItemSpan(maxLineSpan) }) {
                        Box(
                            modifier = Modifier.fillMaxWidth(),
                            contentAlignment = Alignment.Center
                        ) {
                            TitleText(text = titleText)
                        }
                    }
                    items(collectionMovies.size) { index ->
                        CreateSearchPoster(
                            searchViewModel,
                            posterWidth = posterWidth,
                            onNavigateToDetailsScreen = onNavigateToDetailsScreen,
                            movie = collectionMovies[index]
                        )
                    }

                    item(span = { GridItemSpan(2) }) {
                        if (searchViewModel.currentPage.intValue < (searchViewModel.totalPages.value
                                ?: 0)
                        ) {
                            Button(
                                onClick = {
                                    if (searchViewModel.searchType.value) {
                                        searchViewModel.discoverMovies(
                                            searchQuery.value.text,
                                            searchViewModel.currentPage.intValue + 1,
                                        )
                                    } else {
                                        searchViewModel.searchMovies(
                                            searchQuery.value.text,
                                            searchViewModel.currentPage.intValue + 1
                                        )
                                    }
                                },
                                modifier = modifier
                                    .fillMaxWidth()
                                    .padding(horizontal = 10.dp),
                            ) {
                                Text(text = "Load more")
                            }
                        }
                    }
                }
            }
        } else {
            SearchTabs(
                tabs = tabs,
                pagerState = pagerState,
                coroutineScope = coroutineScope,
                recentSearches = recentSearches,
                onRecentSearchClick = {
                    searchQuery.value = TextFieldValue(it, TextRange(it.length))
                },
                onClearRecentSearches = { searchViewModel.clearRecentSearches() },
                onRemoveRecentSearch = { searchViewModel.removeRecentSearch(it) },
                searchQuery = searchQuery,
                searchViewModel = searchViewModel,
                isBoxClicked = isBoxClicked
            )
        }
    }
}

@OptIn(ExperimentalPagerApi::class)
@Composable
private fun SearchTabs(
    tabs: List<String>,
    pagerState: PagerState,
    coroutineScope: CoroutineScope,
    recentSearches: List<String>,
    onRecentSearchClick: (String) -> Unit,
    onClearRecentSearches: () -> Unit,
    onRemoveRecentSearch: (String) -> Unit,
    searchQuery: MutableState<TextFieldValue>,
    searchViewModel: SearchViewModel,
    isBoxClicked: MutableState<Boolean>
) {
    val keyboardController = LocalSoftwareKeyboardController.current

    Column(
        modifier = Modifier.fillMaxSize()
    ) {
        TabContent(tabs, pagerState, coroutineScope, onTabSelected = { index ->
            if (index == 1) {
                keyboardController?.hide()
            } else {
                keyboardController?.show()
                searchViewModel.searchType.value = false
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
                                                searchViewModel.searchMovies(search, 1)
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
                                                    searchQuery.value =
                                                        TextFieldValue(
                                                            search,
                                                            TextRange(search.length)
                                                        )
                                                }
                                        )
                                    }
                                }
                            }
                        }
                    }
                }

                1 -> {
                    AdvancedSearch(
                        searchQuery,
                        searchViewModel,
                        updateSearchType = {
                            searchViewModel.searchType.value = true
                        },
                        isBoxClicked = isBoxClicked
                    )
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

@Composable
private fun CreateSearchPoster(
    searchViewModel: SearchViewModel,
    posterWidth: Dp,
    modifier: Modifier = Modifier,
    onNavigateToDetailsScreen: (String, Int) -> Unit,
    movie: Movie
) {
    var isClickAble by remember { mutableStateOf(true) }
    val coroutineScope = rememberCoroutineScope()
    Column(
        modifier = modifier.padding(10.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Box(
            modifier = Modifier
                .width(posterWidth)
                .aspectRatio(2 / 3f)
                .clip(shape = RoundedCornerShape(30.dp))
                .background(Color.Gray)
        ) {
            AsyncImage(
                model = movie.posterPath,
                contentDescription = null,
                modifier = Modifier
                    .width(posterWidth)
                    .aspectRatio(2 / 3f)
                    .clip(RoundedCornerShape(30.dp))
                    .clickable(enabled = isClickAble) {
                        if (isClickAble) {
                            onNavigateToDetailsScreen(
                                movie.title,
                                movie.id
                            )
                            isClickAble = false
                            coroutineScope.launch {
                                delay(1000)
                                isClickAble = true
                            }
                        }
                    },
                placeholder = ColorPainter(Color.Gray)
            )
        }
        Text(
            modifier = modifier
                .width(posterWidth)
                .clickable {
                    onNavigateToDetailsScreen(
                        movie.title,
                        movie.id
                    )
                },
            text = movie.title,
            textAlign = TextAlign.Center,
            fontWeight = FontWeight.Bold,
            lineHeight = 15.sp
        )
    }
}

@Composable
fun AdvancedSearch(
    searchQuery: MutableState<TextFieldValue>,
    searchViewModel: SearchViewModel,
    updateSearchType: () -> Unit,
    isBoxClicked: MutableState<Boolean>
) {
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
        )
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
                    availableCategoryNames = categoryItems.keys.toList(),  // Get list of category keys (display names)
                    availableCategoryValues = categoryItems.values.toList(), // Get list of category values (actual values)
                    selectedCategories = searchViewModel.selectedCategories,
                    onCategorySelected = { key, value, isSelected ->
                        searchViewModel.onCategorySelected(categoryTitle, key, value, isSelected)
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
                    isBoxClicked.value = true
                    updateSearchType()
                    searchViewModel.discoverMovies(
                        searchQuery.value.text,
                        1,
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

@Composable
fun CategorySection(
    categoryTitle: String,
    availableCategoryNames: List<String>, // List of items for that category
    availableCategoryValues: List<String>, // List of values for that category
    selectedCategories: Map<String, Map<String, String>>, // Track selected items
    onCategorySelected: (String, String, Boolean) -> Unit // Callback for item selection
) {
    Column {
        Text(
            text = categoryTitle,
            style = MaterialTheme.typography.titleMedium,
            modifier = Modifier.padding(vertical = 8.dp)
        )

        LazyRow {
            // Display all items in the current category
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