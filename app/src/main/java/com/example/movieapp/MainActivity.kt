package com.example.movieapp

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import com.example.movieapp.ui.theme.MovieAppTheme
import androidx.navigation.compose.rememberNavController
import com.example.movieapp.components.navigation.BottomNavigationBar
import com.example.movieapp.components.navigation.BottomNavItem
import com.example.movieapp.components.navigation.NavigationGraph
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.outlined.List
import androidx.compose.material.icons.outlined.Favorite
import androidx.compose.material.icons.outlined.Home
import androidx.compose.material.icons.outlined.Search
import androidx.compose.material.icons.outlined.Settings
import androidx.compose.ui.Modifier

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            MovieAppTheme {
                val navController = rememberNavController()
                Scaffold(
                    modifier = Modifier.fillMaxSize(),
                    bottomBar = {
                        BottomNavigationBar(
                            items = listOf(
                                BottomNavItem(
                                    name = "Home",
                                    route = "home",
                                    icon = Icons.Outlined.Home
                                ),
                                BottomNavItem(
                                    name = "Search",
                                    route = "search",
                                    icon = Icons.Outlined.Search
                                ),
                                BottomNavItem(
                                    name = "Favourite",
                                    route = "favourite",
                                    icon = Icons.Outlined.Favorite
                                ),
                                BottomNavItem(
                                    name = "Watchlist",
                                    route = "watchlist",
                                    icon = Icons.AutoMirrored.Outlined.List
                                ),
                                BottomNavItem(
                                    name = "Settings",
                                    route = "settings",
                                    icon = Icons.Outlined.Settings
                                )
                            ),
                            navController = navController
                        )
                    }
                ) { innerPadding ->
                    NavigationGraph(
                        navController = navController,
                        modifier = Modifier.padding(innerPadding)
                    )
                }
            }
        }
    }
}
