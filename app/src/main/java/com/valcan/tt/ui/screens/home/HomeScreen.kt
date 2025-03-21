package com.valcan.tt.ui.screens.home

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.valcan.tt.R
import com.valcan.tt.data.repository.UserRepository
import com.valcan.tt.data.repository.ClothesRepository
import com.valcan.tt.data.repository.ShoesRepository
import com.valcan.tt.ui.components.TTBottomNavigation
import com.valcan.tt.ui.theme.Typography
import androidx.hilt.navigation.compose.hiltViewModel
import com.valcan.tt.ui.viewmodel.HomeViewModel

@Composable
fun HomeScreen(
    navController: NavController,
    viewModel: HomeViewModel = hiltViewModel()
) {
    val currentUser by viewModel.currentUser.collectAsState(initial = null)
    val userClothes by viewModel.userClothes.collectAsState(initial = 0)
    val userShoes by viewModel.userShoes.collectAsState(initial = 0)

    // Debug: stampiamo il valore di currentUser
    LaunchedEffect(currentUser) {
        println("DEBUG: Current user in HomeScreen: $currentUser")
    }

    Scaffold(
        bottomBar = { TTBottomNavigation(navController = navController) }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Titolo centrato
            Text(
                text = "TrendyTracker",
                modifier = Modifier.padding(top = 24.dp),
                style = Typography.headlineLarge,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.primary,
                textAlign = TextAlign.Center
            )

            // Messaggio di benvenuto
            Text(
                text = "Ciao ${currentUser?.name ?: ""}",
                modifier = Modifier.padding(top = 16.dp),
                style = Typography.headlineMedium,
                color = MaterialTheme.colorScheme.secondary
            )

            // Domanda
            Text(
                text = "Cosa ci mettiamo oggi?",
                modifier = Modifier.padding(top = 8.dp),
                style = Typography.titleLarge,
                color = MaterialTheme.colorScheme.tertiary
            )

            Spacer(modifier = Modifier.height(32.dp))

            // Cerchio con conteggio vestiti dell'utente
            Box(
                modifier = Modifier
                    .size(120.dp)
                    .clip(CircleShape)
                    .background(MaterialTheme.colorScheme.primary)
                    .clickable { navController.navigate("clothes") }
                    .padding(16.dp),
                contentAlignment = Alignment.Center
            ) {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Image(
                        painter = painterResource(id = R.drawable.ic_clothes_kawaii),
                        contentDescription = "Vai ai vestiti",
                        modifier = Modifier.size(48.dp)
                    )
                    Text(
                        text = "$userClothes",
                        color = Color.White,
                        style = Typography.headlineMedium,
                        fontWeight = FontWeight.Bold
                    )
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            // Cerchio con conteggio scarpe dell'utente
            Box(
                modifier = Modifier
                    .size(120.dp)
                    .clip(CircleShape)
                    .background(MaterialTheme.colorScheme.secondary)
                    .clickable { navController.navigate("shoes") }
                    .padding(16.dp),
                contentAlignment = Alignment.Center
            ) {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Image(
                        painter = painterResource(id = R.drawable.ic_shoes_kawaii),
                        contentDescription = "Vai alle scarpe",
                        modifier = Modifier.size(48.dp)
                    )
                    Text(
                        text = "$userShoes",
                        color = Color.White,
                        style = Typography.headlineMedium,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        }
    }
} 