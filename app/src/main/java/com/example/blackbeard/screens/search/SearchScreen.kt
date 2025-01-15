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
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.GridItemSpan
import androidx.compose.foundation.lazy.grid.LazyGridState
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.rememberLazyGridState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.NorthWest
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.TabRowDefaults.tabIndicatorOffset
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
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

import androidx.compose.ui.res.stringResource

import androidx.compose.ui.platform.LocalSoftwareKeyboardController

import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import coil3.compose.AsyncImage
import com.example.blackbeard.R
import com.example.blackbeard.components.SearchBar
import com.example.blackbeard.di.DataModule
import com.example.blackbeard.models.Movie
import com.example.blackbeard.models.SearchMovie
import com.example.blackbeard.screens.LoadingScreen
import com.example.blackbeard.screens.NoConnectionScreen
import com.example.blackbeard.screens.home.TitleText
import com.example.blackbeard.screens.search.SearchViewModel.SearchUIModel
import com.google.accompanist.pager.ExperimentalPagerApi
import com.google.accompanist.pager.HorizontalPager
import com.google.accompanist.pager.PagerState
import com.google.accompanist.pager.rememberPagerState
import kotlinx.coroutines.CoroutineScope
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
    val searchQuery = remember { mutableStateOf("") }
    val gridState = rememberSaveable(saver = LazyGridState.Saver) { LazyGridState() }

    when (searchUIModel) {
        SearchUIModel.Empty -> SearchContent(
            modifier,
            searchQuery,
            posterWidth,
            onNavigateToDetailsScreen,
            emptyList(),
            emptyList(),
            searchViewModel,
            gridState
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
                emptyList(),
                searchViewModel,
                gridState
            )
        }

        is SearchUIModel.AdvanceSearchData -> {
            SearchContent(
                modifier,
                searchQuery,
                posterWidth,
                onNavigateToDetailsScreen,
                emptyList(),
                searchUIModel.collectionMovies,
                searchViewModel,
                gridState
            )
        }
    }
}

@OptIn(ExperimentalLayoutApi::class, ExperimentalPagerApi::class)
@Composable
private fun SearchContent(
    modifier: Modifier,
    searchQuery: MutableState<String>,
    posterWidth: Dp,
    onNavigateToDetailsScreen: (String, Int) -> Unit,
    collectionMovies: List<Movie>,
    collectionSearchMovies: List<SearchMovie>,
    searchViewModel: SearchViewModel,
    gridState: LazyGridState = rememberLazyGridState(),
) {
    rememberCoroutineScope()
    listOf("Recent", "Advanced Search")
    rememberPagerState()
    val recentSearches = searchViewModel.recentSearches.collectAsState().value

    Column(
        modifier = modifier.fillMaxSize()
    ) {
        var isSearchBarFocused by remember { mutableStateOf(false) }
        val tabs = listOf("Recent", "Advanced Search")
        val coroutineScope = rememberCoroutineScope()
        val pagerState = rememberPagerState()

        // Determine which list to use based on searchType
        val currentMovies: List<Any> = if (searchViewModel.searchType.value) {
            collectionSearchMovies
        } else {
            collectionMovies
        }

        SearchBar(
            searchQuery = searchQuery,
            searchViewModel = searchViewModel,
            onSearchQueryChange = { query, typeOfSearch ->
                searchViewModel.searchType.value = typeOfSearch
                if (searchViewModel.searchType.value) {
                    searchViewModel.advanceSearchMovies(
                        query,
                        1,
                        searchViewModel.selectedItems.toMap()
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
            if (currentMovies.isEmpty()) {
                Text(
                    text = "No results found",
                    modifier = Modifier.padding(10.dp)
                )
            } else {
                Box(
                    modifier = Modifier.fillMaxWidth(),
                    contentAlignment = Alignment.Center
                ) {
                    TitleText(text = stringResource(id = R.string.popular))
                }
                LazyVerticalGrid(
                    state = gridState,
                    columns = GridCells.Adaptive(posterWidth),
                    modifier = Modifier.fillMaxSize(),
                    horizontalArrangement = Arrangement.Center,
                ) {
                    items(currentMovies.size) { index ->
                        CreateSearchPoster(
                            searchViewModel,
                            posterWidth = posterWidth,
                            onNavigateToDetailsScreen = onNavigateToDetailsScreen,
                            movie = currentMovies[index]
                        )
                    }

                    item(span = { GridItemSpan(2) }) {
                        if (searchViewModel.currentPage.intValue < (searchViewModel.totalPages.value
                                ?: 0)
                        ) {
                            Button(
                                onClick = {
                                    if (searchViewModel.searchType.value) {
                                        searchViewModel.advanceSearchMovies(
                                            searchQuery.value,
                                            searchViewModel.currentPage.intValue + 1,
                                            searchViewModel.selectedItems.toMap()
                                        )
                                    } else {
                                        searchViewModel.searchMovies(
                                            searchQuery.value,
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
                onRecentSearchClick = { searchQuery.value = it },
                onClearRecentSearches = { searchViewModel.clearRecentSearches() },
                onRemoveRecentSearch = { searchViewModel.removeRecentSearch(it) },
                searchQuery = searchQuery,
                searchViewModel = searchViewModel,
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
    searchQuery: MutableState<String>,
    searchViewModel: SearchViewModel,
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
                searchViewModel.selectedItems.clear()
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
                        Column(modifier = Modifier.fillMaxSize()) {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(8.dp),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    text = "\t"
                                )
                                TextButton(onClick = onClearRecentSearches) {
                                    Text(text = "Clear All")
                                }
                            }
                            recentSearches.forEach { search ->
                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(8.dp)
                                        .clickable { onRecentSearchClick(search)
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
                                            }
                                    )
                                }
                            }
                        }
                    }
                }

                1 -> {
                    AdvanceSearch(
                        searchQuery,
                        searchViewModel,
                        updateSearchType = {
                            searchViewModel.searchType.value = true
                        }
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
    onTabSelected: (Int) -> Unit
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
                        color = Color(0xFFFFD700),
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
    movie: Any
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
                model = if (searchViewModel.searchType.value) (movie as SearchMovie).posterPath else (movie as Movie).posterPath,
                contentDescription = null,
                modifier = Modifier
                    .width(posterWidth)
                    .aspectRatio(2 / 3f)
                    .clip(RoundedCornerShape(30.dp))
                    .clickable(enabled = isClickAble) {
                        if (isClickAble) {
                            onNavigateToDetailsScreen(
                                if (searchViewModel.searchType.value) (movie as SearchMovie).title else (movie as Movie).title,
                                if (searchViewModel.searchType.value) (movie as SearchMovie).id else (movie as Movie).id
                            )
                            isClickAble = false
                            coroutineScope.launch {
                                kotlinx.coroutines.delay(1000)
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
                        if (searchViewModel.searchType.value) (movie as SearchMovie).title else (movie as Movie).title,
                        if (searchViewModel.searchType.value) (movie as SearchMovie).id else (movie as Movie).id
                    )
                },
            text = if (searchViewModel.searchType.value) (movie as SearchMovie).title else (movie as Movie).title,
            textAlign = TextAlign.Center,
            fontWeight = FontWeight.Bold,
            lineHeight = 15.sp
        )
    }
}