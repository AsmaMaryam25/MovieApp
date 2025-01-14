package com.example.blackbeard.screens.details

import android.icu.text.NumberFormat
import android.icu.util.Currency
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
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
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Bookmark
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.outlined.BookmarkBorder
import androidx.compose.material.icons.outlined.FavoriteBorder
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
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
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.zIndex
import androidx.lifecycle.LiveData
import androidx.lifecycle.viewmodel.compose.viewModel
import coil3.compose.AsyncImage
import com.example.blackbeard.R
import com.example.blackbeard.components.ExpandableText
import com.example.blackbeard.models.AgeRating
import com.example.blackbeard.models.Cast
import com.example.blackbeard.models.Credits
import com.example.blackbeard.models.Crew
import com.example.blackbeard.models.Genre
import com.example.blackbeard.models.LocalMovie
import com.example.blackbeard.models.ProductionCompany
import com.example.blackbeard.models.ProductionCountry
import com.example.blackbeard.models.SpokenLanguage
import com.example.blackbeard.models.StreamingService
import com.example.blackbeard.models.isBudgetInvalid
import com.example.blackbeard.models.isDetailsInvalid
import com.example.blackbeard.models.isProductionCompaniesInvalid
import com.example.blackbeard.models.isProductionCountriesInvalid
import com.example.blackbeard.models.isReleaseDateInvalid
import com.example.blackbeard.models.isRevenueInvalid
import com.example.blackbeard.models.isRuntimeInvalid
import com.example.blackbeard.models.isSpokenLanguagesInvalid
import com.example.blackbeard.screens.EmptyScreen
import com.example.blackbeard.screens.LoadingScreen
import com.example.blackbeard.screens.NoConnectionScreen
import com.example.blackbeard.utils.TimeUtils
import java.time.LocalDate
import java.util.Locale
import kotlin.math.floor

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
        is DetailsViewModel.DetailsUIModel.Data -> MainContent(
            localMovie = detailsUIModel.localMovie,
            credits = detailsUIModel.credits,
            setVideoLink = setVideoLink,
            videoLink = detailsUIModel.videoLink,
            isFavorite = detailsUIModel.isFavorite,
            isWatchList = detailsUIModel.isWatchlist,
            averageRating = detailsUIModel.averageRating,
            genres = detailsUIModel.localMovie.genres,
            ageRating = detailsUIModel.ageRating,
            streamingServices = detailsUIModel.streamingServices,
            onMovieRating = { rating ->
                detailsViewModel.addRating(
                    movieId.toString(),
                    rating,
                    detailsUIModel.installationID
                )
            },
            movieRating = detailsUIModel.movieRating,
            onWatchlistToggle = { detailsViewModel.toggleWatchlist(detailsUIModel.localMovie) },
            onFavoriteToggle = { detailsViewModel.toggleFavorite(detailsUIModel.localMovie) },
            voterCountLiveData = detailsViewModel.getVoterCount(
                movieId.toString(),
            )
        )
    }

}

@OptIn(ExperimentalFoundationApi::class)
@Composable
private fun MainContent(
    localMovie: LocalMovie,
    credits: Credits,
    setVideoLink: (String?) -> Unit,
    videoLink: String?,
    isFavorite: Boolean,
    isWatchList: Boolean,
    averageRating: Double,
    streamingServices: List<StreamingService>,
    ageRating: AgeRating,
    movieRating: Double?,
    genres: List<Genre>,
    onMovieRating: (Double) -> Unit,
    onWatchlistToggle: () -> Unit,
    onFavoriteToggle: () -> Unit,
    voterCountLiveData: LiveData<Int>
) {

    val voterCount by voterCountLiveData.observeAsState(0)

    var shouldBeSticky by remember { mutableStateOf(true) }

    val simpleContent = @Composable {
        SimpleContent(
            genres = genres,
            title = localMovie.originalTitle,
            overview = localMovie.overview,
            posterPath = localMovie.posterPath,
            ageRating = ageRating,
            onTextExpand = { shouldBeSticky = !shouldBeSticky }
        )
    }

    LaunchedEffect(Unit) {
        setVideoLink(videoLink)
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
    ) {
        BackgroundPoster(localMovie.posterPath)
        LazyColumn(
            modifier = Modifier.fillMaxSize()
        ) {
            if (shouldBeSticky) {
                stickyHeader {
                    simpleContent()
                }
            } else {
                item {
                    simpleContent()
                }
            }

            item {
                SecondaryContent(
                    localMovie = localMovie,
                    credits = credits,
                    averageRating = averageRating,
                    streamingServices = streamingServices,
                    onMovieRating = onMovieRating,
                    isFavorite = isFavorite,
                    isWatchList = isWatchList,
                    movieRating = movieRating,
                    onFavoriteToggle = onFavoriteToggle,
                    onBookmarkToggle = onWatchlistToggle,
                    voterCount = voterCount
                )
            }
        }
    }
}

