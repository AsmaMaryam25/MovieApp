package com.example.movieapp.screens.home

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.snapping.rememberSnapFlingBehavior
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.painter.ColorPainter
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import coil3.compose.AsyncImage
import com.example.movieapp.models.CollectionMovie
import com.example.movieapp.screens.home.HomeViewModel.HomeUIModel
import kotlinx.coroutines.launch


@Composable
fun HomeScreen(onNavigateToDetailsScreen: (String, Int) -> Unit, modifier: Modifier = Modifier) {

    val homeViewModel: HomeViewModel = viewModel()
    val homeUIModel = homeViewModel.homeUIState.collectAsState().value

    when (homeUIModel) {
        HomeUIModel.Empty -> Text("Empty")
        HomeUIModel.Loading -> Box(
            contentAlignment = Alignment.Center,
            modifier = Modifier.size(50.dp)
        ) {
            CircularProgressIndicator(modifier = Modifier.size(50.dp))
        }

        is HomeUIModel.Data -> HomeContent(modifier, homeUIModel, onNavigateToDetailsScreen)
    }
}

@Composable
private fun HomeContent(
    modifier: Modifier,
    homeUIModel: HomeUIModel,
    onNavigateToDetailsScreen: (String, Int) -> Unit
) {
    LazyColumn(
        modifier = modifier.fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally,
        state = rememberLazyListState()
    ) {

        item {
            TitleText("Now Playing")
        }

        item {
            if (homeUIModel is HomeUIModel.Data) {
                CreatePosters(onNavigateToDetailsScreen, homeUIModel.nowPlayingCollectionMovies)
            }
        }

        item {
            TitleText("Popular")
        }

        item {
            if (homeUIModel is HomeUIModel.Data) {
                CreatePosters(onNavigateToDetailsScreen, homeUIModel.popularCollectionMovies)
            }
        }

        item {
            TitleText("Top Rated")
        }

        item {
            if (homeUIModel is HomeUIModel.Data) {
                CreatePosters(onNavigateToDetailsScreen, homeUIModel.topRatedCollectionMovies)
            }
        }

        item {
            TitleText("Upcoming")
        }

        item {
            if (homeUIModel is HomeUIModel.Data) {
                CreatePosters(onNavigateToDetailsScreen, homeUIModel.upcomingCollectionMovies)
            }
        }
    }
}

@Composable
private fun CreatePoster(
    onNavigateToDetailsScreen: (String, Int) -> Unit,
    posterWidth: Dp = 300.dp,
    collectionMovie: CollectionMovie
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Box(
            modifier = Modifier
                .width(posterWidth)
                .aspectRatio(2 / 3f)
                .clip(shape = RoundedCornerShape(30.dp))
                .background(Color.Gray)
        ) {
            AsyncImage(
                model = collectionMovie.posterPath,
                contentDescription = null,
                modifier = Modifier
                    .width(posterWidth)
                    .aspectRatio(2 / 3f)
                    .clip(shape = RoundedCornerShape(30.dp))
                    .clickable(onClick = {
                        onNavigateToDetailsScreen(
                            collectionMovie.title,
                            collectionMovie.id
                        )
                    }),
                placeholder = ColorPainter(Color.Gray)
            )
        }
        Column(
            modifier = Modifier.height(150.dp)
        ) {
            Text(
                modifier = Modifier
                    .width(posterWidth)
                    .padding(start = 35.dp, top = 15.dp, end = 35.dp)
                    .clickable { onNavigateToDetailsScreen(collectionMovie.title, collectionMovie.id) },
                text = collectionMovie.title,
                style = TextStyle(
                    fontSize = 25.sp,
                    lineHeight = 30.sp,
                    textAlign = TextAlign.Center
                ),
                maxLines = 3,
                overflow = TextOverflow.Ellipsis,
                fontWeight = FontWeight.Bold
            )
            Text(
                modifier = Modifier
                    .width(posterWidth),
                text = collectionMovie.genres.joinToString { it.name },
                style = TextStyle(
                    textAlign = TextAlign.Center
                ),
            )
            Text(
                modifier = Modifier
                    .width(posterWidth),
                text = collectionMovie.releaseDate.year.toString(),
                style = TextStyle(
                    textAlign = TextAlign.Center
                ),
            )
            Text(
                modifier = Modifier
                    .width(posterWidth),
                text = collectionMovie.overview ?: "No overview available",
                maxLines = 5,
                overflow = TextOverflow.Ellipsis,
                style = TextStyle(
                    textAlign = TextAlign.Center
                ),
            )
        }
    }
}

@Composable
fun TitleText(text: String, modifier: Modifier = Modifier) {
    Text(
        modifier = modifier.padding(10.dp),
        text = text,
        fontSize = 25.sp,
        fontWeight = FontWeight.Bold
    )
}

@Composable
fun CreatePosters(
    onNavigateToDetailsScreen: (String, Int) -> Unit,
    collectionMovies: List<CollectionMovie>
) {
    val coroutineScope = rememberCoroutineScope()
    val rowState = rememberLazyListState()
    val snapBehavior = rememberSnapFlingBehavior(lazyListState = rowState)

    LazyRow(
        state = rowState,
        horizontalArrangement = Arrangement.spacedBy(20.dp),
        flingBehavior = snapBehavior,
        contentPadding = PaddingValues(start = 60.dp, end = 60.dp)
    ) {
        items(collectionMovies.size) { index ->
            CreatePoster(onNavigateToDetailsScreen, 300.dp, collectionMovies[index])
        }
    }

    LaunchedEffect(rowState) {
        coroutineScope.launch {
            rowState.scrollToItem(collectionMovies.size / 2) // Assuming 6 items, center is at index 3
        }
    }
}