package com.valcan.tt.ui.screens.clothes

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.valcan.tt.ui.components.TTBottomNavigation
import com.valcan.tt.ui.navigation.Screen
import com.valcan.tt.data.model.Clothes

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ClothesScreen(
    navController: NavController,
    viewModel: ClothesViewModel = hiltViewModel()
) {
    val clothes by viewModel.clothes.collectAsState()
    val searchQuery by viewModel.searchQuery.collectAsState()

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("I miei Vestiti") },
                actions = {
                    IconButton(onClick = { /* TODO: Implementare filtri */ }) {
                        Icon(Icons.Default.Search, contentDescription = "Filtra")
                    }
                }
            )
        },
        bottomBar = { TTBottomNavigation(navController) },
        floatingActionButton = {
            FloatingActionButton(
                onClick = { navController.navigate(Screen.AddEditCloth.createRoute()) }
            ) {
                Icon(Icons.Default.Add, contentDescription = "Aggiungi vestito")
            }
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            OutlinedTextField(
                value = searchQuery,
                onValueChange = { viewModel.updateSearchQuery(it) },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                placeholder = { Text("Cerca vestiti...") },
                leadingIcon = { Icon(Icons.Default.Search, contentDescription = null) }
            )

            if (clothes.isEmpty()) {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "Nessun vestito trovato",
                        style = MaterialTheme.typography.bodyLarge
                    )
                }
            } else {
                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    contentPadding = PaddingValues(16.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    items(clothes) { cloth ->
                        ClothItem(
                            cloth = cloth,
                            onClothClick = { 
                                navController.navigate(Screen.ClothDetail.createRoute(cloth.id))
                            },
                            onDeleteClick = { viewModel.deleteCloth(cloth) }
                        )
                    }
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun ClothItem(
    cloth: Clothes,
    onClothClick: () -> Unit,
    onDeleteClick: () -> Unit
) {
    Card(
        onClick = onClothClick,
        modifier = Modifier.fillMaxWidth()
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column {
                Text(
                    text = cloth.name,
                    style = MaterialTheme.typography.titleMedium
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = cloth.category,
                    style = MaterialTheme.typography.bodyMedium
                )
            }
            IconButton(onClick = onDeleteClick) {
                Icon(Icons.Default.Delete, contentDescription = "Elimina")
            }
        }
    }
} 