@Composable
private fun SimpleContent(
    genres: List<Genre>,
    title: String,
    overview: String?,
    posterPath: String?,
    ageRating: AgeRating,
    onTextExpand: () -> Unit
) {
    Column(
        modifier = Modifier
            .padding(start = 20.dp, end = 20.dp, bottom = 20.dp)
            .fillMaxWidth()

    ) {
        Row(Modifier.fillMaxWidth()) {
            Spacer(modifier = Modifier.weight(.2f))
            MoviePoster(
                modifier = Modifier.weight(.6f),
                posterPath = posterPath
            )
            Spacer(modifier = Modifier.weight(.2f))
        }
        MovieTitle(
            Modifier
                .fillMaxWidth()
                .align(Alignment.CenterHorizontally)
                .padding(top = 20.dp, bottom = 10.dp),
            title = title
        )
        Row(
            modifier = Modifier
                .fillMaxWidth(),
            horizontalArrangement = Arrangement.Center,
            verticalAlignment = Alignment.CenterVertically
        ) {
            AgeRatingIcon(ageRating = ageRating)
            GenreItemContainer(genres)
        }
        if (overview.isNullOrEmpty()) {
            Text(
                text = "No overview available",
                modifier = Modifier.fillMaxWidth(),
                textAlign = TextAlign.Center,
                color = Color.White,
                style = MaterialTheme.typography.titleMedium
            )
            return@Column
        }
        ExpandableText(
            text = overview,
            collapsedMaxLine = 3,
            fontSize = 14.sp,
            style = TextStyle(color = Color.White),
            showLessText = "",
            onTextExpand = onTextExpand
        )
        /*
        CollapsibleBodyText(
            text = overview,
            onTextExpand = onTextExpand
        )
         */
    }
}

@Composable
private fun AgeRatingIcon(ageRating: AgeRating) {
    if (ageRating.imageResource == -1) return
    Image(
        modifier = Modifier.size(50.dp),
        painter = painterResource(ageRating.imageResource),
        contentDescription = "Age rating icon for ${ageRating.rating}"
    )
}

@Composable
private fun StreamingServicesSection(streamingServices: List<StreamingService>) {
    Column(
        verticalArrangement = Arrangement.spacedBy(6.dp)
    ) {
        Text(
            text = stringResource(id = R.string.watch_from_streaming_services),
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.Bold
        )

        if(streamingServices.isEmpty()) {
            Text(
                text = stringResource(id = R.string.there_are_no_streaming_services_available_for_this_title),
                style = MaterialTheme.typography.titleMedium
            )
            return@Column
        }

        LazyRow(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(6.dp, Alignment.Start),
            verticalAlignment = Alignment.CenterVertically
        ) {
            items(streamingServices) { streamingService ->
                AsyncImage(
                    model = streamingService.logoPath,
                    contentDescription = streamingService.providerName.plus(" logo")
                )
            }
        }
    }
}

