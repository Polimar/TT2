package com.valcan.tt.ui.screens.shoes

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavController
import com.valcan.tt.ui.components.TTBottomNavigation

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ShoesScreen(navController: NavController) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Le mie Scarpe") }
            )
        },
        bottomBar = { TTBottomNavigation(navController) }
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            Text("Schermata Scarpe - In costruzione")
        }
    }
} 