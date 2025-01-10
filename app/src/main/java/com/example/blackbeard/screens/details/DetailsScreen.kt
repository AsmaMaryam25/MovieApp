package com.example.blackbeard.screens.details

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.Image
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
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Bookmark
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.outlined.StarOutline
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
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
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.painter.ColorPainter
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Devices
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.zIndex
import androidx.lifecycle.viewmodel.compose.viewModel
import coil3.compose.AsyncImage
import com.example.blackbeard.R
import com.example.blackbeard.models.Cast
import com.example.blackbeard.models.Crew
import com.example.blackbeard.models.Genre

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

    /*
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
            isFavorite = detailsUIModel.isFavorite,
            isWatchList = detailsUIModel.isWatchlist,
            averageRating = detailsUIModel.averageRating,
            onMovieRating = 
        )
    }
     */
}

@Preview
@OptIn(ExperimentalFoundationApi::class)
@Composable
private fun MainContent() {

    val shouldBeSticky by remember { mutableStateOf(true) }

    Box(
        modifier = Modifier
            .fillMaxSize()
    ) {
        BackgroundPoster("")
        LazyColumn(
            modifier = Modifier.fillMaxSize()
        ) {
            if(shouldBeSticky) {
                stickyHeader{
                    SimpleContent()
                }
            } else {
                item {
                    SimpleContent()
                }
            }

            item {
                SecondaryContent()
            }
        }
    }
}

@Composable
private fun SimpleContent() {
    Column(
        modifier = Modifier
            .padding(start = 20.dp, end = 20.dp, bottom = 20.dp)

    ) {
        Spacer(Modifier.height(50.dp))
        Row(Modifier.fillMaxWidth()) {
            Spacer(modifier = Modifier.weight(.2f))
            MoviePoster(
                modifier = Modifier.weight(.6f),
                posterPath = ""
            )
            Spacer(modifier = Modifier.weight(.2f))
        }
        MovieTitle(
            Modifier
                .fillMaxWidth()
                .align(Alignment.CenterHorizontally)
                .padding(top = 20.dp, bottom = 10.dp)

        )
        GenreItemContainer(
            listOf(
                Genre(1, "Action"),
                Genre(1, "Action"),
                Genre(1, "Action"),
                Genre(1, "Action"),
                Genre(1, "Action"),
                Genre(1, "Action"),
                Genre(1, "Action"),
                Genre(1, "Action"),
                Genre(1, "Action"),
            )
        )
        CollapsibleBodyText(
            text = """
                    Lorem ipsum dolor sit amet, consectetur adipiscing elit, sed do eiusmod tempor incididunt ut labore et dolore magna aliqua. Ut enim ad minim veniam, quis nostrud exercitation ullamco laboris nisi ut aliquip ex ea commodo consequat. Duis aute irure dolor in reprehenderit in voluptate velit esse cillum dolore eu fugiat nulla pariatur. Excepteur sint occaecat cupidatat non proident, sunt in culpa qui officia deserunt mollit anim id est laborum.
                """.trimIndent(),
            maxLines = 3,
            onReadMoreClick = {},
            readMoreText = " Read More"
        )
    }
}

@Composable
private fun TrailerButton() {

}

@Composable
private fun SecondaryContent() {
    Column (
        Modifier
            .fillMaxWidth()
            .background(MaterialTheme.colorScheme.background, shape = RoundedCornerShape(6.dp))
            .padding(top = 16.dp, start = 14.dp, end = 14.dp, bottom = 16.dp)
            .zIndex(1f)
    ) {
        SaveAndBookmarkSection()
        HorizontalDivider(Modifier.padding(vertical = 10.dp), color = Color.Black)
        MovieRatingSection(userRatings = 159, averageRating = 4.8, onMovieRating = {})
        HorizontalDivider(Modifier.padding(vertical = 10.dp), color = Color.Black)
        CastSection()
        HorizontalDivider(Modifier.padding(vertical = 10.dp), color = Color.Black)
        CrewSection()
        HorizontalDivider(Modifier.padding(vertical = 10.dp), color = Color.Black)
        MovieDetailsSection(40000000)
    }
}

