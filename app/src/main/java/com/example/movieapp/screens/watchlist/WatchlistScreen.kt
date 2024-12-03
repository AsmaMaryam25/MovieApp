package com.example.movieapp.screens.watchlist

import android.R.attr.fontWeight
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.painter.ColorPainter
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import coil3.compose.AsyncImage
import com.example.movieapp.data.model.MovieItem
import com.example.movieapp.screens.watchlist.WatchlistViewModel.WatchlistUIModel
import java.util.Locale
import kotlin.String

@Composable
fun WatchlistScreen(
    modifier: Modifier = Modifier,
    onNavigateToDetailsScreen: (String, Int) -> Unit
) {
    val watchlistViewModel: WatchlistViewModel = viewModel()
    val watchlistUIModel = watchlistViewModel.watchlistUIState.collectAsState().value

    when (watchlistUIModel) {
        WatchlistUIModel.Empty -> Text("Empty")
        WatchlistUIModel.Loading -> Box(
            contentAlignment = Alignment.Center,
            modifier = Modifier.size(50.dp)
        ) {
            CircularProgressIndicator(modifier = Modifier.size(50.dp))
        }

        is WatchlistUIModel.Data -> WatchlistContent(
            onNavigateToDetailsScreen,
            modifier,
            watchlistUIModel.watchlist
        )
    }
}

@Composable
private fun WatchlistContent(
    onNavigateToDetailsScreen: (String, Int) -> Unit,
    modifier: Modifier,
    watchlist: List<MovieItem>
) {
    val posterWidth = 140.dp
    LazyColumn(
        modifier = modifier.fillMaxSize(),
    ) {
        if (watchlist.isEmpty()) {
            item {
                Column(
                    modifier = modifier.fillParentMaxSize(),
                    verticalArrangement = Arrangement.Center,
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = "Nothing in watchlist yet",
                        style = TextStyle(
                            fontSize = 25.sp,
                            lineHeight = 30.sp,
                        ),
                    )
                }
            }
        } else {
            items(watchlist.size) { index ->
                CreateWatchlistCard(
                    posterWidth,
                    onNavigateToDetailsScreen = onNavigateToDetailsScreen,
                    watchlistMovie = watchlist[index]
                )
                HorizontalDivider(
                    modifier = modifier
                        .fillMaxWidth()
                        .padding(vertical = 20.dp)
                )
            }
        }
    }
}

@Composable
fun CreateWatchlistCard(
    posterWidth: Dp,
    modifier: Modifier = Modifier,
    onNavigateToDetailsScreen: (String, Int) -> Unit,
    watchlistMovie: MovieItem
) {
    Row(
        horizontalArrangement = Arrangement.SpaceEvenly,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier = Modifier
                .width(posterWidth)
                .aspectRatio(2 / 3f)
                .clip(shape = RoundedCornerShape(30.dp))
                .background(Color.Gray)
        ) {
            AsyncImage(
                model = watchlistMovie.posterPath,
                contentDescription = null,
                modifier = Modifier
                    .width(posterWidth)
                    .aspectRatio(2 / 3f)
                    .clip(shape = RoundedCornerShape(30.dp))
                    .clickable(onClick = {
                        onNavigateToDetailsScreen(
                            watchlistMovie.title ?: "",
                            watchlistMovie.id?.toInt() ?: 0
                        )
                    }),
                placeholder = ColorPainter(Color.Gray)
            )
        }
        Spacer(modifier = Modifier.size(30.dp))
        Text(
            text = watchlistMovie.title ?: "",
            style = TextStyle(
                fontSize = 25.sp,
                lineHeight = 30.sp,
                textAlign = TextAlign.Center
            ),
            fontWeight = FontWeight.Bold,
            modifier = modifier
                .padding(vertical = 40.dp)
                .weight(1f)
                .clickable {
                    onNavigateToDetailsScreen(
                        watchlistMovie.title ?: "",
                        watchlistMovie.id?.toInt() ?: 0
                    )
                }
        )
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier.weight(0.5f)
        ) {
            Icon(
                imageVector = Icons.Default.Star,
                contentDescription = "Rating",
                modifier = modifier.size(24.dp)
            )
            Text(
                text = String.format(Locale.getDefault(), "%.2f", watchlistMovie.rating),
            )
        }
    }
}

