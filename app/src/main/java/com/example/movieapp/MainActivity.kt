package com.example.movieapp

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.List
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.example.movieapp.ui.theme.NavigationInComposeTheme
import androidx.compose.material.icons.automirrored.filled.List

class MainActivity : ComponentActivity() {
    val navItemList = listOf(
        NavItem("Home", Icons.Default.Home),
        NavItem("Search", Icons.Default.Search),
        NavItem("Favorites", Icons.Default.Favorite),
        NavItem("Watchlist", Icons.AutoMirrored.Filled.List),
        NavItem("Settings" , Icons.Default.Settings)
    )

    @OptIn(ExperimentalMaterial3Api::class)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            NavigationInComposeTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    val navController = rememberNavController()
                    var canNavigateBack by remember { mutableStateOf(false) }
                    var currentScreenTitle by remember { mutableStateOf("") }

                    LaunchedEffect(navController.currentBackStackEntryAsState().value) {
                        canNavigateBack = navController.previousBackStackEntry != null
                    }

                    Scaffold(
                        bottomBar = {
                            NavigationBar {
                                navItemList.forEachIndexed { index, navItem ->
                                    NavigationBarItem(
                                        icon = {
                                            Icon(
                                                imageVector = navItem.icon,
                                                contentDescription = null
                                            )
                                        },
                                        label = {
                                            Text(navItem.label)
                                        },
                                        selected = false,
                                        onClick = {
                                            if(navItem.label == "Home") {
                                                navController.navigate(Route.HomeScreen)
                                            } else if(navItem.label == "Favorite") {
                                                navController.navigate(Route.FavoriteScreen)
                                            } else if(navItem.label == "Search") {
                                                navController.navigate(Route.SearchScreen)
                                            } else if(navItem.label == "Settings") {
                                                navController.navigate(Route.SettingsScreen)
                                            } else if(navItem.label == "Watchlist") {
                                                navController.navigate(Route.WatchlistScreen)
                                            }
                                        }
                                    )
                                }
                            }
                        },
                        topBar = {
                            TopAppBar(
                                title = {
                                    Text(text = currentScreenTitle)
                                },
                                navigationIcon = {
                                    if (canNavigateBack) {
                                        IconButton(onClick = { navController.popBackStack() }) {
                                            Icon(
                                                imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                                                contentDescription = null
                                            )
                                        }
                                    }
                                }
                            )
                        }
                    ) {
                        MainNavHost(
                            navController = navController,
                            onRouteChanged = { route -> currentScreenTitle = route.title },
                            modifier = Modifier.padding(it)
                        )
                    }
                }
            }
        }
    }
}