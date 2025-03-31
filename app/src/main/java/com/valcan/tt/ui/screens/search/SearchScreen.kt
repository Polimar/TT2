package com.valcan.tt.ui.screens.search

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.horizontalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import coil.compose.AsyncImage
import androidx.compose.ui.layout.ContentScale
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.MaterialTheme
import com.valcan.tt.ui.screens.clothes.ClothesViewModel
import com.valcan.tt.ui.screens.shoes.ShoesViewModel
import androidx.compose.foundation.clickable
import androidx.compose.ui.res.painterResource
import com.valcan.tt.R
import com.valcan.tt.ui.screens.clothes.DetailRow
import androidx.compose.foundation.Image
import androidx.compose.foundation.background

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SearchScreen(
    @Suppress("UNUSED_PARAMETER") navController: NavController,
    viewModel: SearchViewModel = hiltViewModel(),
    clothesViewModel: ClothesViewModel = hiltViewModel(),
    shoesViewModel: ShoesViewModel = hiltViewModel()
) {
    var selectedType by remember { mutableStateOf("Tutti") }
    var selectedSeason by remember { mutableStateOf("Tutte") }
    var searchQuery by remember { mutableStateOf("") }
    var showDetailDialog by remember { mutableStateOf<SearchItem?>(null) }
    
    val searchResults by viewModel.searchResults.collectAsState()
    
    Box(modifier = Modifier.fillMaxSize()) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Titolo
            Text(
                text = "Ricerca",
                style = MaterialTheme.typography.headlineLarge,
                color = MaterialTheme.colorScheme.primary,
                modifier = Modifier.padding(vertical = 16.dp)
            )
            
            // Campo di ricerca
            OutlinedTextField(
                value = searchQuery,
                onValueChange = { 
                    searchQuery = it
                    viewModel.updateSearchQuery(it)
                },
                modifier = Modifier.fillMaxWidth(),
                placeholder = { Text("Cerca per nome...") },
                leadingIcon = { Icon(Icons.Default.Search, contentDescription = null) },
                singleLine = true
            )

            Spacer(modifier = Modifier.height(16.dp))

            // Filtri
            FilterSection(
                selectedType = selectedType,
                onTypeSelected = { 
                    selectedType = it
                    viewModel.updateFilters(it, selectedSeason)
                },
                selectedSeason = selectedSeason,
                onSeasonSelected = { 
                    selectedSeason = it
                    viewModel.updateFilters(selectedType, it)
                }
            )

            Spacer(modifier = Modifier.height(16.dp))

            // Lista risultati
            LazyColumn(
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                items(searchResults) { item ->
                    SearchResultItem(item = item) {
                        showDetailDialog = item
                    }
                }
            }
        }
    }
    
    // Dialog di dettaglio
    showDetailDialog?.let {
        ItemDetailDialog(
            item = it,
            onDismiss = { showDetailDialog = null },
            clothesViewModel = clothesViewModel
        )
    }
}

@Composable
fun FilterSection(
    selectedType: String,
    onTypeSelected: (String) -> Unit,
    selectedSeason: String,
    onSeasonSelected: (String) -> Unit
) {
    Column(
        modifier = Modifier.fillMaxWidth()
    ) {
        // Tipo (Vestiti/Scarpe)
        FilterChipGroup(
            title = "Tipo",
            options = listOf("Tutti", "Vestiti", "Scarpe"),
            selectedOption = selectedType,
            onOptionSelected = onTypeSelected
        )

        Spacer(modifier = Modifier.height(8.dp))

        // Stagione
        FilterChipGroup(
            title = "Stagione",
            options = listOf("Tutte", "primavera", "estate", "autunno", "inverno"),
            selectedOption = selectedSeason,
            onOptionSelected = onSeasonSelected
        )
    }
}

@Composable
fun FilterChipGroup(
    title: String,
    options: List<String>,
    selectedOption: String,
    onOptionSelected: (String) -> Unit
) {
    Column {
        Text(
            text = title,
            style = MaterialTheme.typography.titleMedium,
            modifier = Modifier.padding(bottom = 4.dp)
        )
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .horizontalScroll(rememberScrollState()),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            options.forEach { option ->
                FilterChip(
                    selected = option == selectedOption,
                    onClick = { onOptionSelected(option) },
                    label = { Text(option) }
                )
            }
        }
    }
}

@Composable
fun SearchResultItem(
    item: SearchItem,
    onClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp)
            .clickable(onClick = onClick),
        shape = RoundedCornerShape(8.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Immagine
            AsyncImage(
                model = item.imageUrl,
                contentDescription = null,
                modifier = Modifier
                    .size(80.dp)
                    .clip(RoundedCornerShape(4.dp)),
                contentScale = ContentScale.Crop
            )

            Spacer(modifier = Modifier.width(12.dp))

            // Dettagli
            Column(
                modifier = Modifier.weight(1f)
            ) {
                Text(
                    text = item.name,
                    style = MaterialTheme.typography.titleMedium
                )
                Text(
                    text = "Armadio: ${item.wardrobeName ?: "-"}",
                    style = MaterialTheme.typography.bodySmall
                )
                Text(
                    text = "${item.type} - ${item.season ?: "-"}",
                    style = MaterialTheme.typography.bodySmall
                )
                if (item.color != null) {
                    Text(
                        text = "Colore: ${item.color}",
                        style = MaterialTheme.typography.bodySmall
                    )
                }
            }
        }
    }
}

/**
 * Dialog che mostra i dettagli di un SearchItem (vestito o scarpa)
 */
@Composable
fun ItemDetailDialog(
    item: SearchItem,
    onDismiss: () -> Unit,
    clothesViewModel: ClothesViewModel
) {
    val wardrobeName = item.wardrobeName ?: "Non in armadio"

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text(item.name, style = MaterialTheme.typography.headlineSmall) },
        text = {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(8.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                // Immagine dell'item
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(220.dp)
                        .clip(RoundedCornerShape(12.dp))
                        .background(MaterialTheme.colorScheme.surfaceVariant)
                ) {
                    if (item.imageUrl != null) {
                        AsyncImage(
                            model = item.imageUrl,
                            contentDescription = null,
                            modifier = Modifier.fillMaxSize(),
                            contentScale = ContentScale.Crop
                        )
                    } else {
                        Image(
                            painter = painterResource(id = if (item.type == "Vestito") R.drawable.ic_clothes_kawaii else R.drawable.ic_shoes_kawaii),
                            contentDescription = null,
                            modifier = Modifier
                                .size(80.dp)
                                .align(Alignment.Center)
                        )
                    }
                }
                
                Spacer(modifier = Modifier.height(16.dp))
                
                // Dettagli dell'item
                Column(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalAlignment = Alignment.Start
                ) {
                    if (item.type == "Vestito") {
                        DetailRow(label = "Categoria", value = item.category ?: "-")
                    } else {
                        DetailRow(label = "Tipo", value = item.category ?: "-")
                    }
                    DetailRow(label = "Colore", value = item.color ?: "-")
                    DetailRow(label = "Stagione", value = item.season ?: "-")
                    DetailRow(label = "Armadio", value = wardrobeName)
                }
            }
        },
        confirmButton = {
            TextButton(onClick = onDismiss) {
                Text("Chiudi")
            }
        }
    )
}