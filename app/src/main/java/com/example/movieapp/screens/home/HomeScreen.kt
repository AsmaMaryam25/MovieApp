package com.example.movieapp.screens.home

import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.snapping.rememberSnapFlingBehavior
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import coil3.compose.AsyncImage
import com.example.movieapp.models.Movie
import com.example.movieapp.screens.home.HomeViewModel.HomeUIModel
import kotlinx.coroutines.launch


@Composable
fun HomeScreen(onNavigateToDetailsScreen: (String) -> Unit, modifier: Modifier = Modifier) {

    val homeViewModel: HomeViewModel = viewModel()
    val homeUIModel = homeViewModel.homeUIState.collectAsState().value

    when (homeUIModel) {
        HomeUIModel.Empty -> Text("Empty")
        HomeUIModel.Loading -> Text("Loading")
        is HomeUIModel.Data -> HomeContent(modifier, homeUIModel, onNavigateToDetailsScreen)
    }
}

@Composable
private fun HomeContent(
    modifier: Modifier,
    homeUIModel: HomeUIModel,
    onNavigateToDetailsScreen: (String) -> Unit
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
                CreatePosters(onNavigateToDetailsScreen, homeUIModel.nowPlayingMovies)
            }
        }

        item {
            TitleText("Popular")
        }

        item {
            if (homeUIModel is HomeUIModel.Data) {
                CreatePosters(onNavigateToDetailsScreen, homeUIModel.popularMovies)
            }
        }

        item {
            TitleText("Top Rated")
        }

        item {
            if (homeUIModel is HomeUIModel.Data) {
                CreatePosters(onNavigateToDetailsScreen, homeUIModel.topRatedMovies)
            }
        }

        item {
            TitleText("Upcoming")
        }

        item {
            if (homeUIModel is HomeUIModel.Data) {
                CreatePosters(onNavigateToDetailsScreen, homeUIModel.upcomingMovies)
            }
        }
    }
}

@Composable
private fun CreatePoster(
    onNavigateToDetailsScreen: (String) -> Unit,
    posterWidth: Dp = 300.dp,
    movie: Movie
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        AsyncImage(
            model = movie.posterPath,
            contentDescription = null,
            modifier = Modifier
                .width(posterWidth)
                .clip(shape = RoundedCornerShape(30.dp))
                .clickable(onClick = { onNavigateToDetailsScreen("Yu-Gi-Oh!: The Dark Side of Dimensions") })
        )
        Text(
            modifier = Modifier
                .width(posterWidth)
                .padding(start = 35.dp, top = 15.dp, end = 35.dp)
                .clickable { onNavigateToDetailsScreen("Yu-Gi-Oh!: The Dark Side of Dimensions") },
            text = movie.title,
            style = TextStyle(
                fontSize = 25.sp,
                lineHeight = 30.sp,
                textAlign = TextAlign.Center
            ),
            fontWeight = FontWeight.Bold
        )
        Text(
            modifier = Modifier
                .width(posterWidth)
                .padding(vertical = 5.dp),
            text = movie.genres.joinToString(", ") { it.name },
            style = TextStyle(
                textAlign = TextAlign.Center
            ),
        )
        Text(
            modifier = Modifier
                .width(posterWidth),
            text = movie.releaseDate.year.toString(),
            style = TextStyle(
                textAlign = TextAlign.Center
            ),
        )
        Text(
            modifier = Modifier
                .width(posterWidth)
                .padding(bottom = 10.dp),
            text = movie.overview ?: "No overview available",
            style = TextStyle(
                textAlign = TextAlign.Center
            ),
        )
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
    onNavigateToDetailsScreen: (String) -> Unit,
    movies: List<Movie>
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
        items(movies.size) { index ->
            CreatePoster(onNavigateToDetailsScreen, 300.dp, movies[index])
        }
    }

    LaunchedEffect(rowState) {
        coroutineScope.launch {
            rowState.scrollToItem(movies.size / 2) // Assuming 6 items, center is at index 3
        }
    }
}