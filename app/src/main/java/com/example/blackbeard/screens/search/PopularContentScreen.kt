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
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
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
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil3.compose.AsyncImage
import com.example.blackbeard.R
import com.example.blackbeard.models.CollectionMovie
import com.example.blackbeard.screens.home.TitleText
import com.google.accompanist.pager.ExperimentalPagerApi
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@OptIn(ExperimentalLayoutApi::class, ExperimentalPagerApi::class)
@Composable
fun PopularContentScreen(
    modifier: Modifier,
    onNavigateToDetailsScreen: (String, Int) -> Unit,
    collectionMovies: List<CollectionMovie>,
    searchViewModel: SearchViewModel,
    gridState: LazyGridState = rememberLazyGridState(),
) {

    val posterWidth = 170.dp

    Column(
        modifier = modifier.fillMaxSize()
    ) {
        var isSearchBarFocused by remember { mutableStateOf(false) }
        val tabs = listOf("Recent", "Advanced Search")



        if (!isSearchBarFocused) {
            if (collectionMovies.isEmpty()) {
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
                            TitleText(text = stringResource(id = R.string.popular))
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
                }
            }
        }
    }
}

@Composable
private fun CreateSearchPoster(
    searchViewModel: SearchViewModel,
    posterWidth: Dp,
    modifier: Modifier = Modifier,
    onNavigateToDetailsScreen: (String, Int) -> Unit,
    movie: CollectionMovie
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