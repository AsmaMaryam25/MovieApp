package com.example.blackbeard.screens.search.content

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.aspectRatio
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
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.painter.ColorPainter
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import coil3.compose.AsyncImage
import com.example.blackbeard.components.SearchBar
import com.example.blackbeard.components.onDebounceClick
import com.example.blackbeard.models.SearchMovie
import com.example.blackbeard.screens.APIErrorScreen
import com.example.blackbeard.screens.EmptyScreen
import com.example.blackbeard.screens.LoadingScreen
import com.example.blackbeard.screens.NoConnectionScreen
import com.example.blackbeard.screens.home.TitleText
import com.example.blackbeard.screens.search.content.SearchContentViewModel.SearchContentUIModel


@Composable
fun SearchContentScreen(
    modifier: Modifier,
    onMoviePosterClicked: (String, Int) -> Unit,
    onSearchBarFocus: (String, Boolean) -> Unit,
    query: TextFieldValue,
    isAdvancedSearch: Boolean,
    onBackButtonClicked: (String, Boolean) -> Unit
) {
    val posterWidth = 170.dp
    val searchContentViewModel: SearchContentViewModel = viewModel(
        factory = viewModelFactory {
            initializer {
                SearchContentViewModel(query.text, isAdvancedSearch)
            }
        }
    )
    val searchContentUIModel = searchContentViewModel.searchContentUIState.collectAsState().value
    val gridState = rememberLazyGridState()

    Column(
        modifier = modifier.fillMaxSize()
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(
                modifier = Modifier
                    .padding(start = 4.dp, top = 4.dp, bottom = 4.dp),
                onClick = onDebounceClick {
                    onBackButtonClicked(query.text, isAdvancedSearch)
                }
            ) {
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                    contentDescription = "Back button"
                )
            }
            if (searchContentUIModel is SearchContentUIModel.Data ||
                searchContentUIModel is SearchContentUIModel.NoResults) {
                SearchBar(
                    modifier = Modifier
                        .padding(vertical = 16.dp)
                        .padding(end = 8.dp),
                    isFocused = false,
                    onSearchBarFocus = { onSearchBarFocus(it, isAdvancedSearch) } ,
                    searchBarText = if(!isAdvancedSearch) { TextFieldValue(query.text) } else {  TextFieldValue("")  },
                )
            }
        }
        Box(
            contentAlignment = Alignment.Center
        ) {
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
                    Content(
                        modifier,
                        emptyList(),
                        gridState,
                        posterWidth,
                        onMoviePosterClicked,
                        searchContentViewModel,
                        query,
                        isAdvancedSearch
                    )
                }

                is SearchContentUIModel.Data -> {
                    Content(
                        modifier,
                        searchContentUIModel.searchMovies,
                        gridState,
                        posterWidth,
                        onMoviePosterClicked,
                        searchContentViewModel,
                        query,
                        isAdvancedSearch
                    )
                }
            }
        }
    }
}

@Composable
private fun Content(
    modifier: Modifier,
    searchMovies: List<SearchMovie>,
    gridState: LazyGridState,
    posterWidth: Dp,
    onMoviePosterClicked: (String, Int) -> Unit,
    searchContentViewModel: SearchContentViewModel,
    query: TextFieldValue,
    isAdvancedSearch: Boolean
) {
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
                                searchContentViewModel.discoverMovies(searchContentViewModel.selectedCategories, searchContentViewModel.currentPage.intValue + 1)
                            } else {
                                searchContentViewModel.searchMovies(query.text, searchContentViewModel.currentPage.intValue + 1)
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

@Composable
private fun CreateSearchPoster(
    posterWidth: Dp,
    modifier: Modifier = Modifier,
    onNavigateToDetailsScreen: (String, Int) -> Unit,
    movie: SearchMovie
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
                    .clip(RoundedCornerShape(30.dp))
                    .clickable(onClick = onDebounceClick {
                        onNavigateToDetailsScreen(
                            movie.title,
                            movie.id
                        )
                    }),
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