package com.example.blackbeard.screens.details

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentSize
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
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
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
import androidx.compose.ui.tooling.preview.Devices
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import coil3.compose.AsyncImage
import com.example.blackbeard.models.Cast
import com.example.blackbeard.models.Credits
import com.example.blackbeard.models.Crew
import com.example.blackbeard.models.Genre
import com.example.blackbeard.models.LocalMovie
import com.example.blackbeard.screens.EmptyScreen
import com.example.blackbeard.screens.LoadingScreen
import com.example.blackbeard.screens.NoConnectionScreen
import java.text.NumberFormat
import java.util.Currency
import java.util.Locale

@Composable
fun DetailsScreen(
    modifier: Modifier = Modifier,
    movieId: Int,
    showTopBar: () -> Unit,
    setVideoLink: (String?) -> Unit
) {

    LaunchedEffect(Unit) {
        showTopBar()
    }

    val detailsViewModel = viewModel<DetailsViewModel>(factory = DetailsViewModelFactory(movieId))
    val detailsUIModel = detailsViewModel.detailsUIState.collectAsState().value


    when (detailsUIModel) {
        DetailsViewModel.DetailsUIModel.Empty -> EmptyScreen()
        DetailsViewModel.DetailsUIModel.Loading -> LoadingScreen()
        DetailsViewModel.DetailsUIModel.NoConnection -> NoConnectionScreen()
        is DetailsViewModel.DetailsUIModel.Data -> DetailsContent(
            modifier = modifier,
            localMovie = detailsUIModel.localMovie,
            credits = detailsUIModel.credits,
            setVideoLink = setVideoLink,
            videoLink = detailsUIModel.videoLink,
            detailsViewModel = detailsViewModel,
            isFavorite = detailsUIModel.isFavorite,
            isWatchList = detailsUIModel.isWatchlist,
            averageRating = detailsUIModel.averageRating
        )
    }
}

