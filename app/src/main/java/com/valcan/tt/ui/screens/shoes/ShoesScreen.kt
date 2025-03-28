package com.valcan.tt.ui.screens.shoes

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import coil.compose.AsyncImage
import com.valcan.tt.R
import com.valcan.tt.data.model.Shoes
import com.valcan.tt.ui.components.CameraDialog
import com.valcan.tt.ui.components.TTBottomNavigation
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Search
import androidx.compose.ui.text.font.FontWeight

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ShoesScreen(
    @Suppress("UNUSED_PARAMETER") navController: NavController,
    viewModel: ShoesViewModel = hiltViewModel()
) {
    var showAddDialog by remember { mutableStateOf(false) }
    var searchQuery by remember { mutableStateOf("") }
    val shoes by viewModel.shoes.collectAsState()
    val wardrobes by viewModel.wardrobes.collectAsState()
    var shoeToEdit by remember { mutableStateOf<Shoes?>(null) }
    
    Box(modifier = Modifier.fillMaxSize()) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Titolo
            Text(
                text = "Le tue scarpe",
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
                placeholder = { Text("Cerca scarpe...") },
                leadingIcon = { Icon(Icons.Default.Search, contentDescription = null) },
                singleLine = true
            )
            
            Spacer(modifier = Modifier.height(16.dp))
            
            // Lista scarpe
            LazyColumn(
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                items(shoes) { shoe ->
                    ShoeItem(
                        shoe = shoe,
                        onEdit = { shoeToEdit = shoe },
                        onDelete = { viewModel.deleteShoe(shoe) }
                    )
                }
            }
        }
        
        // FAB per aggiungere nuove scarpe
        FloatingActionButton(
            onClick = { showAddDialog = true },
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .padding(16.dp)
        ) {
            Image(
                painter = painterResource(id = R.drawable.ic_add_kawaii),
                contentDescription = "Aggiungi scarpe",
                modifier = Modifier.size(40.dp)
            )
        }
    }
    
    // Dialog per aggiungere nuove scarpe
    if (showAddDialog) {
        ShoeDialog(
            onDismiss = { showAddDialog = false },
            onConfirm = { name, type, color, season, wardrobeId, imageUrl ->
                viewModel.addShoe(Shoes(
                    name = name,
                    type = type,
                    color = color,
                    season = season,
                    wardrobeId = wardrobeId,
                    imageUrl = imageUrl
                ))
                showAddDialog = false
            },
            wardrobes = wardrobes
        )
    }
    
    // Dialog per modificare scarpe esistenti
    shoeToEdit?.let { shoe ->
        ShoeDialog(
            onDismiss = { shoeToEdit = null },
            onConfirm = { name, type, color, season, wardrobeId, imageUrl ->
                viewModel.updateShoe(shoe.copy(
                    name = name,
                    type = type,
                    color = color, 
                    season = season,
                    wardrobeId = wardrobeId,
                    imageUrl = imageUrl
                ))
                shoeToEdit = null
            },
            initialName = shoe.name,
            initialType = shoe.type ?: "",
            initialColor = shoe.color ?: "",
            initialSeason = shoe.season,
            initialWardrobeId = shoe.wardrobeId,
            initialImageUrl = shoe.imageUrl,
            wardrobes = wardrobes
        )
    }
}

