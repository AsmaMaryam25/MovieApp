package com.example.blackbeard.screens.search

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
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
import androidx.compose.material3.Button
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
import com.example.blackbeard.models.Movie
import com.example.blackbeard.screens.LoadingScreen
import com.example.blackbeard.screens.NoConnectionScreen
import com.example.blackbeard.screens.search.SearchViewModel.SearchUIModel
import kotlinx.coroutines.launch

@Composable
fun SearchScreen(
    modifier: Modifier = Modifier,
    onNavigateToAdvancedSearchScreen: (String) -> Unit,
    onNavigateToInterimSearchScreen: () -> Unit,
    onNavigateToDetailsScreen: (String, Int) -> Unit
) {

    val searchViewModel = viewModel<SearchViewModel>()
    val searchUIModel = searchViewModel.searchUIState.collectAsState().value

    val posterWidth = 170.dp
    val searchQuery = remember { mutableStateOf("") }
    val gridState = rememberSaveable(saver = LazyGridState.Saver) { LazyGridState() }

    when (searchUIModel) {
        SearchUIModel.Empty -> SearchContent(
            modifier,
            searchQuery,
            onNavigateToInterimSearchScreen,
            onNavigateToAdvancedSearchScreen,
            posterWidth,
            onNavigateToDetailsScreen,
            emptyList(),
            null,
            searchViewModel,
            gridState
        )

        SearchUIModel.Loading -> LoadingScreen()

        SearchUIModel.NoConnection -> NoConnectionScreen()

        is SearchUIModel.Data -> {
            SearchContent(
                modifier,
                searchQuery,
                onNavigateToInterimSearchScreen,
                onNavigateToAdvancedSearchScreen,
                posterWidth,
                onNavigateToDetailsScreen,
                searchUIModel.collectionMovies,
                searchUIModel.totalPages,
                searchViewModel,
                gridState
            )
        }
    }
}

@OptIn(ExperimentalLayoutApi::class)
@Composable
private fun SearchContent(
    modifier: Modifier,
    searchQuery: MutableState<String>,
    onNavigateToInterimSearchScreen: () -> Unit,
    onNavigateToAdvancedSearchScreen: (String) -> Unit,
    posterWidth: Dp,
    onNavigateToDetailsScreen: (String, Int) -> Unit,
    collectionMovies: List<Movie>,
    totalPages: Int? = null,
    searchViewModel: SearchViewModel,
    gridState: LazyGridState = rememberLazyGridState()
) {
    Column(
        modifier = modifier.fillMaxSize()
    ) {
        SearchBar(
            searchQuery = searchQuery,
            onSearchQueryChange = { query ->
                searchViewModel.searchMovies(query, 1)
            },
            onSearchBarClick = onNavigateToInterimSearchScreen
        )

        LazyVerticalGrid(
            state = gridState,
            columns = GridCells.Adaptive(posterWidth)
        ) {
            if (collectionMovies.isEmpty()) {
                item {
                    Text(
                        text = "No results found",
                        modifier = Modifier.padding(10.dp)
                    )
                }
            } else {
                items(collectionMovies.size) { index ->
                    CreateSearchPoster(
                        posterWidth = posterWidth,
                        onNavigateToDetailsScreen = onNavigateToDetailsScreen,
                        movie = collectionMovies[index]
                    )
                }
            }

            item(span = { GridItemSpan(2) }) {
                if (searchViewModel.currentPage.intValue < (searchViewModel.totalPages.value
                        ?: 0)
                ) {
                    Button(
                        onClick = {
                            searchViewModel.searchMovies(
                                searchQuery.value,
                                searchViewModel.currentPage.intValue + 1
                            )
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
}

@Composable
private fun CreateSearchPoster(
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
                    .clip(shape = RoundedCornerShape(30.dp))
                    .clickable(enabled = isClickAble) {
                        if (isClickAble) {
                            onNavigateToDetailsScreen(
                                movie.title,
                                movie.id
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
                .clickable { onNavigateToDetailsScreen(movie.title, movie.id) },
            text = movie.title,
            textAlign = TextAlign.Center,
            fontWeight = FontWeight.Bold,
            lineHeight = 15.sp
        )
    }
}