package com.valcan.tt.ui.screens.home

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.valcan.tt.ui.components.TTBottomNavigation
import com.valcan.tt.ui.navigation.Screen
import androidx.compose.material3.ExperimentalMaterial3Api

@OptIn(ExperimentalMaterial3Api::class, ExperimentalLayoutApi::class)
@Composable
fun HomeScreen(
    navController: NavController,
    viewModel: HomeViewModel = hiltViewModel()
) {
    val statistics by viewModel.statistics.collectAsState()

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("TrendyTracker") },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primaryContainer
                )
            )
        },
        bottomBar = { TTBottomNavigation(navController) }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = "Il tuo guardaroba",
                style = MaterialTheme.typography.headlineMedium,
                textAlign = TextAlign.Center,
                modifier = Modifier.padding(bottom = 24.dp)
            )

            LazyVerticalGrid(
                columns = GridCells.Fixed(2),
                horizontalArrangement = Arrangement.spacedBy(16.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                item {
                    StatisticCard(
                        title = "Guardaroba",
                        value = statistics.wardrobeCount.toString(),
                        onClick = { /* TODO */ }
                    )
                }
                item {
                    StatisticCard(
                        title = "Vestiti",
                        value = statistics.clothesCount.toString(),
                        onClick = { navController.navigate(Screen.Clothes.route) }
                    )
                }
                item {
                    StatisticCard(
                        title = "Scarpe",
                        value = statistics.shoesCount.toString(),
                        onClick = { navController.navigate(Screen.Shoes.route) }
                    )
                }
                item {
                    StatisticCard(
                        title = "Ricerca",
                        value = "Cerca",
                        onClick = { navController.navigate(Screen.Search.route) }
                    )
                }
            }
        }
    }
}

@Composable
private fun StatisticCard(
    title: String,
    value: String,
    onClick: () -> Unit
) {
    Card(
        onClick = onClick,
        modifier = Modifier
            .fillMaxWidth()
            .aspectRatio(1f)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Text(
                text = title,
                style = MaterialTheme.typography.titleMedium,
                textAlign = TextAlign.Center
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = value,
                style = MaterialTheme.typography.headlineLarge,
                textAlign = TextAlign.Center
            )
        }
    }
} 