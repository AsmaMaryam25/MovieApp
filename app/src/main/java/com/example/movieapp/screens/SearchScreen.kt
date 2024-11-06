package com.example.movieapp.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import dk.shape.dtu.navigation.R

@Composable
fun SearchScreen() {
    val posterWidth = 170.dp
    LazyColumn (
        modifier = Modifier.fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally
    )
    {
        items(10) {
            Row{
                CreateSearchPoster(posterWidth)
                CreateSearchPoster(posterWidth)
            }
        }
    }
}

@Composable
private fun CreateSearchPoster(posterWidth: Dp) {
    Column (
        modifier = Modifier.padding(10.dp)
    ){
        Image(
            painter = painterResource(id = R.drawable.ygo),
            contentDescription = "Movie Poster 1",
            modifier = Modifier
                .width(posterWidth)
                .clip(shape = RoundedCornerShape(20.dp))
        )
        Text(
            modifier = Modifier.width(posterWidth),
            text = "Yu-Gi-Oh!: The Dark Side of Dimensions",
            textAlign = TextAlign.Center,
            fontWeight = FontWeight.Bold,
            lineHeight = 15.sp
        )
    }
}
