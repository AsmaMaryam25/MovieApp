package com.example.movieapp.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
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
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import dk.shape.dtu.navigation.R

@Composable
fun DetailsScreen(modifier: Modifier = Modifier, movieId: String, showTopBar: () -> Unit) {
    showTopBar()
    LazyColumn(
        modifier = modifier
            .padding(10.dp)
            .fillMaxSize(),
        verticalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        item {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Image(
                        painter = painterResource(id = R.drawable.ygo),
                        contentDescription = "Movie Poster 1",
                        modifier = Modifier
                            .width(180.dp)
                            .clip(shape = RoundedCornerShape(20.dp))
                    )
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
                        text = movieId,
                        fontSize = 20.sp,
                        textAlign = TextAlign.Center,
                        fontWeight = FontWeight.Bold,
                    )
                    Text(
                        text = "Animation, Adventure, Drama, Fantasy",
                        fontSize = 15.sp,
                        textAlign = TextAlign.Center,
                    )
                    Text(
                        text = "2016",
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
                }
            }
        }
        item {
            Text(
                text = "Yugi once more must Duel to save the world. Only this time, he must do so " +
                        "without the Pharoah. Kaiba's obsession with trying to find a way to settle " +
                        "the score with the Pharoah sets off a chain reaction, drawing in the " +
                        "mysterious Diva. What does this stranger want with Yugi? And what is " +
                        "the mysterious cube he carries?"
            )
        }
        item {
            Text(
                text = "Release date: April 23, 2016",
                fontSize = 15.sp
            )
        }
        item {
            Text(
                text = "Director: Satoshi Kuwabara",
                fontSize = 15.sp
            )
        }
        item {
            Text(
                text = "Distributed by: Toei Company",
                fontSize = 15.sp,
            )
        }
        item {
            Text(
                text = "Box office: $1.5 million",
                fontSize = 15.sp
            )
        }
        item {
            Text(
                text = "Cinematography: Hiroaki Edamitsu",
                fontSize = 15.sp
            )
        }
        item {
            Text(
                text = "Actors",
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold
            )
        }
        item {
            LazyRow(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                items(5) {
                    CreateActor(Modifier)
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
                    })
            )
        }
    }
}

@Composable
private fun CreateActor(modifier: Modifier) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Image(
            painter = painterResource(id = R.drawable.shunsukekazama),
            contentDescription = "Shunsuke Kazama image",
            modifier = modifier
                .width(100.dp)
                .clip(shape = RoundedCornerShape(20.dp))
        )
        Text(
            text = "Shunsuke Kazama",
            fontSize = 15.sp,
            textAlign = TextAlign.Center,
            modifier = modifier.width(100.dp),
            fontWeight = FontWeight.Bold
        )
        Text(
            text = "Yugi Muto(voice)",
            fontSize = 15.sp,
            textAlign = TextAlign.Center,
            modifier = modifier.width(100.dp),
        )
    }
}