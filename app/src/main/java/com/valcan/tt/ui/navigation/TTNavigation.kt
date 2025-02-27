package com.valcan.tt.ui.navigation

import androidx.compose.runtime.Composable
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

@Composable
fun TTNavigation(navController: NavHostController) {
    NavHost(
        navController = navController,
        startDestination = Screen.Welcome.route
    ) {
        composable(Screen.Welcome.route) {
            WelcomeScreen(navController)
        }
        
        composable(Screen.Home.route) {
            HomeScreen(navController)
        }
        
        composable(Screen.Clothes.route) {
            ClothesScreen(navController)
        }
        
        composable(
            route = Screen.ClothDetail.route,
            arguments = listOf(navArgument("clothId") { type = NavType.LongType })
        ) { backStackEntry ->
            val clothId = backStackEntry.arguments?.getLong("clothId") ?: return@composable
            ClothDetailScreen(navController, clothId)
        }
        
        composable(Screen.Shoes.route) {
            ShoesScreen(navController)
        }
        
        composable(Screen.Search.route) {
            SearchScreen(navController)
        }
        
        composable(Screen.Profile.route) {
            ProfileScreen(navController)
        }
    }
} 