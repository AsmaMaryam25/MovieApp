package com.example.movieapp.screens.search

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
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
import com.example.movieapp.components.SearchBar
import com.example.movieapp.models.CollectionMovie
import com.example.movieapp.screens.search.SearchViewModel.SearchUIModel

@Composable
fun SearchScreen(
    modifier: Modifier = Modifier,
    onNavigateToAdvancedSearchScreen: (String) -> Unit,
    onNavigateToDetailsScreen: (String, Int) -> Unit
) {

    val SearchViewModel = viewModel<SearchViewModel>()
    val searchUIModel = SearchViewModel.searchUIState.collectAsState().value

    val posterWidth = 170.dp
    val searchQuery = remember { mutableStateOf("") }

    when (searchUIModel) {
        SearchUIModel.Empty -> Text("Empty")
        SearchUIModel.Loading -> Box(
            contentAlignment = Alignment.Center,
            modifier = Modifier.size(50.dp)
        ) {
            CircularProgressIndicator(modifier = Modifier.size(50.dp))
        }

        is SearchUIModel.Data -> {
            SearchContent(
                modifier,
                searchQuery,
                onNavigateToAdvancedSearchScreen,
                posterWidth,
                onNavigateToDetailsScreen,
                searchUIModel.popularCollectionMovies
            )
        }
    }
}

@OptIn(ExperimentalLayoutApi::class)
@Composable
private fun SearchContent(
    modifier: Modifier,
    searchQuery: MutableState<String>,
    onNavigateToAdvancedSearchScreen: (String) -> Unit,
    posterWidth: Dp,
    onNavigateToDetailsScreen: (String, Int) -> Unit,
    popularCollectionMovies: List<CollectionMovie>
) {
    Column(
        modifier = modifier.fillMaxSize()
    ) {
        SearchBar(
            searchQuery = searchQuery,
            onSearchQueryChange = { query ->
                //TODO Handle search
            },
            onClickMenu = { onNavigateToAdvancedSearchScreen("Advanced Search") }
        )

        LazyColumn(
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            item {
                FlowRow(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.Center
                ) {
                    popularCollectionMovies.forEach { movie ->
                        CreateSearchPoster(
                            posterWidth = posterWidth,
                            onNavigateToDetailsScreen = onNavigateToDetailsScreen,
                            movie = movie
                        )
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
    movie: CollectionMovie
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
