package com.valcan.tt.ui.components

import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.res.painterResource
import androidx.navigation.NavController
import androidx.navigation.compose.currentBackStackEntryAsState
import com.valcan.tt.R
import com.valcan.tt.ui.navigation.Screen

@Composable
fun TTBottomNavigation(
    navController: NavController
) {
    NavigationBar {
        val navBackStackEntry = navController.currentBackStackEntryAsState()
        val currentRoute = navBackStackEntry.value?.destination?.route

        NavigationBarItem(
            icon = { Icon(painter = painterResource(R.drawable.ic_home), contentDescription = null) },
            label = { Text("Home") },
            selected = currentRoute == Screen.Home.route,
            onClick = { navController.navigate(Screen.Home.route) }
        )
        NavigationBarItem(
            icon = { Icon(painter = painterResource(R.drawable.ic_clothes), contentDescription = null) },
            label = { Text("Vestiti") },
            selected = currentRoute == Screen.Clothes.route,
            onClick = { navController.navigate(Screen.Clothes.route) }
        )
        NavigationBarItem(
            icon = { Icon(painter = painterResource(R.drawable.ic_shoes), contentDescription = null) },
            label = { Text("Scarpe") },
            selected = currentRoute == Screen.Shoes.route,
            onClick = { navController.navigate(Screen.Shoes.route) }
        )
        NavigationBarItem(
            icon = { Icon(painter = painterResource(R.drawable.ic_search), contentDescription = null) },
            label = { Text("Cerca") },
            selected = currentRoute == Screen.Search.route,
            onClick = { navController.navigate(Screen.Search.route) }
        )
        NavigationBarItem(
            icon = { Icon(painter = painterResource(R.drawable.ic_profile), contentDescription = null) },
            label = { Text("Profilo") },
            selected = currentRoute == Screen.Profile.route,
            onClick = { navController.navigate(Screen.Profile.route) }
        )
    }
} 