@Composable
fun ShoeItem(
    shoe: Shoes,
    onEdit: () -> Unit,
    onDelete: () -> Unit
) {
    var showDeleteConfirmation by remember { mutableStateOf(false) }
    var showDetailDialog by remember { mutableStateOf(false) }
    
    // Per ottenere il nome dell'armadio
    val shoesViewModel: ShoesViewModel = hiltViewModel()
    val wardrobes by shoesViewModel.wardrobes.collectAsState()
    val wardrobeName = wardrobes.find { it.wardrobeId == shoe.wardrobeId }?.name ?: "Non in armadio"

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(16.dp))
            .clickable { showDetailDialog = !showDetailDialog },
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        )
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Immagine della scarpa
            Box(
                modifier = Modifier
                    .size(80.dp)
                    .clip(RoundedCornerShape(8.dp))
                    .background(MaterialTheme.colorScheme.surfaceVariant)
            ) {
                if (shoe.imageUrl != null) {
                    AsyncImage(
                        model = shoe.imageUrl,
                        contentDescription = null,
                        modifier = Modifier.fillMaxSize(),
                        contentScale = ContentScale.Crop
                    )
                } else {
                    Image(
                        painter = painterResource(id = R.drawable.ic_shoes_kawaii),
                        contentDescription = null,
                        modifier = Modifier
                            .size(40.dp)
                            .align(Alignment.Center)
                    )
                }
            }

            // Informazioni scarpa
            Column(
                modifier = Modifier
                    .weight(1f)
                    .padding(horizontal = 16.dp)
            ) {
                Text(
                    text = shoe.name,
                    style = MaterialTheme.typography.titleMedium
                )
                Text(
                    text = "Tipo: ${shoe.type ?: "-"}",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f)
                )
                Text(
                    text = "Colore: ${shoe.color ?: "-"}",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f)
                )
                Text(
                    text = "Stagione: ${shoe.season ?: "-"}",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f)
                )
            }

            // Icone di modifica e cancellazione
            Column(
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Image(
                    painter = painterResource(id = R.drawable.ic_edit),
                    contentDescription = "Modifica",
                    modifier = Modifier
                        .size(24.dp)
                        .clickable { 
                            onEdit()
                            showDetailDialog = false 
                        }
                )
                Image(
                    painter = painterResource(id = R.drawable.ic_delete),
                    contentDescription = "Elimina",
                    modifier = Modifier
                        .size(24.dp)
                        .clickable { showDeleteConfirmation = true }
                )
            }
        }
    }

    if (showDeleteConfirmation) {
        AlertDialog(
            onDismissRequest = { showDeleteConfirmation = false },
            title = { Text("Conferma eliminazione") },
            text = { Text("Sei sicuro di voler eliminare la scarpa ${shoe.name}?") },
            confirmButton = {
                TextButton(
                    onClick = {
                        onDelete()
                        showDeleteConfirmation = false
                    }
                ) {
                    Text("Elimina")
                }
            },
            dismissButton = {
                TextButton(
                    onClick = { showDeleteConfirmation = false }
                ) {
                    Text("Annulla")
                }
            }
        )
    }
    
    // Dialog di dettaglio
    if (showDetailDialog) {
        AlertDialog(
            onDismissRequest = { showDetailDialog = false },
            title = { Text(shoe.name, style = MaterialTheme.typography.headlineSmall) },
            text = {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(8.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    // Immagine della scarpa
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(220.dp)
                            .clip(RoundedCornerShape(12.dp))
                            .background(MaterialTheme.colorScheme.surfaceVariant)
                    ) {
                        if (shoe.imageUrl != null) {
                            AsyncImage(
                                model = shoe.imageUrl,
                                contentDescription = null,
                                modifier = Modifier.fillMaxSize(),
                                contentScale = ContentScale.Crop
                            )
                        } else {
                            Image(
                                painter = painterResource(id = R.drawable.ic_shoes_kawaii),
                                contentDescription = null,
                                modifier = Modifier
                                    .size(80.dp)
                                    .align(Alignment.Center)
                            )
                        }
                    }
                    
                    Spacer(modifier = Modifier.height(16.dp))
                    
                    // Dettagli della scarpa
                    Column(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalAlignment = Alignment.Start
                    ) {
                        DetailRow(label = "Tipo", value = shoe.type ?: "-")
                        DetailRow(label = "Colore", value = shoe.color ?: "-")
                        DetailRow(label = "Stagione", value = shoe.season ?: "-")
                        DetailRow(label = "Armadio", value = wardrobeName)
                    }
                }
            },
            confirmButton = {
                TextButton(onClick = { showDetailDialog = false }) {
                    Text("Chiudi")
                }
            }
        )
    }
}

