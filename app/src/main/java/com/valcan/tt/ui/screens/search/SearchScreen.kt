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
import androidx.compose.foundation.clickable
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import com.valcan.tt.R
import com.valcan.tt.ui.screens.clothes.DetailRow
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import com.valcan.tt.ui.screens.search.TYPE_ALL
import com.valcan.tt.ui.screens.search.TYPE_CLOTHES
import com.valcan.tt.ui.screens.search.TYPE_SHOES
import com.valcan.tt.ui.screens.search.SEASON_ALL
import com.valcan.tt.ui.screens.search.SEASON_SPRING
import com.valcan.tt.ui.screens.search.SEASON_SUMMER
import com.valcan.tt.ui.screens.search.SEASON_AUTUMN
import com.valcan.tt.ui.screens.search.SEASON_WINTER

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SearchScreen(
    @Suppress("UNUSED_PARAMETER") navController: NavController,
    viewModel: SearchViewModel = hiltViewModel()
) {
    // Mappatura tra etichette costanti e testi localizzati
    val typeMap = mapOf(
        TYPE_ALL to stringResource(R.string.search_all),
        TYPE_CLOTHES to stringResource(R.string.search_clothes),
        TYPE_SHOES to stringResource(R.string.search_shoes)
    )
    
    val seasonMap = mapOf(
        SEASON_ALL to stringResource(R.string.search_all_seasons),
        SEASON_SPRING to stringResource(R.string.search_spring),
        SEASON_SUMMER to stringResource(R.string.search_summer),
        SEASON_AUTUMN to stringResource(R.string.search_autumn),
        SEASON_WINTER to stringResource(R.string.search_winter)
    )
    
    // Ora usiamo queste etichette costanti per lo stato
    var selectedTypeLabel by remember { mutableStateOf(TYPE_ALL) }
    var selectedSeasonLabel by remember { mutableStateOf(SEASON_ALL) }
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
                text = stringResource(R.string.search_title),
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
                placeholder = { Text(stringResource(R.string.search_placeholder)) },
                leadingIcon = { Icon(Icons.Default.Search, contentDescription = null) },
                singleLine = true
            )

            Spacer(modifier = Modifier.height(16.dp))

            // Filtri
            FilterSection(
                typeMap = typeMap,
                seasonMap = seasonMap,
                selectedTypeLabel = selectedTypeLabel,
                onTypeSelected = { typeLabel -> 
                    selectedTypeLabel = typeLabel
                    viewModel.updateFilters(typeLabel, selectedSeasonLabel)
                },
                selectedSeasonLabel = selectedSeasonLabel,
                onSeasonSelected = { seasonLabel -> 
                    selectedSeasonLabel = seasonLabel
                    viewModel.updateFilters(selectedTypeLabel, seasonLabel)
                }
            )

            Spacer(modifier = Modifier.height(16.dp))

            // Lista risultati
            LazyColumn(
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                items(searchResults) { item ->
                    SearchResultItem(
                        item = item,
                        typeMap = typeMap,
                        onClick = { showDetailDialog = item }
                    )
                }
            }
        }
    }
    
    // Dialog di dettaglio
    showDetailDialog?.let {
        ItemDetailDialog(
            item = it,
            typeMap = typeMap,
            onDismiss = { showDetailDialog = null }
        )
    }
}

@Composable
fun FilterSection(
    typeMap: Map<String, String>,
    seasonMap: Map<String, String>,
    selectedTypeLabel: String,
    onTypeSelected: (String) -> Unit,
    selectedSeasonLabel: String,
    onSeasonSelected: (String) -> Unit
) {
    Column(
        modifier = Modifier.fillMaxWidth()
    ) {
        // Tipo (Vestiti/Scarpe)
        FilterChipGroup(
            title = stringResource(R.string.search_type),
            options = typeMap,
            selectedOption = selectedTypeLabel,
            onOptionSelected = onTypeSelected
        )

        Spacer(modifier = Modifier.height(8.dp))

        // Stagione
        FilterChipGroup(
            title = stringResource(R.string.search_season),
            options = seasonMap,
            selectedOption = selectedSeasonLabel,
            onOptionSelected = onSeasonSelected
        )
    }
}

@Composable
fun FilterChipGroup(
    title: String,
    options: Map<String, String>,
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
            options.forEach { (label, displayText) ->
                FilterChip(
                    selected = label == selectedOption,
                    onClick = { onOptionSelected(label) },
                    label = { Text(displayText) }
                )
            }
        }
    }
}

@Composable
fun SearchResultItem(
    item: SearchItem,
    typeMap: Map<String, String>,
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
                    text = stringResource(R.string.clothes_wardrobe) + ": ${item.wardrobeName ?: "-"}",
                    style = MaterialTheme.typography.bodySmall
                )
                Text(
                    text = "${typeMap[item.type] ?: item.type} - ${item.season ?: "-"}",
                    style = MaterialTheme.typography.bodySmall
                )
                if (item.color != null) {
                    Text(
                        text = stringResource(R.string.clothes_color) + ": ${item.color}",
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
    typeMap: Map<String, String>,
    onDismiss: () -> Unit
) {
    val wardrobeName = item.wardrobeName ?: stringResource(R.string.clothes_wardrobe_none)
    val clothesTypeText = stringResource(R.string.search_clothes)

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
                            painter = painterResource(id = if (item.type == "clothes") R.drawable.ic_clothes_kawaii else R.drawable.ic_shoes_kawaii),
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
                    if (item.type == "clothes") {
                        DetailRow(label = stringResource(R.string.clothes_category), value = item.category ?: "-")
                    } else {
                        DetailRow(label = stringResource(R.string.shoes_type), value = item.category ?: "-")
                    }
                    DetailRow(label = stringResource(R.string.clothes_color), value = item.color ?: "-")
                    DetailRow(label = stringResource(R.string.clothes_season), value = item.season ?: "-")
                    DetailRow(label = stringResource(R.string.clothes_wardrobe), value = wardrobeName)
                }
            }
        },
        confirmButton = {
            TextButton(onClick = onDismiss) {
                Text(stringResource(R.string.action_close))
            }
        }
    )
}