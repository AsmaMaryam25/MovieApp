package com.example.movieapp

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.outlined.FormatListBulleted
import androidx.compose.material.icons.outlined.FavoriteBorder
import androidx.compose.material.icons.outlined.Home
import androidx.compose.material.icons.outlined.Search
import androidx.compose.material.icons.outlined.Settings
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
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.FormatListBulleted
import androidx.compose.ui.text.font.FontWeight

class MainActivity : ComponentActivity() {
    private var navItemList = mutableListOf(
        NavItem("Home", Icons.Filled.Home),
        NavItem("Search", Icons.Outlined.Search),
        NavItem("Favorites", Icons.Outlined.FavoriteBorder),
        NavItem("Watchlist", Icons.AutoMirrored.Outlined.FormatListBulleted),
        NavItem("Settings" , Icons.Outlined.Settings)
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
                    var isNavigationBarAction by remember { mutableStateOf(false) }
                    var topBarShown by remember { mutableStateOf(false) }
                    var selectedItem by remember { mutableStateOf(navItemList[0].label) }

                    LaunchedEffect(navController.currentBackStackEntryAsState().value) {
                        if (!isNavigationBarAction) {
                            canNavigateBack = navController.previousBackStackEntry != null
                        }
                        isNavigationBarAction = false
                    }

                    Scaffold(
                        bottomBar = {
                            NavigationBar {
                                navItemList.forEach{ navItem ->
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
                                        selected = selectedItem == navItem.label,
                                        onClick = {
                                            isNavigationBarAction = true
                                            canNavigateBack = false
                                            topBarShown = false
                                            selectedItem = navItem.label
                                            navItemList.clear()
                                            navItemList.addAll(
                                                listOf(
                                                    NavItem("Home", Icons.Outlined.Home),
                                                    NavItem("Search", Icons.Outlined.Search),
                                                    NavItem("Favorites", Icons.Outlined.FavoriteBorder),
                                                    NavItem("Watchlist", Icons.AutoMirrored.Outlined.FormatListBulleted),
                                                    NavItem("Settings", Icons.Outlined.Settings)
                                                )
                                            )
                                            when (navItem.label) {
                                                "Home" -> {
                                                    navController.navigate(Route.HomeScreen)
                                                    navItemList[0] = NavItem("Home", Icons.Filled.Home)
                                                }
                                                "Favorites" -> {
                                                    navController.navigate(Route.FavoriteScreen)
                                                    navItemList[2] = NavItem("Favorites", Icons.Filled.Favorite)
                                                    topBarShown = true

                                                }
                                                "Search" -> {
                                                    navController.navigate(Route.SearchScreen)
                                                    navItemList[1] = NavItem("Search", Icons.Filled.Search)
                                                }
                                                "Settings" -> {
                                                    navController.navigate(Route.SettingsScreen)
                                                    navItemList[4] = NavItem("Settings", Icons.Filled.Settings)
                                                }
                                                "Watchlist" -> {
                                                    navController.navigate(Route.WatchlistScreen)
                                                    navItemList[3] = NavItem("Watchlist", Icons.Filled.FormatListBulleted)
                                                    topBarShown = true
                                                }
                                            }
                                        }
                                    )
                                }
                            }
                        },
                        topBar = {
                            if (topBarShown){
                                TopAppBar(
                                    title = {
                                        Text( text = currentScreenTitle,
                                            style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold),
                                            modifier = Modifier.fillMaxWidth(),
                                        )
                                    },
                                    navigationIcon = {
                                        if (canNavigateBack) {
                                            IconButton(onClick = { navController.popBackStack()
                                                topBarShown = false
                                            }) {
                                                Icon(
                                                    imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                                                    contentDescription = null
                                                )
                                            }
                                        }
                                    }
                                )
                            }
                        }
                    ) {
                        MainNavHost(
                            navController = navController,
                            onRouteChanged = { route -> currentScreenTitle = route.title },
                            modifier = Modifier.padding(it),
                            showTopBar = { topBarShown = true }
                        )
                    }
                }
            }
        }
    }
}