@Composable
private fun SecondaryContent(
    localMovie: LocalMovie,
    credits: Credits,
    averageRating: Double,
    streamingServices: List<StreamingService>,
    isFavorite: Boolean,
    isWatchList: Boolean,
    movieRating: Double?,
    onMovieRating: (Double) -> Unit,
    onFavoriteToggle: () -> Unit,
    onBookmarkToggle: () -> Unit,
    voterCount: Int
) {
    val sections = listOf<@Composable () -> Unit>(
        { StreamingServicesSection(streamingServices) },
        {
            SaveAndBookmarkSection(
                isFavorite = isFavorite,
                isWatchList = isWatchList,
                onBookmarkToggle = onBookmarkToggle,
                onFavoriteToggle = onFavoriteToggle
            )
        },
        {
            MovieRatingSection(
                userRatings = voterCount,
                movieRating = movieRating,
                averageRating = averageRating,
                onMovieRating = onMovieRating
            )
        },
        { CastSection(credits.cast) },
        { CrewSection(credits.crew) },
        {
            MovieDetailsSection(
                runtime = localMovie.runtime,
                revenue = localMovie.revenue,
                releaseDate = localMovie.releaseDate,
                budget = localMovie.budget,
                spokenLanguages = localMovie.spokenLanguages,
                productionCountries = localMovie.productionCountries,
                productionCompanies = localMovie.productionCompanies
            )
        }
    )

    Column(
        Modifier
            .fillMaxWidth()
            .background(
                MaterialTheme.colorScheme.background,
                shape = RoundedCornerShape(topStart = 6.dp, topEnd = 6.dp)
            )
            .padding(top = 16.dp, start = 14.dp, end = 14.dp, bottom = 16.dp)
            .zIndex(1f)
            .pointerInput(Unit) {
                // Intercept touch events to prevent clicks from propagating
                detectTapGestures(onTap = { /* Do nothing */ })
            }
    ) {
        sections.forEachIndexed { index, section ->
            if (index > 0) {
                HorizontalDivider(Modifier.padding(vertical = 10.dp), color = Color.Black)
            }
            section()
        }
    }
}

@Composable
private fun SaveAndBookmarkSection(
    isFavorite: Boolean,
    isWatchList: Boolean,
    onFavoriteToggle: () -> Unit,
    onBookmarkToggle: () -> Unit
) {

    var isFavorited by remember { mutableStateOf(isFavorite) }
    var isWatchListed by remember { mutableStateOf(isWatchList) }

    Text(
        text = stringResource(id= R.string.favorite_and_Watchlist),
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
            text = stringResource(id= R.string.add_to_favorites_and_watchlist),
            style = MaterialTheme.typography.titleSmall,
            color = Color.DarkGray
        )

        Row(
            modifier = Modifier.weight(0.6f),
            horizontalArrangement = Arrangement.End
        ) {
            Icon(
                imageVector = if (isFavorited) Icons.Filled.Favorite else Icons.Outlined.FavoriteBorder,
                contentDescription = "Favorite",
                modifier = Modifier
                    .padding(5.dp)
                    .size(40.dp)
                    .clickable {
                        isFavorited = !isFavorited
                        onFavoriteToggle.invoke()
                    },
                tint = Color(0xFFA20321)
            )
            Icon(
                imageVector = if (isWatchListed) Icons.Filled.Bookmark else Icons.Outlined.BookmarkBorder,
                contentDescription = "Watchlist",
                modifier = Modifier
                    .padding(5.dp)
                    .size(40.dp)
                    .clickable {
                        isWatchListed = !isWatchListed
                        onBookmarkToggle.invoke()
                    },
                tint = Color(0xFF0000FF)
            )
        }
    }
}

