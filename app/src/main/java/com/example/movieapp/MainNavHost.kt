package com.example.movieapp

import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.toRoute
import com.example.movieapp.screens.AboutScreen
import com.example.movieapp.screens.AppearanceScreen
import com.example.movieapp.screens.FavoriteScreen
import com.example.movieapp.screens.HomeScreen
import com.example.movieapp.screens.SearchScreen
import com.example.movieapp.screens.SettingsScreen
import com.example.movieapp.screens.WatchlistScreen

// Step1: define routes ✅

// Step2: get nav controller ✅

// Step3: call NavHost ✅

// Step4: add screens to nav graph ✅

// Step5: add navigation actions ✅

// Step6: add navigation arguments ✅

// Step7: add top app bar with back arrow and screen title ✅

@androidx.compose.runtime.Composable
fun MainNavHost(
    navController: androidx.navigation.NavHostController,
    onRouteChanged: (Route) -> Unit,
    modifier: androidx.compose.ui.Modifier,
    showTopBar: () -> Unit
) {
    NavHost(
        navController = navController,
        startDestination = Route.HomeScreen,
        modifier = modifier
    ) {
        composable<Route.HomeScreen> {
            androidx.compose.runtime.LaunchedEffect(Unit) { onRouteChanged(it.toRoute<Route.HomeScreen>()) }

            HomeScreen(
                onNavigateToDetailsScreen = {
                    navController.navigate(Route.FavoriteScreen)
                }
            )
        }

        composable<Route.FavoriteScreen> { backStackEntry ->
            androidx.compose.runtime.LaunchedEffect(Unit) { onRouteChanged(backStackEntry.toRoute<Route.FavoriteScreen>()) }

            FavoriteScreen()
        }

        composable<Route.SearchScreen> { backStackEntry ->
            androidx.compose.runtime.LaunchedEffect(Unit) { onRouteChanged(backStackEntry.toRoute<Route.SearchScreen>()) }

            SearchScreen()
        }

        composable<Route.SettingsScreen> { backStackEntry ->
            androidx.compose.runtime.LaunchedEffect(Unit) { onRouteChanged(backStackEntry.toRoute<Route.SettingsScreen>()) }

            SettingsScreen(
                onNavigateToAboutScreen = {
                    navController.navigate(Route.AboutScreen)
                },
                onNavigateToAppearanceScreen = {
                    navController.navigate(Route.AppearanceScreen)
                }
            )
        }

        composable<Route.WatchlistScreen> { backStackEntry ->
            androidx.compose.runtime.LaunchedEffect(Unit) { onRouteChanged(backStackEntry.toRoute<Route.WatchlistScreen>()) }

            WatchlistScreen()
        }

        composable<Route.AboutScreen> { backStackEntry ->
            androidx.compose.runtime.LaunchedEffect(Unit) { onRouteChanged(backStackEntry.toRoute<Route.AboutScreen>()) }

            AboutScreen(showTopBar = showTopBar)
        }

        composable<Route.AppearanceScreen> { backStackEntry ->
            androidx.compose.runtime.LaunchedEffect(Unit) { onRouteChanged(backStackEntry.toRoute<Route.AppearanceScreen>()) }

            AppearanceScreen(showTopBar = showTopBar)
        }

    }
}