@Composable
private fun MainContentLeftSide(
     localMovie: LocalMovie,
     detailsViewModel: DetailsViewModel
) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Box(
            modifier = Modifier
                .width(180.dp)
                .aspectRatio(2 / 3f)
                .clip(shape = RoundedCornerShape(30.dp))
                .background(Color.Gray)
        ) {
            AsyncImage(
                model = localMovie.posterPath,
                contentDescription = null,
                modifier = Modifier
                    .fillMaxSize()
                    .clip(shape = RoundedCornerShape(30.dp)),
                placeholder = ColorPainter(Color.Gray)
            )
        }
        Row {
            CreateStars(Modifier, detailsViewModel)
        }
    }
}
@OptIn(ExperimentalLayoutApi::class)
@Preview(showBackground = true, device = Devices.PIXEL)
@Composable
private fun GenreItemContainer(genres: List<Genre> = listOf(
    Genre(1, "Action"),
    Genre(1, "Action"),
    Genre(1, "Action"),
    Genre(1, "Action"),
    Genre(1, "Action"),
    Genre(1, "Action"),
    Genre(1, "Action"),
    Genre(1, "Action"),
    Genre(1, "Action"),
    Genre(1, "Action"),
    Genre(1, "Action"),
    Genre(1, "Action")
)) {
    Box(
        Modifier
            .wrapContentSize()
    ) {
        FlowRow(
            horizontalArrangement = Arrangement.spacedBy(6.dp),
            verticalArrangement = Arrangement.spacedBy(6.dp)
        ) {
            genres.forEach { genre ->
                GenreItem(genre)
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun GenreItem(genre: Genre = Genre(
    1,
    "Action"
)) {
    Box(
        Modifier
            .wrapContentSize()
            .background(Color.LightGray, shape = RoundedCornerShape(4.dp))
            .padding(8.dp)
    ) {
        Text(
            text = genre.name
        )
    }
}


@Composable
private fun MainContentRightSide(
    detailsViewModel: DetailsViewModel,
    isFavorite: Boolean,
    isWatchList: Boolean,
    averageRating: Double,
    localMovie: LocalMovie
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(6.dp, Alignment.CenterVertically),
        modifier = Modifier.padding(10.dp)
    ) {
        Text(
            text = localMovie.title,
            fontSize = 20.sp,
            textAlign = TextAlign.Center,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.fillMaxWidth()
        )

        GenreItemContainer(localMovie.genres)
        Text(
            text = localMovie.releaseDate.year.toString(),
            fontSize = 15.sp,
            textAlign = TextAlign.Center,
        )
        Row {
            val favIcon =
                if (isFavorite) Icons.Filled.Favorite else Icons.Outlined.FavoriteBorder
            var favoriteIcon by remember { mutableStateOf(favIcon) }

            val watchIcon =
                if (isWatchList) Icons.Filled.Bookmark else Icons.Outlined.BookmarkBorder
            var watchlistIcon by remember { mutableStateOf(watchIcon) }
            Icon(
                imageVector = favoriteIcon,
                contentDescription = "Favorite",
                modifier = Modifier
                    .padding(5.dp)
                    .size(40.dp)
                    .clickable(onClick = {
                        favoriteIcon =
                            if (favoriteIcon == Icons.Outlined.FavoriteBorder) {
                                Icons.Filled.Favorite
                            } else {
                                Icons.Outlined.FavoriteBorder
                            }
                        detailsViewModel.toggleFavorite(localMovie)
                    })
            )
            Icon(
                imageVector = watchlistIcon,
                contentDescription = "Watchlist",
                modifier = Modifier
                    .padding(5.dp)
                    .size(40.dp)
                    .clickable(onClick = {
                        watchlistIcon =
                            if (watchlistIcon == Icons.Outlined.BookmarkBorder) {
                                Icons.Filled.Bookmark
                            } else {
                                Icons.Outlined.BookmarkBorder
                            }
                        detailsViewModel.toggleWatchlist(localMovie)
                    })
            )
        }
        Spacer(
            modifier = Modifier
                .height(30.dp)
                .fillMaxWidth()
        )
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Icon(
                imageVector = Icons.Filled.Star,
                contentDescription = "Favorite",
                modifier = Modifier
                    .size(50.dp)
            )
            Text(
                text = String.format(Locale.getDefault(), "%.2f", averageRating),
                fontSize = 15.sp,
                textAlign = TextAlign.Center,
            )
        }
    }
}

@Composable
private fun Overview(content: String) {
    Text(
        text = "Overview",
        fontSize = 20.sp,
        fontWeight = FontWeight.Bold
    )
    Text(
        text = content
    )
}

@Composable
private fun DetailsContent(
    modifier: Modifier,
    localMovie: LocalMovie,
    credits: Credits,
    setVideoLink: (String?) -> Unit,
    videoLink: String? = null,
    detailsViewModel: DetailsViewModel,
    isFavorite: Boolean,
    isWatchList: Boolean,
    averageRating: Double,
) {

    LaunchedEffect(Unit) {
        setVideoLink(videoLink)
    }

    LazyColumn(
        modifier = modifier
            .padding(10.dp)
            .fillMaxSize(),
        verticalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        item {
            Row(verticalAlignment = Alignment.CenterVertically) {
                MainContentLeftSide(
                    localMovie = localMovie,
                    detailsViewModel = detailsViewModel
                )
                MainContentRightSide(
                    detailsViewModel = detailsViewModel,
                    isFavorite = isFavorite,
                    isWatchList = isWatchList,
                    averageRating = averageRating,
                    localMovie = localMovie
                )

            }
        }
        item {
            Overview(localMovie.overview ?: "No overview available")
            HorizontalDivider()
        }
        item {
            if (!localMovie.releaseDate.equals("")) {
                Text(
                    text = buildAnnotatedString {
                        withStyle(style = SpanStyle(fontWeight = FontWeight.Bold)) {
                            append("Release date: ")
                        }
                        append(localMovie.releaseDate.toString())
                    },
                    fontSize = 15.sp
                )
            }
        }
        item {
            if (localMovie.productionCompanies.isNotEmpty()) {
                Text(
                    text = buildAnnotatedString {
                        withStyle(style = SpanStyle(fontWeight = FontWeight.Bold)) {
                            append("Produced by: ")
                        }
                        append(localMovie.productionCompanies.joinToString { it.name })
                    },
                    fontSize = 15.sp
                )

            }
        }
        item {
            if (localMovie.productionCountries.isNotEmpty()) {
                Text(
                    text = buildAnnotatedString {
                        withStyle(style = SpanStyle(fontWeight = FontWeight.Bold)) {
                            append("Produced in: ")
                        }
                        append(localMovie.productionCountries.joinToString { it.name })
                    },
                    fontSize = 15.sp,
                )
            }
        }
        item {
            if (localMovie.revenue > 0) {
                Text(
                    text = buildAnnotatedString {
                        withStyle(style = SpanStyle(fontWeight = FontWeight.Bold)) {
                            append("Revenue generated: ")
                        }
                        append(localMovie.revenue.toString())
                    },
                    fontSize = 15.sp
                )
            }
        }
        item {
            localMovie.runtime?.let {
                if (it > 0) {
                    Text(
                        text = buildAnnotatedString {
                            withStyle(style = SpanStyle(fontWeight = FontWeight.Bold)) {
                                append("Runtime: ")
                            }
                            if (localMovie.runtime == 1) {
                                append("${localMovie.runtime} minute")
                            } else {
                                append("${localMovie.runtime} minutes")
                            }
                        },
                        fontSize = 15.sp
                    )
                }
            }
        }
        item {
            if (localMovie.spokenLanguages.isNotEmpty()) {
                Text(
                    text = buildAnnotatedString {
                        withStyle(style = SpanStyle(fontWeight = FontWeight.Bold)) {
                            append("Spoken languages: ")
                        }
                        append(localMovie.spokenLanguages.joinToString { it.name })
                    },
                    fontSize = 15.sp,
                )
            }
            HorizontalDivider()
        }
        item {
            if (credits.cast.isNotEmpty()) {
                Text(
                    text = "Cast",
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold
                )
                LazyRow(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                    items(credits.cast.size) {
                        Cast(Modifier, credits.cast[it])
                    }
                }
            }
            HorizontalDivider()
        }
        item {
            if (credits.crew.isNotEmpty()) {
                Text(
                    text = "Crew",
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold
                )

                LazyRow(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                    items(credits.crew.size) {
                        Crew(Modifier, credits.crew[it])
                    }
                }
            }
        }
    }
}

@Composable
private fun Overview(modifier: Modifier, text: String) {
    Text(text)
}

@Composable
private fun CreateStars(modifier: Modifier, detailsViewModel: DetailsViewModel) {
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
                            detailsViewModel.addRating(
                                detailsViewModel.movieId.toString(),
                                (i + 1).toDouble()
                            )
                        }
                    }),
            )
        }
    }
}

@Composable
private fun Cast(modifier: Modifier, cast: Cast) {
    Column(
        Modifier.fillMaxHeight(),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
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
private fun Crew(modifier: Modifier, crew: Crew) {
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