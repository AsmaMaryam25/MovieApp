package com.example.movieapp.components.navigation

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.example.movieapp.screens.*

@Composable
fun NavigationGraph(navController: NavHostController, modifier: Modifier = Modifier) {
    NavHost(
        navController = navController,
        startDestination = "home",
        modifier = modifier
    ) {

            composable("home") {
                HomeScreen()
            }
            composable("search") {
                SearchScreen()
            }
        composable("favourite") {
            FavouriteScreen()
        }
        composable("watchlist") {
            WatchlistScreen()
        }
        composable("settings") {
            SettingsScreen()
        }
        }
    }

