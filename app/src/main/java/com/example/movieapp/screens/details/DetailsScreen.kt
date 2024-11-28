package com.example.movieapp.screens.details

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
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Bookmark
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.outlined.BookmarkBorder
import androidx.compose.material.icons.outlined.FavoriteBorder
import androidx.compose.material.icons.outlined.StarOutline
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.painter.ColorPainter
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import coil3.compose.AsyncImage
import com.example.movieapp.models.Cast
import com.example.movieapp.models.Credits
import com.example.movieapp.models.Crew
import com.example.movieapp.models.Movie

@Composable
fun DetailsScreen(modifier: Modifier = Modifier, movieId: Int, showTopBar: () -> Unit, setVideoLink: (String) -> Unit) {
    showTopBar()

    val detailsViewModel = viewModel<DetailsViewModel>(factory = DetailsViewModelFactory(movieId))
    val detailsUIModel = detailsViewModel.detailsUIState.collectAsState().value


    when (detailsUIModel) {
        DetailsViewModel.DetailsUIModel.Empty -> Text("Empty")
        DetailsViewModel.DetailsUIModel.Loading -> Text("Loading")
        is DetailsViewModel.DetailsUIModel.Data -> DetailsContent(
            modifier = modifier,
            movie = detailsUIModel.movie,
            credits = detailsUIModel.credits,
            setVideoLink = setVideoLink
        )
    }
}

@Composable
private fun DetailsContent(modifier: Modifier, movie: Movie, credits: Credits, setVideoLink: (String) -> Unit) {

    //setVideoLink(movie.)

    LazyColumn(
        modifier = modifier
            .padding(10.dp)
            .fillMaxSize(),
        verticalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        item {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Box(
                        modifier = Modifier
                            .width(180.dp)
                            .aspectRatio(2 / 3f)
                            .clip(shape = RoundedCornerShape(30.dp))
                            .background(Color.Gray)
                    ) {
                        AsyncImage(
                            model = movie.posterPath,
                            contentDescription = null,
                            modifier = Modifier
                                .fillMaxSize()
                                .clip(shape = RoundedCornerShape(30.dp)),
                            placeholder = ColorPainter(Color.Gray)
                        )
                    }
                    Row {
                        CreateStars(Modifier)
                    }
                }
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center,
                    modifier = Modifier.padding(10.dp)
                ) {
                    Text(
                        text = movie.title,
                        fontSize = 20.sp,
                        textAlign = TextAlign.Center,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.fillMaxWidth()
                    )
                    Text(
                        text = movie.genres.map { it.name }.joinToString(),
                        fontSize = 15.sp,
                        textAlign = TextAlign.Center,
                    )
                    Text(
                        text = movie.releaseDate.year.toString(),
                        fontSize = 15.sp,
                        textAlign = TextAlign.Center,
                    )
                    Row {
                        var favoriteIcon by remember { mutableStateOf(Icons.Outlined.FavoriteBorder) }
                        var watchlistIcon by remember { mutableStateOf(Icons.Outlined.BookmarkBorder) }
                        Icon(
                            imageVector = favoriteIcon,
                            contentDescription = "Favorite",
                            modifier = Modifier
                                .padding(5.dp)
                                .size(40.dp)
                                .clickable(onClick = {
                                    //TODO Add to favorites
                                    favoriteIcon =
                                        if (favoriteIcon == Icons.Outlined.FavoriteBorder) {
                                            Icons.Filled.Favorite
                                        } else {
                                            Icons.Outlined.FavoriteBorder
                                        }
                                })
                        )
                        Icon(
                            imageVector = watchlistIcon,
                            contentDescription = "Watchlist",
                            modifier = Modifier
                                .padding(5.dp)
                                .size(40.dp)
                                .clickable(onClick = {
                                    //TODO Add to watchlist
                                    watchlistIcon =
                                        if (watchlistIcon == Icons.Outlined.BookmarkBorder) {
                                            Icons.Filled.Bookmark
                                        } else {
                                            Icons.Outlined.BookmarkBorder
                                        }
                                })
                        )
                    }
                    Spacer(
                        modifier = Modifier
                            .height(30.dp)
                            .fillMaxWidth()
                    )
                    Column (horizontalAlignment = Alignment.CenterHorizontally) {
                        Icon(
                            imageVector = Icons.Filled.Star,
                            contentDescription = "Favorite",
                            modifier = Modifier
                                .size(50.dp)
                        )
                        Text(
                            text = "1.5",
                            fontSize = 15.sp,
                            textAlign = TextAlign.Center,
                        )
                    }
                }
            }
        }
        item {
            Text(
                text = "Overview",
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold
            )
        }
        item {
            Text(
                text = movie.overview ?: "No overview available"
            )
        }
        item {
            Text(
                text = buildAnnotatedString {
                    withStyle(style = SpanStyle(fontWeight = FontWeight.Bold)) {
                        append("Release date: ")
                    }
                    append(movie.releaseDate.toString())
                },
                fontSize = 15.sp
            )
        }
        item {
            Text(
                text = buildAnnotatedString {
                    withStyle(style = SpanStyle(fontWeight = FontWeight.Bold)) {
                        append("Produced by: ")
                    }
                    append(movie.productionCompanies.joinToString { it.name })
                },
                fontSize = 15.sp
            )
        }
        item {
            Text(
                text = buildAnnotatedString {
                    withStyle(style = SpanStyle(fontWeight = FontWeight.Bold)) {
                        append("Produced in: ")
                    }
                    append(movie.productionCountries.joinToString { it.name })
                },
                fontSize = 15.sp,
            )
        }
        item {
            Text(
                text = buildAnnotatedString {
                    withStyle(style = SpanStyle(fontWeight = FontWeight.Bold)) {
                        append("Revenue generated: ")
                    }
                    append(movie.revenue.toString())
                },
                fontSize = 15.sp
            )
        }
        item {
            Text(
                text = buildAnnotatedString {
                    withStyle(style = SpanStyle(fontWeight = FontWeight.Bold)) {
                        append("Runtime: ")
                    }
                    append("${movie.runtime} minutes")
                },
                fontSize = 15.sp
            )
        }
        item {
            Text(
                text = buildAnnotatedString {
                    withStyle(style = SpanStyle(fontWeight = FontWeight.Bold)) {
                        append("Spoken languages: ")
                    }
                    append(movie.spokenLanguages.joinToString { it.name })
                },
                fontSize = 15.sp,
            )
        }
        if(credits.cast.isNotEmpty()) {
            item {
                Text(
                    text = "Cast",
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold
                )
            }
            item {
                LazyRow(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                    items(5) {
                        CreateCast(Modifier, credits.cast[it])
                    }
                }
            }
        }
        if(credits.crew.isNotEmpty()) {
            item {
                Text(
                    text = "Crew",
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold
                )
            }
            item {
                LazyRow(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                    items(5) {
                        CreateCrew(Modifier, credits.crew[it])
                    }
                }
            }
        }
    }
}

