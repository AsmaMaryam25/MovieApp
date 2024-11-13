package com.example.movieapp.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.SearchBar
import com.example.movieapp.components.SearchBar
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
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
fun SearchScreen(
    modifier: Modifier = Modifier,
    onNavigateToAdvancedSearchScreen: (String) -> Unit,
    onNavigateToDetailsScreen: (String) -> Unit
) {
    val posterWidth = 170.dp
    //TODO add search bar
    val searchQuery = remember { mutableStateOf("") }

    Column(
        modifier = modifier.fillMaxSize()
    ) {
        SearchBar(
            searchQuery = searchQuery,
            onSearchQueryChange = { query ->
                //TODO Handle search
            },
            onClickMenu = { onNavigateToAdvancedSearchScreen("Advanced Search") }
        )

        LazyColumn(
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            items(10) {
                Row(
                    horizontalArrangement = Arrangement.Center,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    CreateSearchPoster(posterWidth, onNavigateToDetailsScreen = onNavigateToDetailsScreen)
                    CreateSearchPoster(posterWidth, onNavigateToDetailsScreen = onNavigateToDetailsScreen)
                }
            }
        }
    }
}

@Composable
private fun CreateSearchPoster(
    posterWidth: Dp,
    modifier: Modifier = Modifier,
    onNavigateToDetailsScreen: (String) -> Unit
) {
    Column(
        modifier = modifier.padding(10.dp)
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
            modifier = modifier
                .width(posterWidth)
                .clickable { onNavigateToDetailsScreen("Yu-Gi-Oh!: The Dark Side of Dimensions") },
            text = "Yu-Gi-Oh!: The Dark Side of Dimensions",
            textAlign = TextAlign.Center,
            fontWeight = FontWeight.Bold,
            lineHeight = 15.sp
        )
    }
}
