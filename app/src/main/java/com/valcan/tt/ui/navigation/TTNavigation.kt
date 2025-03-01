package com.valcan.tt.ui.navigation

import androidx.compose.runtime.Composable
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import com.valcan.tt.ui.screens.clothes.ClothDetailScreen
import com.valcan.tt.ui.screens.clothes.ClothesScreen
import com.valcan.tt.ui.screens.home.HomeScreen
import com.valcan.tt.ui.screens.profile.ProfileScreen
import com.valcan.tt.ui.screens.search.SearchScreen
import com.valcan.tt.ui.screens.shoes.ShoesScreen
import com.valcan.tt.ui.screens.welcome.WelcomeScreen
import com.valcan.tt.ui.screens.wardrobe.WardrobeScreen

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
            HomeScreen(navController = navController)
        }
        
        composable(Screen.Clothes.route) {
            ClothesScreen(navController = navController)
        }
        
        composable(
            route = Screen.ClothDetail.route,
            arguments = listOf(navArgument("clothId") { type = NavType.LongType })
        ) { backStackEntry ->
            val clothId = backStackEntry.arguments?.getLong("clothId") ?: return@composable
            ClothDetailScreen(navController, clothId)
        }
        
        composable(Screen.Shoes.route) {
            ShoesScreen(navController = navController)
        }
        
        composable(Screen.Search.route) {
            SearchScreen(navController = navController)
        }
        
        composable(Screen.Profile.route) {
            ProfileScreen(navController = navController)
        }
        
        composable(Screen.Wardrobe.route) {
            WardrobeScreen(navController = navController)
        }
    }
} 