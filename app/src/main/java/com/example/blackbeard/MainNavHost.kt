package com.example.blackbeard

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.safeDrawingPadding
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.toRoute
import com.example.blackbeard.models.Route
import com.example.blackbeard.screens.AboutScreen
import com.example.blackbeard.screens.AppearanceScreen
import com.example.blackbeard.screens.SettingsScreen
import com.example.blackbeard.screens.details.DetailsScreen
import com.example.blackbeard.screens.favorite.FavoriteScreen
import com.example.blackbeard.screens.home.HomeScreen
import com.example.blackbeard.screens.search.SearchScreen
import com.example.blackbeard.screens.watchlist.WatchlistScreen

@Composable
fun MainNavHost(
    navController: NavHostController,
    onRouteChanged: (Route) -> Unit,
    modifier: Modifier = Modifier,
) {
    NavHost(
        navController = navController,
        startDestination = Route.HomeScreen,
        modifier = Modifier.fillMaxSize()
    ) {
        composable<Route.HomeScreen> {
            LaunchedEffect(Unit) { onRouteChanged(it.toRoute<Route.HomeScreen>()) }

            HomeScreen(
                onNavigateToDetailsScreen = { name, movieId ->
                    navController.navigate(Route.DetailsScreen(name = name, movieId = movieId))
                },
                modifier = modifier.fillMaxSize()
            )
        }

        composable<Route.FavoriteScreen> { backStackEntry ->
            LaunchedEffect(Unit) { onRouteChanged(backStackEntry.toRoute<Route.FavoriteScreen>()) }

            FavoriteScreen(
                modifier = Modifier
                    .safeDrawingPadding()
                    .fillMaxSize(),
                onNavigateToDetailsScreen = { name, movieId ->
                    navController.navigate(Route.DetailsScreen(name = name, movieId = movieId))
                })
        }

        composable<Route.SearchScreen> {
            SearchScreen(
                onNavigateToDetailsScreen = { name, movieId ->
                    navController.navigate(Route.DetailsScreen(name = name, movieId = movieId))
                },
                /*onNavigateToSearchContentScreen = {
                    navController.navigate(Route.SearchContentScreen())
                }*/
                modifier = modifier.fillMaxSize()
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
                modifier = Modifier
                    .safeDrawingPadding()
                    .fillMaxSize()
            )
        }

        composable<Route.WatchlistScreen> { backStackEntry ->
            LaunchedEffect(Unit) { onRouteChanged(backStackEntry.toRoute<Route.WatchlistScreen>()) }

            WatchlistScreen(
                modifier = Modifier
                    .safeDrawingPadding()
                    .fillMaxSize(),
                onNavigateToDetailsScreen = { name, movieId ->
                    navController.navigate(Route.DetailsScreen(name = name, movieId = movieId))
                })
        }

        composable<Route.AboutScreen> { backStackEntry ->
            LaunchedEffect(Unit) { onRouteChanged(backStackEntry.toRoute<Route.AboutScreen>()) }
            AboutScreen(
                //showTopBar = showTopBar,
                modifier = Modifier
                    .safeDrawingPadding()
                    .fillMaxSize(),
                popBackStack = { navController.popBackStack() }
            )
        }

        composable<Route.AppearanceScreen> { backStackEntry ->
            LaunchedEffect(Unit) { onRouteChanged(backStackEntry.toRoute<Route.AppearanceScreen>()) }

            AppearanceScreen(
                //showTopBar = showTopBar,
                modifier = Modifier
                    .safeDrawingPadding()
                    .fillMaxSize(),
                popBackStack = { navController.popBackStack() }
            )
        }

        composable<Route.DetailsScreen> { backStackEntry ->
            LaunchedEffect(Unit) { onRouteChanged(backStackEntry.toRoute<Route.DetailsScreen>()) }
            DetailsScreen(
                movieId = backStackEntry.arguments?.getInt("movieId")!!,
                modifier = Modifier
                    .safeDrawingPadding()
                    .fillMaxSize(),
                popBackStack = { navController.popBackStack() }
            )
        }

        /*composable<Route.SearchContentScreen> { backStackEntry ->
            LaunchedEffect(Unit) { onRouteChanged(backStackEntry.toRoute<Route.SearchContentScreen>()) }
            SearchContent(
                modifier = Modifier
                    .safeDrawingPadding()
                    .fillMaxSize(),
                searchQuery = backStackEntry.arguments?.getString("query")!!,
                onNavigateToDetailsScreen = { name, movieId ->
                    navController.navigate(Route.DetailsScreen(name = name, movieId = movieId))
                },
                collectionMovies = backStackEntry.arguments?.getList("query")!!,
                searchViewModel = TODO(),
                gridState = TODO(),
                isBoxClicked = TODO(),
            )
        }*/
    }
}