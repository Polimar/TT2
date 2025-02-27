package com.valcan.tt.ui.screens.clothes

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.valcan.tt.ui.navigation.Screen

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ClothDetailScreen(
    navController: NavController,
    clothId: Long,
    viewModel: ClothDetailViewModel = hiltViewModel()
) {
    val cloth by viewModel.cloth.collectAsState()

    LaunchedEffect(clothId) {
        viewModel.loadCloth(clothId)
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(cloth?.name ?: "Dettaglio Vestito") },
                navigationIcon = {
                    IconButton(onClick = { navController.navigateUp() }) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Indietro")
                    }
                },
                actions = {
                    cloth?.let { currentCloth ->
                        IconButton(
                            onClick = { 
                                navController.navigate(Screen.AddEditCloth.createRoute(currentCloth.id))
                            }
                        ) {
                            Icon(Icons.Default.Edit, contentDescription = "Modifica")
                        }
                    }
                }
            )
        }
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            cloth?.let { currentCloth ->
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp)
                ) {
                    Text(
                        text = "Categoria",
                        style = MaterialTheme.typography.titleMedium
                    )
                    Text(
                        text = currentCloth.category,
                        style = MaterialTheme.typography.bodyLarge
                    )
                    
                    Spacer(modifier = Modifier.height(16.dp))
                    
                    Text(
                        text = "Stagione",
                        style = MaterialTheme.typography.titleMedium
                    )
                    Text(
                        text = currentCloth.season,
                        style = MaterialTheme.typography.bodyLarge
                    )
                }
            }
        }
    }
} 