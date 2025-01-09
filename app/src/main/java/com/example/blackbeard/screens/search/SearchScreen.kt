package com.example.blackbeard.screens.search

import android.util.Log
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyGridState
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.rememberLazyGridState
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.TabRowDefaults.tabIndicatorOffset
import androidx.compose.material3.Text
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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import coil3.compose.AsyncImage
import com.example.blackbeard.components.SearchBar
import com.example.blackbeard.models.Category
import com.example.blackbeard.models.Movie
import com.example.blackbeard.screens.LoadingScreen
import com.example.blackbeard.screens.NoConnectionScreen
import com.example.blackbeard.screens.search.SearchViewModel.SearchUIModel
import com.google.accompanist.pager.ExperimentalPagerApi
import com.google.accompanist.pager.HorizontalPager
import com.google.accompanist.pager.PagerState
import com.google.accompanist.pager.rememberPagerState
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch

@Composable
fun SearchScreen(
    modifier: Modifier = Modifier,
    onNavigateToAdvancedSearchScreen: (String) -> Unit,
    onNavigateToDetailsScreen: (String, Int) -> Unit
) {

    val searchViewModel = viewModel<SearchViewModel>()
    val searchUIModel = searchViewModel.searchUIState.collectAsState().value

    val posterWidth = 170.dp
    val searchQuery = remember { mutableStateOf("") }
    val popularState = rememberSaveable(saver = LazyGridState.Saver) { LazyGridState() }

    when (searchUIModel) {
        SearchUIModel.Empty -> SearchContent(
            modifier,
            searchQuery,
            posterWidth,
            onNavigateToDetailsScreen,
            emptyList(),
            searchViewModel
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
                popularState
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
    searchViewModel: SearchViewModel,
    popularState: LazyGridState = rememberLazyGridState()
) {
    Column(
        modifier = modifier.fillMaxSize()
    ) {
        var isSearchBarFocused by remember { mutableStateOf(false) }
        val tabs = listOf("Recent", "Advance Search")
        val coroutineScope = rememberCoroutineScope()
        val pagerState = rememberPagerState()

        SearchBar(
            searchQuery = searchQuery,
            onSearchQueryChange = { query ->
                searchViewModel.searchMovies(query, 1)
            },
            isSearchBarFocused = isSearchBarFocused,
            onSearchBarFocusChange = { isFocused ->
                isSearchBarFocused = isFocused
            },
        )

        if (!isSearchBarFocused) {
            if (collectionMovies.isEmpty()) {
                Text(
                    text = "No results found",
                    modifier = Modifier.padding(10.dp)
                )
            } else {
                LazyVerticalGrid(
                    state = popularState,
                    columns = GridCells.Adaptive(posterWidth),
                    modifier = Modifier.fillMaxSize(),
                    horizontalArrangement = Arrangement.Center,
                ) {
                    items(collectionMovies.size) { index ->
                        CreateSearchPoster(
                            posterWidth = posterWidth,
                            onNavigateToDetailsScreen = onNavigateToDetailsScreen,
                            movie = collectionMovies[index]
                        )
                    }
                }
            }
        } else {
            SearchTabs(
                tabs = tabs,
                pagerState = pagerState,
                coroutineScope = coroutineScope
            )
        }
    }
}

@OptIn(ExperimentalPagerApi::class)
@Composable
private fun SearchTabs(
    tabs: List<String>,
    pagerState: PagerState,
    coroutineScope: CoroutineScope
) {
    Column(
        modifier = Modifier.fillMaxSize()
    ) {
        TabContent(tabs, pagerState, coroutineScope)

        HorizontalPager(
            count = tabs.size,
            state = pagerState,
            modifier = Modifier
                .fillMaxSize()
                .padding(8.dp)
                .weight(1f)
        ) { page ->
            when (page) {
                0 -> {
                    Text("Feature in development", modifier = Modifier.fillMaxSize())
                }

                1 -> {
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
    coroutineScope: CoroutineScope
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
    ) {
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
                }
            )
        }
    }
}

@Composable
private fun CreateSearchPoster(
    posterWidth: Dp,
    modifier: Modifier = Modifier,
    onNavigateToDetailsScreen: (String, Int) -> Unit,
    movie: Movie
) {
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
                    .clip(shape = RoundedCornerShape(30.dp))
                    .clickable(onClick = { onNavigateToDetailsScreen(movie.title, movie.id) }),
                placeholder = ColorPainter(Color.Gray)
            )
        }
        Text(
            modifier = modifier
                .width(posterWidth)
                .clickable { onNavigateToDetailsScreen(movie.title, movie.id) },
            text = movie.title,
            textAlign = TextAlign.Center,
            fontWeight = FontWeight.Bold,
            lineHeight = 15.sp
        )
    }
}