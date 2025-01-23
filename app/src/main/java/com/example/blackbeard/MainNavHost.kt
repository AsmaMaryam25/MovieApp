package com.example.blackbeard

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.safeDrawingPadding
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.TextFieldValue
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
import com.example.blackbeard.screens.search.content.SearchContentScreen
import com.example.blackbeard.screens.search.tab.TabSearchScreen
import com.example.blackbeard.screens.watchlist.WatchlistScreen

@Composable
fun MainNavHost(
    navController: NavHostController,
    onRouteChanged: (Route) -> Unit,
    onShowBottomBar: (Boolean) -> Unit,
    modifier: Modifier = Modifier,
) {
    NavHost(
        navController = navController,
        startDestination = Route.HomeScreen,
        modifier = Modifier.fillMaxSize()
    ) {
        composable<Route.HomeScreen> { backStackEntry ->
            LaunchedEffect(Unit) {
                onRouteChanged(backStackEntry.toRoute<Route.HomeScreen>())
                onShowBottomBar(true)
            }

            HomeScreen(
                onNavigateToDetailsScreen = { name, movieId ->
                    navController.navigate(Route.DetailsScreen(name = name, movieId = movieId))
                },
                modifier = modifier.fillMaxSize()
            )
        }

        composable<Route.FavoriteScreen> { backStackEntry ->
            LaunchedEffect(Unit) {
                onRouteChanged(backStackEntry.toRoute<Route.FavoriteScreen>())
                onShowBottomBar(true)
            }

            FavoriteScreen(
                modifier = modifier
                    .fillMaxSize(),
                onNavigateToDetailsScreen = { name, movieId ->
                    navController.navigate(Route.DetailsScreen(name = name, movieId = movieId))
                })
        }

        composable<Route.SearchScreen> {
            LaunchedEffect(Unit) {
                onShowBottomBar(true)
            }

            SearchScreen(
                onMoviePosterClicked = { name, movieId ->
                    navController.navigate(Route.DetailsScreen(name = name, movieId = movieId))
                },
                onSearchBarFocus = {
                    navController.navigate(Route.AdvancedSearchScreen(
                        "",
                        false
                    ))
                },
                modifier = modifier.fillMaxSize()
            )
        }


        composable<Route.SettingsScreen> { backStackEntry ->
            LaunchedEffect(Unit) {
                onRouteChanged(backStackEntry.toRoute<Route.SettingsScreen>())
                onShowBottomBar(true)
            }

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
            LaunchedEffect(Unit) {
                onRouteChanged(backStackEntry.toRoute<Route.WatchlistScreen>())
                onShowBottomBar(true)
            }

            WatchlistScreen(
                modifier = modifier
                    .fillMaxSize(),
                onNavigateToDetailsScreen = { name, movieId ->
                    navController.navigate(Route.DetailsScreen(name = name, movieId = movieId))
                })
        }

        composable<Route.AboutScreen> { backStackEntry ->
            LaunchedEffect(Unit) {
                onRouteChanged(backStackEntry.toRoute<Route.AboutScreen>())
                onShowBottomBar(true)
            }
            AboutScreen(
                //showTopBar = showTopBar,
                modifier = Modifier
                    .safeDrawingPadding()
                    .fillMaxSize(),
                popBackStack = { navController.popBackStack() }
            )
        }

        composable<Route.AppearanceScreen> { backStackEntry ->
            LaunchedEffect(Unit) {
                onRouteChanged(backStackEntry.toRoute<Route.AppearanceScreen>())
                onShowBottomBar(true)
            }

            AppearanceScreen(
                //showTopBar = showTopBar,
                modifier = Modifier
                    .safeDrawingPadding()
                    .fillMaxSize(),
                popBackStack = { navController.popBackStack() }
            )
        }

        composable<Route.DetailsScreen> { backStackEntry ->
            LaunchedEffect(Unit) {
                onRouteChanged(backStackEntry.toRoute<Route.DetailsScreen>())
                onShowBottomBar(false)
            }
            DetailsScreen(
                movieId = backStackEntry.arguments?.getInt("movieId")!!,
                modifier = Modifier
                    .safeDrawingPadding()
                    .fillMaxSize(),
                popBackStack = { navController.popBackStack() }
            )
        }

        composable<Route.AdvancedSearchScreen> { backStackEntry ->
            LaunchedEffect(Unit) {
                onRouteChanged(backStackEntry.toRoute<Route.AdvancedSearchScreen>())
                onShowBottomBar(false)
            }

            TabSearchScreen(
                modifier = modifier.fillMaxSize(),
                onCancelClicked = {
                    navController.navigate(Route.SearchScreen)
                },
                onSearchClicked = { query, isAdvanceSearch ->
                    navController.navigate(Route.SearchContentScreen(query = query, isAdvanceSearch = isAdvanceSearch))
                },
                query = backStackEntry.arguments?.getString("query")!!,
                isAdvancedSearch = backStackEntry.arguments?.getBoolean("isAdvanceSearch")!!
            )
        }

        composable<Route.SearchContentScreen> { backStackEntry ->
            LaunchedEffect(Unit) {
                onRouteChanged(backStackEntry.toRoute<Route.SearchContentScreen>())
                onShowBottomBar(false)
            }

            SearchContentScreen(
                modifier = modifier.fillMaxSize(),
                onMoviePosterClicked = { name, movieId ->
                    navController.navigate(Route.DetailsScreen(name = name, movieId = movieId))
                },
                onSearchBarFocus = { query, isAdvancedSearch ->
                    navController.navigate(Route.AdvancedSearchScreen(
                        query = query,
                        isAdvanceSearch = isAdvancedSearch
                    ))
                },
                query = TextFieldValue(backStackEntry.arguments?.getString("query")!!),
                isAdvancedSearch = backStackEntry.arguments?.getBoolean("isAdvanceSearch")!!,
                onBackButtonClicked = { query, isAdvancedSearch ->
                    navController.navigate(Route.AdvancedSearchScreen(
                        query = if(!isAdvancedSearch) query else "",
                        isAdvanceSearch = isAdvancedSearch
                    ))
                }
            )
        }
    }
}