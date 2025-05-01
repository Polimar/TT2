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
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.valcan.tt.R
import androidx.hilt.navigation.compose.hiltViewModel
import com.valcan.tt.ui.viewmodel.HomeViewModel
import com.valcan.tt.ui.navigation.Screen
import com.valcan.tt.ui.components.NativeAdComponent

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
                text = stringResource(R.string.app_name),
                style = MaterialTheme.typography.headlineLarge,
                color = MaterialTheme.colorScheme.primary,
                modifier = Modifier.padding(vertical = 24.dp)
            )
            
            // Saluto utente
            if (currentUser != null) {
                Text(
                    text = stringResource(R.string.home_welcome, currentUser?.name ?: ""),
                    style = MaterialTheme.typography.headlineMedium,
                    textAlign = TextAlign.Center
                )
                
                Spacer(modifier = Modifier.height(8.dp))
                
                // Cosa ci mettiamo oggi?
                Text(
                    text = stringResource(R.string.home_what_to_wear),
                    style = MaterialTheme.typography.titleLarge,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.padding(vertical = 16.dp)
                )
                
                Spacer(modifier = Modifier.height(16.dp))
                
                // Statistiche vestiti e scarpe in colonna
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.Center
                    //horizontalAlignment = Alignment.CenterHorizontally,
                    //verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    // Card vestiti
                    HomeStatCard(
                        icon = R.drawable.ic_clothes_kawaii,
                        count = userClothes,
                        title = stringResource(R.string.nav_clothes),
                        onClick = { navController.navigate(Screen.Clothes.route) }
                    )
                    Spacer(modifier = Modifier.width(24.dp))
                    // Card scarpe
                    HomeStatCard(
                        icon = R.drawable.ic_shoes_kawaii,
                        count = userShoes,
                        title = stringResource(R.string.nav_shoes),
                        onClick = { navController.navigate(Screen.Shoes.route) }
                    )
                }
            } else {
                // Se non c'è un utente, mostriamo un messaggio
                Text(
                    text = stringResource(R.string.home_no_user),
                    style = MaterialTheme.typography.bodyLarge,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.padding(vertical = 16.dp)
                )
                
                Button(
                    onClick = { navController.navigate(Screen.Profile.route) }
                ) {
                    Text(stringResource(R.string.home_go_to_profile))
                }
            }
            
            // Aggiungi spazio prima dell'annuncio
            Spacer(modifier = Modifier.height(24.dp))
            
            // Annuncio nativo
            NativeAdComponent(adUnitId = "ca-app-pub-8145977851051737/4593816705")
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
    Box(
        modifier = Modifier
            .size(120.dp)
            .clip(CircleShape)
            .background(MaterialTheme.colorScheme.primaryContainer)
            .clickable(onClick = onClick),
        contentAlignment = Alignment.Center
    ) {
        Column(
            modifier = Modifier.padding(2.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            // Icona
            Image(
                painter = painterResource(id = icon),
                contentDescription = title,
                modifier = Modifier.size(24.dp)
            )
            
            Spacer(modifier = Modifier.height(2.dp))
            
            // Conteggio
            Text(
                text = count.toString(),
                style = MaterialTheme.typography.headlineMedium,
                fontWeight = FontWeight.ExtraLight,
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