@Composable
private fun CrewSection(crew: List<Crew>) {
    SecondaryContentSection(
        header = stringResource(id = R.string.crew)
    ) {
        if (crew.isEmpty()) {
            Text(
                text = stringResource(id = R.string.no_crew_available),
                modifier = Modifier.fillMaxWidth(),
                textAlign = TextAlign.Center,
                style = MaterialTheme.typography.titleMedium
            )
            return@SecondaryContentSection
        }
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
private fun MovieRatingSection(
    userRatings: Int,
    movieRating: Double?,
    averageRating: Double,
    onMovieRating: (Double) -> Unit
) {
    Row(
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column(
            Modifier.fillMaxWidth()
        ) {
            Text(
                text = stringResource(id = R.string.rating),
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
                    text = stringResource(id = R.string.give_opinion),
                    color = Color.DarkGray,
                    style = MaterialTheme.typography.titleSmall
                )
                Column(
                    modifier = Modifier.weight(1f),
                    horizontalAlignment = Alignment.End
                ) {
                    Text(
                        text = stringResource(id = R.string.users)+" ($userRatings)",
                        fontWeight = FontWeight.Bold,
                        color = Color.DarkGray
                    )
                    RatingStars(
                        movieRating = movieRating,
                        onMovieRating = onMovieRating
                    )
                    Text(
                        text = stringResource(id = R.string.average)+
                        ": " + String.format(Locale.US, "%.2f", averageRating),
                        fontWeight = FontWeight.Bold,
                        color = Color.DarkGray
                    )
                }
            }
        }
    }
}


@Composable
private fun SecondaryContentSection(
    modifier: Modifier = Modifier,
    header: String,
    content: @Composable () -> Unit
) {
    Text(
        header,
        style = MaterialTheme.typography.titleMedium,
        fontWeight = FontWeight.Bold
    )
    Spacer(Modifier.height(6.dp))
    Row(
        modifier = modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        content()
    }
}

@Composable
private fun CastSection(cast: List<Cast>) {
    SecondaryContentSection(
        header = stringResource(id = R.string.cast)
    ) {
        if (cast.isEmpty()) {
            Text(
                text = stringResource(id = R.string.no_cast_available),
                modifier = Modifier.fillMaxWidth(),
                textAlign = TextAlign.Center,
                style = MaterialTheme.typography.titleMedium
            )
            return@SecondaryContentSection
        }
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
    releaseDate: LocalDate,
    runtime: Int?,
    revenue: Long,
    budget: Long,
    productionCompanies: List<ProductionCompany>,
    productionCountries: List<ProductionCountry>,
    spokenLanguages: List<SpokenLanguage>,
) {
    SecondaryContentSection(
        header = "Details"
    ) {
        val format: NumberFormat = NumberFormat.getCurrencyInstance()
        format.maximumFractionDigits = 0
        format.currency = Currency.getInstance("USD")

        // No detail data available
        if (isDetailsInvalid(
                releaseDate = releaseDate,
                spokenLanguages = spokenLanguages,
                productionCountries = productionCountries,
                productionCompanies = productionCompanies,
                budget = budget,
                revenue = revenue,
                runtime = runtime,
            )
        ) {
            Text(
                text = stringResource(id = R.string.no_details_available),
                modifier = Modifier.fillMaxWidth(),
                textAlign = TextAlign.Center,
                style = MaterialTheme.typography.titleMedium
            )
            return@SecondaryContentSection
        }

        val details = listOf<@Composable () -> Unit>(
            {
                if (!isReleaseDateInvalid(releaseDate)) MovieDetailSingleLine(
                    "Release Date",
                    releaseDate.toString()
                )
            },
            {
                if (!isRuntimeInvalid(runtime)) MovieDetailSingleLine(
                    "Runtime",
                    TimeUtils.convertMinutesToHoursAndMinutes(runtime ?: 0)
                )
            },
            {
                if (!isRevenueInvalid(revenue)) MovieDetailSingleLine(
                    "Revenue",
                    format.format(revenue)
                )
            },
            {
                if (!isBudgetInvalid(budget)) MovieDetailSingleLine(
                    "Budget",
                    format.format(budget)
                )
            },
            {
                if (!isProductionCompaniesInvalid(productionCompanies)) MovieDetailMultiLine(
                    "Production Company",
                    "Production Companies",
                    productionCompanies.map { it.name })
            },
            {
                if (!isProductionCountriesInvalid(productionCountries)) MovieDetailMultiLine(
                    "Production Country",
                    "Production Countries",
                    productionCountries.map { it.name })
            },
            {
                if (!isSpokenLanguagesInvalid(spokenLanguages)) MovieDetailMultiLine(
                    "Language",
                    "Languages",
                    spokenLanguages.map { it.englishName })
            },

            )

        Column(
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            details.forEach { movieDetail ->
                movieDetail()
            }
        }
    }
}

@Composable
private fun MovieDetailSingleLine(header: String, data: String) {
    Row(
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(
            text = header,
            style = MaterialTheme.typography.titleSmall
        )
        Spacer(modifier = Modifier.weight(1f))
        Text(
            text = data,
            textAlign = TextAlign.Right,
            style = MaterialTheme.typography.titleSmall
        )
    }
}

@Composable
private fun MovieDetailMultiLine(
    singleItemHeader: String,
    multiItemHeader: String,
    data: List<String>
) {
    Row(
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(
            text = if (data.size > 1) multiItemHeader else singleItemHeader,
            style = MaterialTheme.typography.titleSmall
        )
        Spacer(modifier = Modifier.weight(1f))
        Text(
            text = data.joinToString("\n"),
            textAlign = TextAlign.Right,
            style = MaterialTheme.typography.titleSmall
        )
    }
}

@Composable
private fun MovieTitle(
    modifier: Modifier = Modifier,
    title: String
) {
    Box(Modifier.wrapContentSize()) {
        Text(
            modifier = modifier,
            text = title.ifEmpty { "Title not available" },
            textAlign = TextAlign.Center,
            fontWeight = FontWeight.Bold,
            fontSize = 25.sp,
            color = Color.White
        )
    }

}

@Composable
private fun BackgroundPoster(posterPath: String? = "") {
    Box(modifier = Modifier.fillMaxSize()) {
        AsyncImage(
            model = posterPath,
            contentDescription = "Darkened Background Poster",
            modifier = Modifier.fillMaxSize(),
            contentScale = ContentScale.FillBounds,
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
private fun MoviePoster(
    modifier: Modifier = Modifier,
    posterPath: String?
) {
    Box(
        modifier = modifier
            .aspectRatio(2 / 3f)
            .clip(shape = RoundedCornerShape(10.dp))
            .background(Color.Gray)

    ) {
        AsyncImage(
            modifier = Modifier.fillMaxSize(),
            model = posterPath,
            contentDescription = "Movie Poster",
            contentScale = ContentScale.Crop,
        )
    }
}

// Credit: https://medium.com/@manuyadav644/jetpack-compose-conditionally-display-read-more-button-based-on-text-lines-c9c0c5f65556
@Composable
private fun CollapsibleBodyText(
    text: String?,
    onTextExpand: () -> Unit
) {
    val readMore = "... Read more"
    val minimumLineLength = 3
    var expandedState by remember { mutableStateOf(false) }
    var showReadMoreButtonState by remember { mutableStateOf(false) }
    val maxLines = if (expandedState) 200 else minimumLineLength

    Column(
        modifier = Modifier
            .fillMaxHeight()
            .padding(start = 20.dp, end = 20.dp)
            .clickable {
                //onTextExpand.invoke()
                expandedState = !expandedState
            }
    ) {
        if (text.isNullOrEmpty()) {
            Text(
                text = "No overview available",
                modifier = Modifier.fillMaxWidth(),
                textAlign = TextAlign.Center,
                color = Color.White,
                style = MaterialTheme.typography.titleMedium
            )
            return@Column
        }
        Text(
            text = if (!showReadMoreButtonState) text.dropLast(readMore.length)
                .plus(readMore) else text,
            textAlign = TextAlign.Center,
            color = Color.White,
            overflow = TextOverflow.Clip,
            maxLines = maxLines,
            onTextLayout = { textLayoutResult ->
                if (textLayoutResult.lineCount > minimumLineLength - 1) {
                    if (textLayoutResult.hasVisualOverflow) {
                        showReadMoreButtonState = true
                    }
                }
            }
        )
    }
}

@OptIn(ExperimentalLayoutApi::class)
@Composable
private fun GenreItemContainer(genres: List<Genre>) {

    LazyRow(
        modifier = Modifier.padding(vertical = 10.dp).fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(5.dp, Alignment.CenterHorizontally),
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
            .background(Color.Gray, shape = RoundedCornerShape(4.dp))
            .padding(horizontal = 8.dp, vertical = 2.dp)
            .wrapContentSize()
    ) {
        Text(
            text = genre.name,
            color = Color.White,
            fontSize = 15.sp,
        )
    }
}

@Composable
private fun RatingStars(
    modifier: Modifier = Modifier,
    movieRating: Double?,
    onMovieRating: (Double) -> Unit
) {
    val movieRatingFloor = floor(movieRating ?: 0.0).toInt()

    val iconList = remember {
        mutableStateListOf<Int>().apply {
            repeat(movieRatingFloor) {
                add(R.drawable.chest_open)
            }
            repeat(5 - movieRatingFloor) {
                add(R.drawable.chest_closed)
            }
        }
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
            .height(220.dp),
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
                    .clip(shape = RoundedCornerShape(12.dp)),
                placeholder = ColorPainter(Color.Gray)
            )
        }
        Text(
            text = name,
            fontSize = 15.sp,
            textAlign = TextAlign.Center,
            modifier = modifier.fillMaxWidth(),
            maxLines = 2,
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
