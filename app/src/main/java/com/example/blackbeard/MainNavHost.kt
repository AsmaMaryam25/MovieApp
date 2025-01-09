package com.example.blackbeard

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Modifier
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.toRoute
import com.example.blackbeard.models.Route
import com.example.blackbeard.screens.AboutScreen
import com.example.blackbeard.screens.AdvancedSearchScreen
import com.example.blackbeard.screens.AppearanceScreen
import com.example.blackbeard.screens.SettingsScreen
import com.example.blackbeard.screens.details.DetailsScreen
import com.example.blackbeard.screens.favorite.FavoriteScreen
import com.example.blackbeard.screens.home.HomeScreen
import com.example.blackbeard.screens.search.SearchScreen
import com.example.blackbeard.screens.watchlist.WatchlistScreen

@androidx.compose.runtime.Composable
fun MainNavHost(
    navController: androidx.navigation.NavHostController,
    onRouteChanged: (Route) -> Unit,
    modifier: Modifier = Modifier,
    showTopBar: () -> Unit,
    setVideoLink: (String?) -> Unit
) {
    NavHost(
        navController = navController,
        startDestination = Route.HomeScreen,
        modifier = modifier.fillMaxSize()
    ) {
        composable<Route.HomeScreen> {
            LaunchedEffect(Unit) { onRouteChanged(it.toRoute<Route.HomeScreen>()) }

            HomeScreen(
                onNavigateToDetailsScreen = { name, movieId ->
                    navController.navigate(Route.DetailsScreen(name = name, movieId = movieId))
                },
                modifier = Modifier.fillMaxSize()
            )
        }

        composable<Route.FavoriteScreen> { backStackEntry ->
            LaunchedEffect(Unit) { onRouteChanged(backStackEntry.toRoute<Route.FavoriteScreen>()) }

            FavoriteScreen(
                modifier = Modifier.fillMaxSize(),
                onNavigateToDetailsScreen = { name, movieId ->
                    navController.navigate(Route.DetailsScreen(name = name, movieId = movieId))
                })
        }

        composable<Route.SearchScreen> { backStackEntry ->
            LaunchedEffect(Unit) { onRouteChanged(backStackEntry.toRoute<Route.SearchScreen>()) }

            SearchScreen(
                onNavigateToAdvancedSearchScreen = {
                    navController.navigate(Route.AdvancedSearchScreen(it))
                },
                onNavigateToInterimSearchScreen = {
                    navController.navigate("interimSearch")
                },
                modifier = Modifier.fillMaxSize(),
                onNavigateToDetailsScreen = { name, movieId ->
                    navController.navigate(Route.DetailsScreen(name = name, movieId = movieId))
                }
            )
        }

        composable<Route.SettingsScreen> { backStackEntry ->
            LaunchedEffect(Unit) { onRouteChanged(backStackEntry.toRoute<Route.SettingsScreen>()) }

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
            LaunchedEffect(Unit) { onRouteChanged(backStackEntry.toRoute<Route.WatchlistScreen>()) }

            WatchlistScreen(
                modifier = Modifier.fillMaxSize(),
                onNavigateToDetailsScreen = { name, movieId ->
                    navController.navigate(Route.DetailsScreen(name = name, movieId = movieId))
                })
        }

        composable<Route.AboutScreen> { backStackEntry ->
            LaunchedEffect(Unit) { onRouteChanged(backStackEntry.toRoute<Route.AboutScreen>()) }
            AboutScreen(showTopBar = showTopBar, modifier = Modifier.fillMaxSize())
        }

        composable<Route.AppearanceScreen> { backStackEntry ->
            androidx.compose.runtime.LaunchedEffect(Unit) { onRouteChanged(backStackEntry.toRoute<Route.AppearanceScreen>()) }

            AppearanceScreen(
                showTopBar = showTopBar,
                modifier = Modifier.fillMaxSize()
            )
        }

        composable<Route.DetailsScreen> { backStackEntry ->
            LaunchedEffect(Unit) { onRouteChanged(backStackEntry.toRoute<Route.DetailsScreen>()) }

            DetailsScreen(
                movieId = backStackEntry.arguments?.getInt("movieId")!!,
                showTopBar = showTopBar,
                modifier = Modifier.fillMaxSize(),
                setVideoLink = setVideoLink
            )
        }

        composable<Route.AdvancedSearchScreen> { backStackEntry ->
            LaunchedEffect(Unit) { onRouteChanged(backStackEntry.toRoute<Route.AdvancedSearchScreen>()) }

            AdvancedSearchScreen(
                query = backStackEntry.arguments?.getString("query")!!,
                modifier = Modifier.fillMaxSize(),
                showTopBar = showTopBar
            )
        }
    }
}