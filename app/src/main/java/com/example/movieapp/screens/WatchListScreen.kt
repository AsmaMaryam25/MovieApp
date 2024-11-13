package com.example.movieapp.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
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

@Composable
fun WatchlistScreen(modifier: Modifier = Modifier, onNavigateToDetailsScreen: (String) -> Unit) {
    val posterWidth = 140.dp
    LazyColumn(
        modifier = modifier.fillMaxSize(),
    ) {
        items(20) {
            CreateWatchlistCard(posterWidth, onNavigateToDetailsScreen = onNavigateToDetailsScreen)
            HorizontalDivider(
                modifier = modifier
                    .fillMaxWidth()
                    .padding(vertical = 20.dp)
            )
        }
    }
}

@Composable
fun CreateWatchlistCard(
    posterWidth: Dp,
    modifier: Modifier = Modifier,
    onNavigateToDetailsScreen: (String) -> Unit
) {
    Row(
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Image(
            painter = painterResource(id = R.drawable.ygo),
            contentDescription = "Movie Poster 1",
            modifier = modifier
                .width(posterWidth)
                .clip(shape = RoundedCornerShape(20.dp))
                .clickable { onNavigateToDetailsScreen("Yu-Gi-Oh!: The Dark Side of Dimensions") }
        )
        Text(
            text = "Yu-Gi-Oh!: The Dark Side of Dimensions",
            style = TextStyle(
                fontSize = 25.sp,
                lineHeight = 30.sp,
                textAlign = TextAlign.Center
            ),
            fontWeight = FontWeight.Bold,
            modifier = modifier
                .padding(top = 40.dp, start = 10.dp, end = 64.dp, bottom = 40.dp)
                .weight(2f)
                .clickable { onNavigateToDetailsScreen("Yu-Gi-Oh!: The Dark Side of Dimensions") }
        )
    }
}