@Composable
private fun SaveAndBookmarkSection() {
    Text(
        text = "Favorite and Watchlist",
        style = MaterialTheme.typography.titleMedium,
        fontWeight = FontWeight.Bold
    )
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {

        Text(
            modifier = Modifier.weight(0.4f),
            text = "Add the movie to your favorites and watchlist",
            style = MaterialTheme.typography.titleSmall,
            color = Color.DarkGray
        )

        Row(
            modifier = Modifier.weight(0.6f),
            horizontalArrangement = Arrangement.End
        ) {
            Icon(
                imageVector = Icons.Filled.Favorite,
                contentDescription = "Favorite",
                modifier = Modifier
                    .padding(5.dp)
                    .size(40.dp),
                tint = Color(0xFFA20321)
            )
            Icon(
                imageVector = Icons.Filled.Bookmark,
                contentDescription = "Watchlist",
                modifier = Modifier
                    .padding(5.dp)
                    .size(40.dp),
                tint = Color(0xFF0000FF)
            )
        }
    }
}

@Composable
private fun CrewSection(crew: List<Crew> = List(10) {
    Crew(it, "Name $it", it.toDouble(), null, "Department $it", "Job $it")
}) {
    SecondaryContentSection(
        header = "Crew"
    ) {
        LazyRow(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
            items(crew) { crewMember ->
                PersonPoster(
                    name = crewMember.name,
                    description = crewMember.job,
                    profilePath = crewMember.profilePath
                )
            }
        }
    }
}

@Composable
private fun MovieRatingSection(userRatings: Int, averageRating: Double, onMovieRating: (Double) -> Unit) {
    Row(
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column(
            Modifier.fillMaxWidth()
        ) {
            Text(
                text = "Rating",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold
            )
            Row(
                Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            )
            {
                Text(
                    modifier = Modifier.weight(0.4f),
                    text = "Give your opinion on the movie rating it",
                    color = Color.DarkGray,
                    style = MaterialTheme.typography.titleSmall
                )
                Column(
                    modifier = Modifier
                        .weight(0.7f),
                    horizontalAlignment = Alignment.End
                ) {
                    RatingStars(onMovieRating = {})
                    Text(
                        text = "Users ($userRatings)",
                        fontWeight = FontWeight.Bold,
                        color = Color.DarkGray
                    )
                    Text(
                        text = "Average rating is $averageRating",
                        fontWeight = FontWeight.Bold,
                        color = Color.DarkGray
                    )
                }
            }
        }
    }
}


@Composable
private fun SecondaryContentSection(modifier: Modifier = Modifier, header: String, content: @Composable () -> Unit) {
    Text(
        header,
        style = MaterialTheme.typography.titleMedium,
        fontWeight = FontWeight.Bold
    )
    Row(
        modifier = modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        content()
    }
}

@Composable
private fun CastSection(cast: List<Cast> =
        List(10) {
            Cast(it, "Name $it", "Original Name $it", it.toDouble(), null, "Character $it", it)
        }
) {
    SecondaryContentSection(
        header = "Cast"
    ) {
        LazyRow(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
            items(cast) { castMember ->
                PersonPoster(
                    name = castMember.name,
                    description = castMember.character,
                    profilePath = castMember.profilePath
                )
            }
        }
    }


}

@Composable
private fun MovieDetailsSection(
    revenue: Int
) {
    SecondaryContentSection(
        header = "Details"
    ) {
        Text(text = "Revenue")
        Text(text = revenue.toString())
    }

}

@Composable
private fun MovieTitle(modifier: Modifier = Modifier) {
    Box(Modifier.wrapContentSize()) {
        Text(
            modifier = modifier,
            text = "Test Movie",
            textAlign = TextAlign.Center,
            fontWeight = FontWeight.Bold,
            fontSize = 20.sp,
            color = Color.White
        )
    }

}

@Composable
private fun BackgroundPoster(posterPath: String? = "") {
    Box(modifier = Modifier.fillMaxSize()) {
        Image(
            painter = painterResource(id = R.drawable.no_connection),
            contentDescription = "Darkened Image",
            modifier = Modifier.fillMaxSize(),
            contentScale = ContentScale.Fit,
        )
        Box(
            modifier = Modifier
                .matchParentSize()
                .background(
                    brush = Brush.verticalGradient(
                        colors = listOf(Color.Transparent, Color.Black),
                        startY = 0f,
                        endY = 1200f
                    )
                )
        )
    }
}

@Composable
private fun MoviePoster(modifier: Modifier = Modifier,
                        posterPath: String?) {
    Box(
        modifier = modifier
            .aspectRatio(2 / 3f)
            .clip(shape = RoundedCornerShape(10.dp))
            .background(Color.Gray)

    ) {
        Image(
            modifier = Modifier.fillMaxSize(),
            painter = painterResource(id = R.drawable.foreground),
            contentDescription = "Darkened Image",
            contentScale = ContentScale.Crop,
        )
    }
}

