package com.valcan.tt.ui.screens.home

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
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
import com.valcan.tt.ui.navigation.Screen

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    navController: NavController,
    viewModel: HomeViewModel = hiltViewModel()
) {
    val currentUser by viewModel.currentUser.collectAsState()
    val userClothes by viewModel.userClothes.collectAsState()
    val userShoes by viewModel.userShoes.collectAsState()

    // Debug: stampiamo il valore di currentUser
    LaunchedEffect(currentUser) {
        println("DEBUG: Current user in HomeScreen: $currentUser")
    }

    Box(modifier = Modifier.fillMaxSize()) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Titolo principale
            Text(
                text = "Trendy Tracker",
                style = MaterialTheme.typography.headlineLarge,
                color = MaterialTheme.colorScheme.primary,
                modifier = Modifier.padding(vertical = 24.dp)
            )
            
            // Saluto utente
            if (currentUser != null) {
                Text(
                    text = "Benvenuto ${currentUser?.name}!",
                    style = MaterialTheme.typography.headlineMedium,
                    textAlign = TextAlign.Center
                )
                
                Spacer(modifier = Modifier.height(8.dp))
                
                // Cosa ci mettiamo oggi?
                Text(
                    text = "Cosa ci mettiamo oggi?",
                    style = MaterialTheme.typography.titleLarge,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.padding(vertical = 16.dp)
                )
                
                Spacer(modifier = Modifier.height(16.dp))
                
                // Statistiche vestiti e scarpe in colonna
                Column(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(24.dp)
                ) {
                    // Card vestiti
                    HomeStatCard(
                        icon = R.drawable.ic_clothes_kawaii,
                        count = userClothes,
                        title = "Vestiti",
                        onClick = { navController.navigate(Screen.Clothes.route) }
                    )
                    
                    // Card scarpe
                    HomeStatCard(
                        icon = R.drawable.ic_shoes_kawaii,
                        count = userShoes,
                        title = "Scarpe",
                        onClick = { navController.navigate(Screen.Shoes.route) }
                    )
                }
            } else {
                // Se non c'è un utente, mostriamo un messaggio
                Text(
                    text = "Nessun utente selezionato",
                    style = MaterialTheme.typography.bodyLarge,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.padding(vertical = 16.dp)
                )
                
                Button(
                    onClick = { navController.navigate(Screen.Profile.route) }
                ) {
                    Text("Vai al profilo")
                }
            }
        }
    }
}

@Composable
fun HomeStatCard(
    icon: Int,
    count: Int,
    title: String,
    onClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .width(150.dp)
            .height(180.dp)
            .clickable(onClick = onClick),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        )
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            // Icona
            Box(
                modifier = Modifier
                    .size(64.dp)
                    .clip(CircleShape)
                    .background(MaterialTheme.colorScheme.primaryContainer)
                    .padding(12.dp),
                contentAlignment = Alignment.Center
            ) {
                Image(
                    painter = painterResource(id = icon),
                    contentDescription = title,
                    modifier = Modifier.size(40.dp)
                )
            }
            
            Spacer(modifier = Modifier.height(16.dp))
            
            // Conteggio
            Text(
                text = count.toString(),
                style = MaterialTheme.typography.headlineMedium,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.primary
            )
            
            // Titolo
            Text(
                text = title,
                style = MaterialTheme.typography.titleMedium,
                color = MaterialTheme.colorScheme.onSurface,
                textAlign = TextAlign.Center
            )
        }
    }
} 