package com.valcan.tt.ui.navigation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import androidx.compose.material3.Scaffold
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.foundation.layout.padding
import androidx.compose.ui.Modifier
import com.google.accompanist.pager.ExperimentalPagerApi
import com.google.accompanist.pager.HorizontalPager
import com.google.accompanist.pager.rememberPagerState
import com.valcan.tt.ui.screens.clothes.ClothDetailScreen
import com.valcan.tt.ui.screens.clothes.ClothesScreen
import com.valcan.tt.ui.screens.home.HomeScreen
import com.valcan.tt.ui.screens.profile.ProfileScreen
import com.valcan.tt.ui.screens.search.SearchScreen
import com.valcan.tt.ui.screens.shoes.ShoesScreen
import com.valcan.tt.ui.screens.welcome.WelcomeScreen
import com.valcan.tt.ui.screens.wardrobe.WardrobeScreen
import com.valcan.tt.ui.components.TTBottomNavigationWithPager
import kotlinx.coroutines.launch
import androidx.compose.runtime.LaunchedEffect

@Composable
fun TTNavigation(
    navController: NavHostController,
    startDestination: String = Screen.Welcome.route
) {
    NavHost(
        navController = navController,
        startDestination = startDestination
    ) {
        composable(Screen.Welcome.route) {
            WelcomeScreen(navController)
        }
        
        composable(Screen.Home.route) {
            MainScreenWithPager(navController, initialPage = 0)
        }
        
        composable(Screen.Clothes.route) {
            MainScreenWithPager(navController, initialPage = 1)
        }
        
        composable(
            route = Screen.ClothDetail.route,
            arguments = listOf(navArgument("clothId") { type = NavType.LongType })
        ) { backStackEntry ->
            val clothId = backStackEntry.arguments?.getLong("clothId") ?: return@composable
            ClothDetailScreen(navController, clothId)
        }
        
        composable(Screen.Shoes.route) {
            MainScreenWithPager(navController, initialPage = 2)
        }
        
        composable(Screen.Search.route) {
            MainScreenWithPager(navController, initialPage = 3)
        }
        
        composable(Screen.Profile.route) {
            MainScreenWithPager(navController, initialPage = 4)
        }
        
        composable(Screen.Wardrobe.route) {
            WardrobeScreen(navController = navController)
        }
    }
}

@OptIn(ExperimentalPagerApi::class, ExperimentalMaterial3Api::class)
@Composable
fun MainScreenWithPager(
    navController: NavHostController,
    initialPage: Int
) {
    val pages = listOf(
        Screen.Home,
        Screen.Clothes,
        Screen.Shoes,
        Screen.Search,
        Screen.Profile
    )
    
    val pagerState = rememberPagerState(initialPage = initialPage)
    val coroutineScope = rememberCoroutineScope()

    // Aggiorniamo la route corrente in base allo stato del pager
    val currentRoute = remember { mutableStateOf(pages[initialPage].route) }
    
    // Aggiorniamo la route quando cambia la pagina
    LaunchedEffect(pagerState.currentPage) {
        currentRoute.value = pages[pagerState.currentPage].route
    }
    
    Scaffold(
        bottomBar = {
            TTBottomNavigationWithPager(
                navController = navController,
                currentRoute = currentRoute.value,
                onTabSelected = { screen ->
                    val newIndex = pages.indexOfFirst { it.route == screen.route }
                    if (newIndex >= 0) {
                        coroutineScope.launch {
                            pagerState.animateScrollToPage(newIndex)
                        }
                    } else {
                        navController.navigate(screen.route)
                    }
                }
            )
        }
    ) { paddingValues ->
        HorizontalPager(
            count = pages.size,
            state = pagerState,
            modifier = Modifier.padding(paddingValues)
        ) { page ->
            when (page) {
                0 -> {
                    HomeScreen(navController = navController)
                }
                1 -> {
                    ClothesScreen(navController = navController)
                }
                2 -> {
                    ShoesScreen(navController = navController)
                }
                3 -> {
                    SearchScreen(navController = navController)
                }
                4 -> {
                    ProfileScreen(navController = navController)
                }
            }
        }
    }
} 