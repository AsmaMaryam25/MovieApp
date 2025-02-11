package com.example.blackbeard.screens.favorite

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
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
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
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import coil3.compose.AsyncImage
import com.example.blackbeard.R
import com.example.blackbeard.components.onDebounceClick
import com.example.blackbeard.data.model.MovieItem
import com.example.blackbeard.screens.EmptyScreen
import com.example.blackbeard.screens.LoadingScreen
import com.example.blackbeard.screens.favorite.FavoriteViewModel.FavoriteUIModel
import kotlinx.coroutines.launch
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FavoriteScreen(
    modifier: Modifier = Modifier,
    onNavigateToDetailsScreen: (String, Int) -> Unit
) {
    val favoriteViewModel: FavoriteViewModel = viewModel()
    val favoriteUIModel = favoriteViewModel.favoriteUIState.collectAsState().value

    Scaffold(
        topBar = {
            TopAppBar({
                Text(
                    text = "Favorites",
                    style = MaterialTheme.typography.titleLarge.copy(
                        fontWeight = FontWeight.Bold
                    ),
                    modifier = Modifier.fillMaxWidth(),
                )
            }
            )
        },
    ) {

        when (favoriteUIModel) {
            FavoriteUIModel.Empty -> EmptyScreen(modifier.padding(it))
            FavoriteUIModel.Loading -> LoadingScreen(modifier.padding(it))
            is FavoriteUIModel.Data -> FavoriteContent(
                onNavigateToDetailsScreen,
                modifier.fillMaxSize(),
                favoriteUIModel.favorites,
            )
        }
    }
}

@Composable
private fun FavoriteContent(
    onNavigateToDetailsScreen: (String, Int) -> Unit,
    modifier: Modifier,
    favorites: List<MovieItem>,
) {
    val posterWidth = 140.dp
    if (favorites.isEmpty()) {
        Column(
            modifier = modifier,
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = stringResource(id = R.string.no_favorites),
                style = TextStyle(
                    fontSize = 25.sp,
                    lineHeight = 30.sp,
                ),
            )
        }
    } else {
        LazyColumn(
            modifier = modifier,
        ) {
            item {
                Spacer(modifier = Modifier.size(70.dp))
            }

            items(favorites.size) { index ->
                CreateFavCard(
                    posterWidth,
                    onNavigateToDetailsScreen = onNavigateToDetailsScreen,
                    favoriteMovie = favorites[index]
                )
                HorizontalDivider(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 20.dp)
                )
            }
        }
    }
}

@Composable
fun CreateFavCard(
    posterWidth: Dp,
    modifier: Modifier = Modifier,
    onNavigateToDetailsScreen: (String, Int) -> Unit,
    favoriteMovie: MovieItem
) {
    Row(
        horizontalArrangement = Arrangement.SpaceEvenly,
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier
            .padding(horizontal = 10.dp)
    ) {
        Box(
            modifier = Modifier
                .width(posterWidth)
                .aspectRatio(2 / 3f)
                .clip(shape = RoundedCornerShape(30.dp))
                .background(Color.Gray)
        ) {
            AsyncImage(
                model = favoriteMovie.posterPath,
                contentDescription = null,
                modifier = Modifier
                    .width(posterWidth)
                    .aspectRatio(2 / 3f)
                    .clip(shape = RoundedCornerShape(30.dp))
                    .clickable(onClick = onDebounceClick {
                        onNavigateToDetailsScreen(
                            favoriteMovie.title,
                            favoriteMovie.id.toInt()
                        )
                    }),
                placeholder = ColorPainter(Color.Gray)
            )
        }
        Spacer(modifier = Modifier.size(10.dp))
        Text(
            text = favoriteMovie.title,
            style = TextStyle(
                fontSize = 25.sp,
                lineHeight = 30.sp,
                textAlign = TextAlign.Center
            ),
            fontWeight = FontWeight.Bold,
            modifier = if (favoriteMovie.rating != -1.0) {
                modifier
                    .padding(vertical = 40.dp)
                    .weight(1f)
                    .clickable {
                        onNavigateToDetailsScreen(
                            favoriteMovie.title,
                            favoriteMovie.id.toInt()
                        )
                    }
            } else {
                modifier
                    .padding(vertical = 40.dp, horizontal = 10.dp)
                    .weight(1f)
                    .clickable {
                        onNavigateToDetailsScreen(
                            favoriteMovie.title,
                            favoriteMovie.id.toInt()
                        )
                    }
            }
        )
        if (favoriteMovie.rating != -1.0) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier.weight(0.5f)
            ) {
                Icon(
                    imageVector = Icons.Filled.Star,
                    contentDescription = "Rating",
                    modifier = modifier.size(24.dp),
                    tint = MaterialTheme.colorScheme.primary
                )
                Text(
                    text = String.format(Locale.getDefault(), "%.2f", favoriteMovie.rating),
                )
            }
        }
    }
}