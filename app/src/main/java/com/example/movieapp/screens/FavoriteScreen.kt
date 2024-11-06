package com.example.movieapp.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Reorder
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
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
fun FavoriteScreen() {
    val posterWidth = 140.dp
    LazyColumn {
        items(20) {
            CreateFavCard(posterWidth)
            HorizontalDivider(
                modifier = Modifier.fillMaxWidth().padding(vertical = 20.dp)
            )
        }
    }
}

@Composable
fun CreateFavCard(posterWidth: Dp) {
    Row(
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Image(
            painter = painterResource(id = R.drawable.ygo),
            contentDescription = "Movie Poster 1",
            modifier = Modifier
                .width(posterWidth)
                .clip(shape = RoundedCornerShape(20.dp))
        )
        Text(
            text = "Yu-Gi-Oh!: The Dark Side of Dimensions",
            style = TextStyle(
                fontSize = 25.sp,
                lineHeight = 30.sp,
                textAlign = TextAlign.Center
            ),
            fontWeight = FontWeight.Bold,
            modifier = Modifier
                .padding(vertical = 40.dp, horizontal = 10.dp)
                .weight(2f)
        )
        Icon(
            imageVector = Icons.Default.Reorder,
            contentDescription = "Reorder",
            modifier = Modifier
                .size(24.dp)
                .weight(0.5f)
        )
    }
}