@Composable
private fun CreateStars(modifier: Modifier) {
    val iconList = remember {
        mutableStateListOf(
            Icons.Outlined.StarOutline,
            Icons.Outlined.StarOutline,
            Icons.Outlined.StarOutline,
            Icons.Outlined.StarOutline,
            Icons.Outlined.StarOutline
        )
    }
    Row {
        for (i in 0 until 5) {
            Icon(
                imageVector = iconList[i],
                contentDescription = "Star Rating ${i + 1}",
                modifier = modifier
                    .size(30.dp)
                    .clickable(onClick = {
                        for (j in 0 until 5) {
                            if (iconList[i] == Icons.Outlined.StarOutline) {
                                iconList[j] = Icons.Filled.Star
                            } else if (j > i) {
                                iconList[j] = Icons.Outlined.StarOutline
                            }
                        }
                    }),
            )
        }
    }
}

@Composable
private fun CreateCast(modifier: Modifier, cast: Cast) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Box(
            modifier = Modifier
                .width(130.dp)
                .aspectRatio(2 / 3f)
                .clip(shape = RoundedCornerShape(30.dp))
                .background(Color.Gray)
        ) {
            AsyncImage(
                model = cast.profilePath,
                contentDescription = null,
                modifier = Modifier
                    .fillMaxSize()
                    .clip(shape = RoundedCornerShape(30.dp)),
                placeholder = ColorPainter(Color.Gray)
            )
        }
        Text(
            text = cast.name,
            fontSize = 15.sp,
            textAlign = TextAlign.Center,
            modifier = modifier.width(100.dp),
            fontWeight = FontWeight.Bold
        )
        Text(
            text = cast.character,
            fontSize = 15.sp,
            textAlign = TextAlign.Center,
            modifier = modifier.width(100.dp),
        )
    }
}

@Composable
private fun CreateCrew(modifier: Modifier, crew: Crew) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Box(
            modifier = Modifier
                .width(130.dp)
                .aspectRatio(2 / 3f)
                .clip(shape = RoundedCornerShape(30.dp))
                .background(Color.Gray)
        ) {
            AsyncImage(
                model = crew.profilePath,
                contentDescription = null,
                modifier = Modifier
                    .fillMaxSize()
                    .clip(shape = RoundedCornerShape(30.dp)),
                placeholder = ColorPainter(Color.Gray)
            )
        }
        Text(
            text = crew.name,
            fontSize = 15.sp,
            textAlign = TextAlign.Center,
            modifier = modifier.width(100.dp),
            fontWeight = FontWeight.Bold
        )
        Text(
            text = crew.job,
            fontSize = 15.sp,
            textAlign = TextAlign.Center,
            modifier = modifier.width(100.dp),
        )
    }
}