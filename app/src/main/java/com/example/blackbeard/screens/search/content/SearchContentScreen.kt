package com.example.blackbeard.screens.search.content

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.GridItemSpan
import androidx.compose.foundation.lazy.grid.LazyGridState
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.rememberLazyGridState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
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
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import coil3.compose.AsyncImage
import com.example.blackbeard.components.SearchBar
import com.example.blackbeard.models.SearchMovie
import com.example.blackbeard.screens.APIErrorScreen
import com.example.blackbeard.screens.EmptyScreen
import com.example.blackbeard.screens.LoadingScreen
import com.example.blackbeard.screens.NoConnectionScreen
import com.example.blackbeard.screens.home.TitleText
import com.example.blackbeard.screens.search.content.SearchContentViewModel.SearchContentUIModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch


@Composable
fun SearchContentScreen(
    modifier: Modifier,
    onMoviePosterClicked: (String, Int) -> Unit,
    onSearchBarFocus: () -> Unit,
    query: String,
    isAdvancedSearch: Boolean,
    onBackButtonClicked: () -> Unit
) {
    val posterWidth = 170.dp
    val searchContentViewModel: SearchContentViewModel = viewModel(
        factory = viewModelFactory {
            initializer {
                SearchContentViewModel(query, isAdvancedSearch)
            }
        }
    )
    val searchContentUIModel = searchContentViewModel.searchContentUIState.collectAsState().value

    val gridState = rememberLazyGridState()

    when (searchContentUIModel) {
        SearchContentUIModel.Empty -> {
            EmptyScreen(modifier)
        }

        SearchContentUIModel.ApiError -> {
            APIErrorScreen(modifier)
        }

        SearchContentUIModel.Loading -> {
            LoadingScreen(modifier)
        }
        SearchContentUIModel.NoConnection -> {
            NoConnectionScreen(modifier)
        }
        SearchContentUIModel.NoResults -> {
            // No results
        }
        is SearchContentUIModel.Data -> {
            Content(
                modifier,
                onSearchBarFocus,
                searchContentUIModel.searchMovies,
                gridState,
                posterWidth,
                onMoviePosterClicked,
                searchContentViewModel,
                query,
                isAdvancedSearch,
                onBackButtonClicked = onBackButtonClicked
            )
        }
    }
}

@Composable
private fun Content(
    modifier: Modifier,
    onSearchBarFocus: () -> Unit,
    searchMovies: List<SearchMovie>,
    gridState: LazyGridState,
    posterWidth: Dp,
    onMoviePosterClicked: (String, Int) -> Unit,
    searchContentViewModel: SearchContentViewModel,
    query: String,
    isAdvancedSearch: Boolean,
    onBackButtonClicked: () -> Unit
) {
    var isClickAble by remember { mutableStateOf(true) }
    val coroutineScope = rememberCoroutineScope()

    Column(
        modifier = modifier.fillMaxSize()
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(
                modifier = Modifier.padding(start = 4.dp, top = 4.dp, bottom = 4.dp),
                onClick = {
                    onBackButtonClicked()
                    isClickAble = false
                    coroutineScope.launch {
                        delay(1000)
                        isClickAble = true
                    }
                },
            ) {
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                    contentDescription = "Back button"
                )
            }
            SearchBar(
                modifier = Modifier
                    .padding(vertical = 16.dp)
                    .padding(end = 8.dp),
                isFocused = false,
                onSearchBarFocus = onSearchBarFocus,
                searchBarText = if(!isAdvancedSearch) {query} else {""},
            )
        }
        if (searchMovies.isEmpty()) {
            Box(
                modifier = Modifier.fillMaxSize()
            ) {
                Text(
                    text = "No results found",
                    fontSize = 30.sp,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier
                        .padding(10.dp)
                        .align(Alignment.Center)
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
                        TitleText(text = "Search Results")
                    }
                }
                items(searchMovies.size) { index ->
                    CreateSearchPoster(
                        posterWidth = posterWidth,
                        onNavigateToDetailsScreen = onMoviePosterClicked,
                        movie = searchMovies[index]
                    )
                }

                item(span = { GridItemSpan(2) }) {
                    if (searchContentViewModel.currentPage.intValue < (searchContentViewModel.totalPages.value
                            ?: 0)
                    ) {
                        Button(
                            onClick = {
                                if(isAdvancedSearch) {
                                    searchContentViewModel.discoverMovies(searchContentViewModel.selectedCategories, searchContentViewModel.currentPage.intValue + 1, query)
                                } else {
                                    searchContentViewModel.searchMovies(query, searchContentViewModel.currentPage.intValue + 1)
                                }
                            },
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 10.dp),
                        ) {
                            Text(text = "Load more")
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun CreateSearchPoster(
    posterWidth: Dp,
    modifier: Modifier = Modifier,
    onNavigateToDetailsScreen: (String, Int) -> Unit,
    movie: SearchMovie
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