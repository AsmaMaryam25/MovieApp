package com.example.movieapp.screens

import androidx.compose.foundation.Image
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
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import dk.shape.dtu.navigation.R
import kotlinx.coroutines.launch


@Composable
fun HomeScreen(onNavigateToDetailsScreen: (String) -> Unit, modifier: Modifier = Modifier) {

    LazyColumn(
        modifier = modifier.fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {

        item {
            TitleText("Now Playing")
        }

        item {
            CreatePosters(onNavigateToDetailsScreen)
        }

        item {
            TitleText("Popular")
        }

        item {
            CreatePosters(onNavigateToDetailsScreen)
        }

        item {
            TitleText("Top Rated")
        }

        item {
            CreatePosters(onNavigateToDetailsScreen)
        }

        item {
            TitleText("Upcoming")
        }

        item {
            CreatePosters(onNavigateToDetailsScreen)
        }
    }
}

@Composable
private fun CreatePoster(
    onNavigateToDetailsScreen: (String) -> Unit,
    posterWidth: Dp = 300.dp,
    modifier: Modifier = Modifier
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Image(
            painter = painterResource(id = R.drawable.ygo),
            contentDescription = "Movie Poster 1",
            modifier = modifier
                .width(posterWidth)
                .clip(shape = RoundedCornerShape(30.dp))
                .clickable(onClick = { onNavigateToDetailsScreen("Yu-Gi-Oh!: The Dark Side of Dimensions") })
        )
        Text(
            modifier = modifier
                .width(posterWidth)
                .padding(start = 35.dp, top = 15.dp, end = 35.dp),
            text = "Yu-Gi-Oh!: The Dark Side of Dimensions",
            style = TextStyle(
                fontSize = 25.sp,
                lineHeight = 30.sp,
                textAlign = TextAlign.Center
            ),
            fontWeight = FontWeight.Bold
        )
        Text(
            modifier = modifier
                .width(posterWidth)
                .padding(vertical = 5.dp),
            text = "Animation, Adventure, Drama, Fantasy",
            style = TextStyle(
                textAlign = TextAlign.Center
            ),
        )
        Text(
            modifier = modifier
                .width(posterWidth),
            text = "2016",
            style = TextStyle(
                textAlign = TextAlign.Center
            ),
        )
        Text(
            modifier = modifier
                .width(posterWidth)
                .padding(bottom = 10.dp),
            text = "Yugi once more must Duel to save the world. Only this time, he must do so " +
                    "without the Pharoah. Kaiba's obsession with trying to find a way to settle " +
                    "the score with the Pharoah sets off a chain reaction, drawing in the " +
                    "mysterious Diva. What does this stranger want with Yugi? And what is " +
                    "the mysterious cube he carries?",
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
fun CreatePosters(onNavigateToDetailsScreen: (String) -> Unit, modifier: Modifier = Modifier) {
    val coroutineScope = rememberCoroutineScope()
    val rowState = rememberLazyListState()
    val snapBehavior = rememberSnapFlingBehavior(lazyListState = rowState)

    LazyRow(
        state = rowState,
        horizontalArrangement = Arrangement.spacedBy(20.dp),
        flingBehavior = snapBehavior,
        contentPadding = PaddingValues(start = 60.dp, end = 60.dp)
    ) {
        items(6) {
            CreatePoster(onNavigateToDetailsScreen)
        }
    }

    LaunchedEffect(rowState) {
        coroutineScope.launch {
            rowState.scrollToItem(3) // Assuming 6 items, center is at index 3
        }
    }
}