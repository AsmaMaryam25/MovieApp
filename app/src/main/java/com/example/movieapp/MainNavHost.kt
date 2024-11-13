package com.example.movieapp

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.ui.Modifier
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.toRoute
import com.example.movieapp.screens.AboutScreen
import com.example.movieapp.screens.AdvancedSearchScreen
import com.example.movieapp.screens.AppearanceScreen
import com.example.movieapp.screens.DetailsScreen
import com.example.movieapp.screens.FavoriteScreen
import com.example.movieapp.screens.HomeScreen
import com.example.movieapp.screens.SearchScreen
import com.example.movieapp.screens.SettingsScreen
import com.example.movieapp.screens.WatchlistScreen

@androidx.compose.runtime.Composable
fun MainNavHost(
    navController: androidx.navigation.NavHostController,
    onRouteChanged: (Route) -> Unit,
    modifier: Modifier = Modifier,
    showTopBar: () -> Unit,
    toggleDarkTheme: () -> Unit
) {
    NavHost(
        navController = navController,
        startDestination = Route.HomeScreen,
        modifier = modifier.fillMaxSize()
    ) {
        composable<Route.HomeScreen> {
            androidx.compose.runtime.LaunchedEffect(Unit) { onRouteChanged(it.toRoute<Route.HomeScreen>()) }

            HomeScreen(
                onNavigateToDetailsScreen = {
                    navController.navigate(Route.DetailsScreen(it))
                },
                modifier = Modifier.fillMaxSize()
            )
        }

        composable<Route.FavoriteScreen> { backStackEntry ->
            androidx.compose.runtime.LaunchedEffect(Unit) { onRouteChanged(backStackEntry.toRoute<Route.FavoriteScreen>()) }

            FavoriteScreen(modifier = Modifier.fillMaxSize(), onNavigateToDetailsScreen = {
                navController.navigate(Route.DetailsScreen(it))
            })
        }

        composable<Route.SearchScreen> { backStackEntry ->
            androidx.compose.runtime.LaunchedEffect(Unit) { onRouteChanged(backStackEntry.toRoute<Route.SearchScreen>()) }

            SearchScreen(onNavigateToAdvancedSearchScreen = {
                navController.navigate(Route.AdvancedSearchScreen(it))
            }, modifier = Modifier.fillMaxSize(), onNavigateToDetailsScreen = {
                navController.navigate(Route.DetailsScreen(it))
            })
        }

        composable<Route.SettingsScreen> { backStackEntry ->
            androidx.compose.runtime.LaunchedEffect(Unit) { onRouteChanged(backStackEntry.toRoute<Route.SettingsScreen>()) }

            SettingsScreen(
                onNavigateToAboutScreen = {
                    navController.navigate(Route.AboutScreen)
                },
                onNavigateToAppearanceScreen = {
                    navController.navigate(Route.AppearanceScreen)
                },
                modifier = Modifier.fillMaxSize()
            )
        }

        composable<Route.WatchlistScreen> { backStackEntry ->
            androidx.compose.runtime.LaunchedEffect(Unit) { onRouteChanged(backStackEntry.toRoute<Route.WatchlistScreen>()) }

            WatchlistScreen(modifier = Modifier.fillMaxSize(), onNavigateToDetailsScreen = {
                navController.navigate(Route.DetailsScreen(it))
            })
        }

        composable<Route.AboutScreen> { backStackEntry ->
            androidx.compose.runtime.LaunchedEffect(Unit) { onRouteChanged(backStackEntry.toRoute<Route.AboutScreen>()) }
            AboutScreen(showTopBar = showTopBar, modifier = Modifier.fillMaxSize())
        }

        composable<Route.AppearanceScreen> { backStackEntry ->
            androidx.compose.runtime.LaunchedEffect(Unit) { onRouteChanged(backStackEntry.toRoute<Route.AppearanceScreen>()) }

            AppearanceScreen(
                showTopBar = showTopBar,
                toggleDarkTheme = toggleDarkTheme,
                modifier = Modifier.fillMaxSize()
            )
        }

        composable<Route.DetailsScreen> { backStackEntry ->
            androidx.compose.runtime.LaunchedEffect(Unit) { onRouteChanged(backStackEntry.toRoute<Route.DetailsScreen>()) }

            DetailsScreen(
                movieId = backStackEntry.arguments?.getString("movieId")!!,
                showTopBar = showTopBar,
                modifier = Modifier.fillMaxSize()
            )
        }

        composable<Route.AdvancedSearchScreen> { backStackEntry ->
            androidx.compose.runtime.LaunchedEffect(Unit) { onRouteChanged(backStackEntry.toRoute<Route.AdvancedSearchScreen>()) }

            AdvancedSearchScreen(
                query = backStackEntry.arguments?.getString("query")!!,
                modifier = Modifier.fillMaxSize(),
                showTopBar = showTopBar
            )
        }
    }
}