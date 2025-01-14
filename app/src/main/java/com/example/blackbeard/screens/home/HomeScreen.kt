package com.example.blackbeard.screens.home

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.snapping.rememberSnapFlingBehavior
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
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
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import coil3.compose.AsyncImage
import com.example.blackbeard.models.CollectionMovie
import com.example.blackbeard.screens.EmptyScreen
import com.example.blackbeard.screens.LoadingScreen
import com.example.blackbeard.screens.NoConnectionScreen
import com.example.blackbeard.screens.home.HomeViewModel.HomeUIModel
import kotlinx.coroutines.launch


@Composable
fun HomeScreen(onNavigateToDetailsScreen: (String, Int) -> Unit, modifier: Modifier = Modifier) {

    val homeViewModel: HomeViewModel = viewModel()
    val homeUIModel = homeViewModel.homeUIState.collectAsState().value

    val nowPlayingState = rememberSaveable(saver = LazyListState.Saver) { LazyListState() }
    val popularState = rememberSaveable(saver = LazyListState.Saver) { LazyListState() }
    val topRatedState = rememberSaveable(saver = LazyListState.Saver) { LazyListState() }
    val upcomingState = rememberSaveable(saver = LazyListState.Saver) { LazyListState() }


    when (homeUIModel) {
        HomeUIModel.Empty -> EmptyScreen()
        HomeUIModel.Loading -> LoadingScreen()
        HomeUIModel.NoConnection -> NoConnectionScreen()
        is HomeUIModel.Data -> HomeContent(
            modifier,
            homeUIModel,
            onNavigateToDetailsScreen,
            nowPlayingState,
            popularState,
            topRatedState,
            upcomingState
        )
    }
}

@Composable
private fun HomeContent(
    modifier: Modifier,
    homeUIModel: HomeUIModel,
    onNavigateToDetailsScreen: (String, Int) -> Unit,
    nowPlayingState: LazyListState,
    popularState: LazyListState,
    topRatedState: LazyListState,
    upcomingState: LazyListState
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
                CreatePosters(
                    onNavigateToDetailsScreen,
                    homeUIModel.nowPlayingCollectionMovies,
                    nowPlayingState
                )
            }
        }

        item {
            TitleText("Popular")
        }

        item {
            if (homeUIModel is HomeUIModel.Data) {
                CreatePosters(
                    onNavigateToDetailsScreen,
                    homeUIModel.popularCollectionMovies,
                    popularState
                )
            }
        }

        item {
            TitleText("Top Rated")
        }

        item {
            if (homeUIModel is HomeUIModel.Data) {
                CreatePosters(
                    onNavigateToDetailsScreen,
                    homeUIModel.topRatedCollectionMovies,
                    topRatedState
                )
            }
        }

        item {
            TitleText("Upcoming")
        }

        item {
            if (homeUIModel is HomeUIModel.Data) {
                CreatePosters(
                    onNavigateToDetailsScreen,
                    homeUIModel.upcomingCollectionMovies,
                    upcomingState
                )
            }
        }
    }
}

@Composable
private fun CreatePoster(
    onNavigateToDetailsScreen: (String, Int) -> Unit,
    collectionMovie: CollectionMovie
) {
    var isClickAble by remember { mutableStateOf(true) }
    val coroutineScope = rememberCoroutineScope()

    Column(
        Modifier.width(300.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(5.dp)
    ) {
        Box(
            modifier = Modifier
                .aspectRatio(2 / 3f)
                .clip(shape = RoundedCornerShape(30.dp))
                .background(Color.Gray)
        ) {
            AsyncImage(
                model = collectionMovie.posterPath,
                contentDescription = null,
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable(enabled = isClickAble) {
                        if (isClickAble) {
                            onNavigateToDetailsScreen(
                                collectionMovie.title,
                                collectionMovie.id
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
        Column(
            modifier = Modifier.height(152.dp),
            verticalArrangement = Arrangement.spacedBy(5.dp)
        ) {
            Text(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable {
                        onNavigateToDetailsScreen(
                            collectionMovie.title,
                            collectionMovie.id
                        )
                    },
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
                modifier = Modifier.fillMaxWidth(),
                text = collectionMovie.genres.joinToString { it.name },
                style = TextStyle(
                    textAlign = TextAlign.Center
                ),
            )
            Text(
                modifier = Modifier.fillMaxWidth(),
                text = collectionMovie.releaseDate.year.toString(),
                style = TextStyle(
                    textAlign = TextAlign.Center
                ),
            )
            Text(
                modifier = Modifier.fillMaxWidth(),
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
    collectionMovies: List<CollectionMovie>,
    state: LazyListState
) {
    val rowState = state
    val snapBehavior = rememberSnapFlingBehavior(lazyListState = rowState)

    LazyRow(
        state = rowState,
        horizontalArrangement = Arrangement.spacedBy(20.dp),
        flingBehavior = snapBehavior,
        contentPadding = PaddingValues(start = 60.dp, end = 60.dp)
    ) {
        items(collectionMovies.size) { index ->
            CreatePoster(onNavigateToDetailsScreen, collectionMovies[index])
        }
    }
}