package com.example.movieapp.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import dk.shape.dtu.navigation.R


@Composable
fun HomeScreen(onNavigateToFavoriteScreen: (String) -> Unit) {
    Column(
        modifier = Modifier.fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            modifier = Modifier.padding(10.dp),
            text = "Now Playing",
            fontSize = 30.sp,
            fontWeight = FontWeight.Bold
        )
        Column (
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Image(
                painter = painterResource(id = R.drawable.ygo),
                contentDescription = "Movie Poster 1",
                modifier = Modifier.clip(shape = RoundedCornerShape(30.dp))
            )
            Text(
                modifier = Modifier.padding(start = 35.dp, top = 15.dp, end = 35.dp),
                text = "Yu-Gi-Oh!: The Dark Side of Dimensions",
                style = TextStyle(
                    fontSize = 30.sp,
                    lineHeight = 30.sp,
                    textAlign = TextAlign.Center
                ),
                fontWeight = FontWeight.Bold
            )
        }
    }
}