@Composable
fun DetailRow(label: String, value: String) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(
            text = "$label:",
            style = MaterialTheme.typography.bodyMedium,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.primary
        )
        
        Text(
            text = value,
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onBackground
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ShoeDialog(
    onDismiss: () -> Unit,
    onConfirm: (name: String, type: String, color: String, season: String, wardrobeId: Long?, imageUrl: String?) -> Unit,
    initialName: String = "",
    initialType: String = "",
    initialColor: String = "",
    initialSeason: String? = null,
    initialWardrobeId: Long? = null,
    initialImageUrl: String? = null,
    wardrobes: List<com.valcan.tt.data.model.Wardrobe>,
    viewModel: ShoesViewModel = hiltViewModel()
) {
    var name by remember { mutableStateOf(initialName) }
    var type by remember { mutableStateOf(initialType) }
    var color by remember { mutableStateOf(initialColor) }
    var season by remember { mutableStateOf(initialSeason) }
    var wardrobeId by remember { mutableStateOf(initialWardrobeId) }
    var imageUrl by remember { mutableStateOf(initialImageUrl) }
    var showCamera by remember { mutableStateOf(false) }
    var showError by remember { mutableStateOf(false) }
    
    val types by viewModel.types.collectAsState()
    var expandedSeason by remember { mutableStateOf(false) }
    var expandedWardrobe by remember { mutableStateOf(false) }
    var expandedType by remember { mutableStateOf(false) }

    val seasons = listOf("primavera", "estate", "autunno", "inverno", "tutte le stagioni")
    val scrollState = rememberScrollState()

    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Text(
                text = if (initialName.isEmpty()) "Aggiungi scarpe" else "Modifica scarpe",
                style = MaterialTheme.typography.titleLarge,
                modifier = Modifier.fillMaxWidth(),
                textAlign = TextAlign.Center
            )
        },
        text = {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .heightIn(max = 400.dp)
                    .verticalScroll(scrollState)
                    .padding(8.dp)
            ) {
                // Immagine
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(120.dp)
                        .clip(RoundedCornerShape(8.dp))
                        .background(MaterialTheme.colorScheme.surfaceVariant),
                    contentAlignment = Alignment.Center
                ) {
                    if (imageUrl != null) {
                        AsyncImage(
                            model = imageUrl,
                            contentDescription = null,
                            modifier = Modifier.fillMaxSize(),
                            contentScale = ContentScale.Crop
                        )
                    }
                    IconButton(
                        onClick = { showCamera = true },
                        modifier = Modifier.align(Alignment.Center)
                    ) {
                        Image(
                            painter = painterResource(id = R.drawable.photo),
                            contentDescription = "Scatta foto"
                        )
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))
                
                // Nome scarpe
                OutlinedTextField(
                    value = name,
                    onValueChange = { name = it },
                    label = { Text("Nome") },
                    modifier = Modifier.fillMaxWidth(),
                    isError = showError && name.isBlank(),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = MaterialTheme.colorScheme.primary,
                        focusedLabelColor = MaterialTheme.colorScheme.primary
                    )
                )
                if (showError && name.isBlank()) {
                    Text(
                        "Il nome è obbligatorio",
                        color = MaterialTheme.colorScheme.error,
                        style = MaterialTheme.typography.bodySmall
                    )
                }
                
                Spacer(modifier = Modifier.height(8.dp))
                
                // Tipo (es. scarpe da ginnastica, eleganti, ecc.)
                ExposedDropdownMenuBox(
                    expanded = expandedType,
                    onExpandedChange = { expandedType = !expandedType }
                ) {
                    OutlinedTextField(
                        value = type,
                        onValueChange = { type = it },
                        label = { Text("Tipo") },
                        trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expandedType) },
                        modifier = Modifier
                            .fillMaxWidth()
                            .menuAnchor(),
                        isError = showError && type.isBlank(),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = MaterialTheme.colorScheme.primary,
                            focusedLabelColor = MaterialTheme.colorScheme.primary
                        )
                    )
                    
                    if (types.isNotEmpty()) {
                        ExposedDropdownMenu(
                            expanded = expandedType,
                            onDismissRequest = { expandedType = false }
                        ) {
                            // Opzione per aggiungere un nuovo tipo
                            if (type.isNotBlank() && !types.contains(type)) {
                                DropdownMenuItem(
                                    text = { Text("Aggiungi nuovo tipo: $type") },
                                    onClick = {
                                        viewModel.addType(type)
                                        expandedType = false
                                    }
                                )
                                Divider()
                            }
                            
                            // Tipi esistenti
                            types.forEach { savedType ->
                                DropdownMenuItem(
                                    text = {
                                        Row(
                                            modifier = Modifier.fillMaxWidth(),
                                            horizontalArrangement = Arrangement.SpaceBetween,
                                            verticalAlignment = Alignment.CenterVertically
                                        ) {
                                            Text(savedType)
                                        }
                                    },
                                    onClick = {
                                        type = savedType
                                        expandedType = false
                                    }
                                )
                            }
                        }
                    }
                }
                
                Spacer(modifier = Modifier.height(8.dp))
                
                // Colore
                OutlinedTextField(
                    value = color,
                    onValueChange = { color = it },
                    label = { Text("Colore") },
                    modifier = Modifier.fillMaxWidth(),
                    isError = showError && color.isBlank(),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = MaterialTheme.colorScheme.primary,
                        focusedLabelColor = MaterialTheme.colorScheme.primary
                    )
                )
                
                Spacer(modifier = Modifier.height(8.dp))
                
                // Stagione
                ExposedDropdownMenuBox(
                    expanded = expandedSeason,
                    onExpandedChange = { expandedSeason = !expandedSeason }
                ) {
                    OutlinedTextField(
                        value = season ?: "",
                        onValueChange = {},
                        readOnly = true,
                        label = { Text("Stagione") },
                        trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expandedSeason) },
                        modifier = Modifier
                            .fillMaxWidth()
                            .menuAnchor(),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = MaterialTheme.colorScheme.primary,
                            focusedLabelColor = MaterialTheme.colorScheme.primary
                        )
                    )
                    
                    ExposedDropdownMenu(
                        expanded = expandedSeason,
                        onDismissRequest = { expandedSeason = false }
                    ) {
                        seasons.forEach { seasonOption ->
                            DropdownMenuItem(
                                text = { Text(seasonOption) },
                                onClick = {
                                    season = seasonOption
                                    expandedSeason = false
                                }
                            )
                        }
                    }
                }
                
                Spacer(modifier = Modifier.height(8.dp))
                
                // Armadio
                ExposedDropdownMenuBox(
                    expanded = expandedWardrobe,
                    onExpandedChange = { expandedWardrobe = !expandedWardrobe }
                ) {
                    OutlinedTextField(
                        value = wardrobes.find { it.wardrobeId == wardrobeId }?.name ?: "",
                        onValueChange = { /* Ignora gli input manuali */ },
                        readOnly = true,
                        label = { Text("Armadio") },
                        trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expandedWardrobe) },
                        modifier = Modifier
                            .fillMaxWidth()
                            .menuAnchor(),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = MaterialTheme.colorScheme.primary,
                            focusedLabelColor = MaterialTheme.colorScheme.primary
                        )
                    )
                    
                    ExposedDropdownMenu(
                        expanded = expandedWardrobe,
                        onDismissRequest = { expandedWardrobe = false }
                    ) {
                        DropdownMenuItem(
                            text = { Text("Nessun armadio") },
                            onClick = {
                                wardrobeId = null
                                expandedWardrobe = false
                            }
                        )
                        
                        wardrobes.forEach { wardrobe ->
                            DropdownMenuItem(
                                text = { Text(wardrobe.name) },
                                onClick = {
                                    wardrobeId = wardrobe.wardrobeId
                                    expandedWardrobe = false
                                }
                            )
                        }
                    }
                }
            }
        },
        confirmButton = {
            TextButton(
                onClick = {
                    if (name.isBlank() || type.isBlank() || color.isBlank()) {
                        showError = true
                    } else {
                        if (type.isNotBlank() && !types.contains(type)) {
                            viewModel.addType(type)
                        }
                        onConfirm(name, type, color, season ?: "", wardrobeId, imageUrl)
                    }
                }
            ) {
                Text("Conferma")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Annulla")
            }
        }
    )
    
    // Dialog fotocamera
    if (showCamera) {
        CameraDialog(
            onImageCaptured = { imageUri ->
                imageUrl = imageUri.toString()
                showCamera = false
            },
            onDismiss = { showCamera = false }
        )
    }
} 