@Composable
private fun CollapsibleBodyText(modifier: Modifier = Modifier,
                                text: String,
                                onReadMoreClick: () -> Unit,
                                readMoreText: String = " Read more",
                                maxLines: Int = 3,
) {
    var height by remember { mutableStateOf(50.dp) }
    var expandedState by remember { mutableStateOf(false) }
    val minimumLineLength = 2   //Change this to your desired value
    var showReadMoreButtonState by remember { mutableStateOf(false) }
    val maxLines = if (expandedState) 200 else minimumLineLength

    Box(
        modifier = modifier.height(height)
    ) {
        Text(
            modifier = Modifier.clickable {
                height = if (height.value == 50.dp.value) 300.dp else 50.dp
            },
            text = text,
            textAlign = TextAlign.Center,
            color = Color.White,
            overflow = TextOverflow.Ellipsis,
            onTextLayout = { textLayoutResult ->
                if (textLayoutResult.lineCount > minimumLineLength-1) {           //Adding this check to avoid ArrayIndexOutOfBounds Exception
                    if (textLayoutResult.isLineEllipsized(minimumLineLength-1)) {
                        showReadMoreButtonState = true
                    }
                }
            }
        )
        if (showReadMoreButtonState) {
            Text(
                text = if (expandedState) "Read Less" else "Read More",
                color = Color.Gray,

                modifier = Modifier.clickable {
                    expandedState = !expandedState
                },

            )
        }
    }
}

@Composable
private fun GenreItemContainer(genres: List<Genre>) {

    LazyRow(
        modifier = Modifier
            .padding(vertical = 10.dp),
        horizontalArrangement = Arrangement.spacedBy(5.dp)
    ) {
        items(genres) { genre ->
            GenreItem(genre)
        }
    }
}

@Composable
private fun GenreItem(genre: Genre) {
    Box(
        Modifier
            .wrapContentSize()
            .background(Color.Gray, shape = RoundedCornerShape(4.dp))
            .padding(4.dp)
    ) {
        Text(
            text = genre.name,
            color = Color.White
        )
    }
}

@Composable
private fun RatingStars(modifier: Modifier = Modifier, onMovieRating: (Double) -> Unit) {
    val iconList = remember {
        mutableStateListOf (
            R.drawable.chest_closed,
            R.drawable.chest_closed,
            R.drawable.chest_closed,
            R.drawable.chest_closed,
            R.drawable.chest_closed
        )
    }
    Row {
        for (i in 0 until 5) {
            Icon(
                painter = painterResource(iconList[i]),
                contentDescription = "Star Rating ${i + 1}",
                modifier = modifier
                    .size(30.dp)
                    .clickable(onClick = {
                        for (j in 0 until 5) {
                            if (iconList[i] == R.drawable.chest_closed) {
                                iconList[j] = R.drawable.chest_open
                            } else if (j > i) {
                                iconList[j] = R.drawable.chest_closed
                            }
                            onMovieRating.invoke((i + 1).toDouble())
                        }
                    }),
                tint = Color.Unspecified
            )
        }
    }
}

@Composable
private fun PersonPoster(
    modifier: Modifier = Modifier,
    name: String,
    profilePath: String?,
    description: String
    ) {
    Column(
        Modifier
            .width(80.dp)
            .height(180.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Box(
            modifier = Modifier
                .aspectRatio(2 / 3f)
                .clip(shape = RoundedCornerShape(12.dp))
                .background(Color.Gray)
        ) {
            AsyncImage(
                model = profilePath,
                contentDescription = null,
                modifier = Modifier
                    .fillMaxSize()
                    .clip(shape = RoundedCornerShape(30.dp)),
                placeholder = ColorPainter(Color.Gray)
            )
        }
        Text(
            text = name,
            fontSize = 15.sp,
            textAlign = TextAlign.Center,
            modifier = modifier.fillMaxWidth(),
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
            fontWeight = FontWeight.Bold
        )
        Text(
            text = description,
            fontSize = 15.sp,
            textAlign = TextAlign.Center,
            maxLines = 2,
            overflow = TextOverflow.Ellipsis,
            modifier = modifier.fillMaxWidth(),
        )
    }
}
