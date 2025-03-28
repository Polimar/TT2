package com.valcan.tt.ui.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.size
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawWithContent
import androidx.compose.ui.graphics.ColorMatrix
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import androidx.navigation.compose.currentBackStackEntryAsState
import com.valcan.tt.R
import com.valcan.tt.ui.navigation.Screen

@Composable
fun TTBottomNavigation(
    navController: NavController
) {
    NavigationBar(
        containerColor = MaterialTheme.colorScheme.surface,
        contentColor = MaterialTheme.colorScheme.primary
    ) {
        val navBackStackEntry = navController.currentBackStackEntryAsState()
        val currentRoute = navBackStackEntry.value?.destination?.route

        val items = listOf(
            Screen.Home to R.drawable.ic_home_kawaii,
            Screen.Clothes to R.drawable.ic_clothes_kawaii,
            Screen.Shoes to R.drawable.ic_shoes_kawaii,
            Screen.Search to R.drawable.ic_search_kawaii,
            Screen.Profile to R.drawable.ic_profile_kawaii
        )

        items.forEach { (screen, iconRes) ->
            val selected = currentRoute == screen.route
            NavigationBarItem(
                icon = {
                    Image(
                        painter = painterResource(iconRes),
                        contentDescription = null,
                        modifier = Modifier.size(if (selected) 60.dp else 48.dp),
                        colorFilter = if (!selected) ColorFilter.colorMatrix(ColorMatrix().apply { setToSaturation(0.5f) }) else null
                    )
                },
                label = null,
                selected = selected,
                onClick = {
                    navController.navigate(screen.route) {
                        popUpTo(navController.graph.startDestinationId) { saveState = true }
                        launchSingleTop = true
                        restoreState = true
                    }
                }
            )
        }
    }
}

@Composable
fun TTBottomNavigationWithPager(
    navController: NavController,
    currentRoute: String,
    onTabSelected: (Screen) -> Unit
) {
    NavigationBar(
        containerColor = MaterialTheme.colorScheme.surface,
        contentColor = MaterialTheme.colorScheme.primary
    ) {
        val items = listOf(
            Screen.Home to R.drawable.ic_home_kawaii,
            Screen.Clothes to R.drawable.ic_clothes_kawaii,
            Screen.Shoes to R.drawable.ic_shoes_kawaii,
            Screen.Search to R.drawable.ic_search_kawaii,
            Screen.Profile to R.drawable.ic_profile_kawaii
        )

        items.forEach { (screen, iconRes) ->
            val selected = currentRoute == screen.route
            NavigationBarItem(
                icon = {
                    Image(
                        painter = painterResource(iconRes),
                        contentDescription = null,
                        modifier = Modifier.size(if (selected) 60.dp else 48.dp),
                        colorFilter = if (!selected) ColorFilter.colorMatrix(ColorMatrix().apply { setToSaturation(0.5f) }) else null
                    )
                },
                label = null,
                selected = selected,
                onClick = {
                    onTabSelected(screen)
                }
            )
        }
    }
} 