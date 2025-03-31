package com.valcan.tt.ui.screens.clothes

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
import com.valcan.tt.data.model.Clothes
import com.valcan.tt.ui.components.CameraDialog
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Search
import androidx.compose.ui.text.font.FontWeight

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ClothesScreen(
    @Suppress("UNUSED_PARAMETER") navController: NavController,
    viewModel: ClothesViewModel = hiltViewModel()
) {
    var showAddDialog by remember { mutableStateOf(false) }
    var searchQuery by remember { mutableStateOf("") }
    val clothesList by viewModel.clothes.collectAsState()
    var clothToEdit by remember { mutableStateOf<Clothes?>(null) }
    
    Box(modifier = Modifier.fillMaxSize()) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Titolo
            Text(
                text = "I tuoi vestiti",
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
                placeholder = { Text("Cerca vestiti...") },
                leadingIcon = { Icon(Icons.Default.Search, contentDescription = null) },
                singleLine = true
            )
            
            Spacer(modifier = Modifier.height(16.dp))
            
            // Lista vestiti
            LazyColumn(
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                items(clothesList) { cloth ->
                    ClothItem(
                        cloth = cloth,
                        onEdit = { clothToEdit = cloth },
                        onDelete = { viewModel.deleteCloth(cloth) }
                    )
                }
            }
        }
        
        // FAB per aggiungere un nuovo vestito
        FloatingActionButton(
            onClick = { showAddDialog = true },
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .padding(16.dp)
        ) {
            Image(
                painter = painterResource(id = R.drawable.ic_add_kawaii),
                contentDescription = "Aggiungi vestito",
                modifier = Modifier.size(40.dp)
            )
        }
    }
    
    // Dialog per aggiungere un nuovo vestito
    if (showAddDialog) {
        ClothDialog(
            onDismiss = { showAddDialog = false },
            onConfirm = { name, category, color, season, position, wardrobeId, imageUrl ->
                viewModel.addCloth(Clothes(
                    name = name,
                    category = category,
                    color = color,
                    season = season,
                    position = position,
                    wardrobeId = wardrobeId,
                    imageUrl = imageUrl
                ))
                showAddDialog = false
            }
        )
    }
    
    // Dialog per modifica vestito
    clothToEdit?.let { cloth ->
        ClothDialog(
            onDismiss = { clothToEdit = null },
            onConfirm = { name, category, color, season, position, wardrobeId, imageUrl ->
                viewModel.updateCloth(cloth.copy(
                    name = name,
                    category = category,
                    color = color,
                    season = season,
                    position = position,
                    wardrobeId = wardrobeId,
                    imageUrl = imageUrl
                ))
                clothToEdit = null
            },
            initialName = cloth.name,
            initialCategory = cloth.category,
            initialColor = cloth.color,
            initialSeason = cloth.season,
            initialPosition = cloth.position,
            initialWardrobeId = cloth.wardrobeId,
            initialImageUrl = cloth.imageUrl
        )
    }
}

@Composable
fun ClothItem(
    cloth: Clothes,
    onEdit: () -> Unit,
    onDelete: () -> Unit
) {
    var showDeleteConfirmation by remember { mutableStateOf(false) }
    var showDetailDialog by remember { mutableStateOf(false) }
    
    // Per ottenere il nome dell'armadio
    val clothesViewModel: ClothesViewModel = hiltViewModel()
    val wardrobes by clothesViewModel.wardrobes.collectAsState()
    val wardrobeName = wardrobes.find { it.wardrobeId == cloth.wardrobeId }?.name ?: "Non in armadio"

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
            // Immagine del vestito
            Box(
                modifier = Modifier
                    .size(80.dp)
                    .clip(RoundedCornerShape(8.dp))
                    .background(MaterialTheme.colorScheme.surfaceVariant)
            ) {
                if (cloth.imageUrl != null) {
                    AsyncImage(
                        model = cloth.imageUrl,
                        contentDescription = null,
                        modifier = Modifier.fillMaxSize(),
                        contentScale = ContentScale.Crop
                    )
                } else {
                    Image(
                        painter = painterResource(id = R.drawable.ic_clothes_kawaii),
                        contentDescription = null,
                        modifier = Modifier
                            .size(40.dp)
                            .align(Alignment.Center)
                    )
                }
            }

            // Informazioni vestito
            Column(
                modifier = Modifier
                    .weight(1f)
                    .padding(horizontal = 16.dp)
            ) {
                Text(
                    text = cloth.name,
                    style = MaterialTheme.typography.titleMedium
                )
                Text(
                    text = "Categoria: ${cloth.category}",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f)
                )
                Text(
                    text = "Colore: ${cloth.color}",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f)
                )
                Text(
                    text = "Stagione: ${cloth.season}",
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

    // Dialog di conferma eliminazione
    if (showDeleteConfirmation) {
        AlertDialog(
            onDismissRequest = { showDeleteConfirmation = false },
            title = { Text("Conferma eliminazione") },
            text = { Text("Sei sicuro di voler eliminare il vestito ${cloth.name}?") },
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
            title = { Text(cloth.name, style = MaterialTheme.typography.headlineSmall) },
            text = {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(8.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    // Immagine del vestito
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(220.dp)
                            .clip(RoundedCornerShape(12.dp))
                            .background(MaterialTheme.colorScheme.surfaceVariant)
                    ) {
                        if (cloth.imageUrl != null) {
                            AsyncImage(
                                model = cloth.imageUrl,
                                contentDescription = null,
                                modifier = Modifier.fillMaxSize(),
                                contentScale = ContentScale.Crop
                            )
                        } else {
                            Image(
                                painter = painterResource(id = R.drawable.ic_clothes_kawaii),
                                contentDescription = null,
                                modifier = Modifier
                                    .size(80.dp)
                                    .align(Alignment.Center)
                            )
                        }
                    }
                    
                    Spacer(modifier = Modifier.height(16.dp))
                    
                    // Dettagli del vestito
                    Column(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalAlignment = Alignment.Start
                    ) {
                        DetailRow(label = "Categoria", value = cloth.category)
                        DetailRow(label = "Colore", value = cloth.color)
                        DetailRow(label = "Stagione", value = cloth.season)
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
fun ClothDialog(
    onDismiss: () -> Unit,
    onConfirm: (name: String, category: String, color: String, season: String, position: String, wardrobeId: Long?, imageUrl: String?) -> Unit,
    initialName: String = "",
    initialCategory: String = "",
    initialColor: String = "",
    initialSeason: String = "",
    initialPosition: String = "",
    initialWardrobeId: Long? = null,
    initialImageUrl: String? = null,
    viewModel: ClothesViewModel = hiltViewModel()
) {
    var name by remember { mutableStateOf(initialName) }
    var category by remember { mutableStateOf(initialCategory) }
    var color by remember { mutableStateOf(initialColor) }
    var season by remember { mutableStateOf(initialSeason) }
    var position by remember { mutableStateOf(initialPosition) }
    var wardrobeId by remember { mutableStateOf(initialWardrobeId) }
    var imageUrl by remember { mutableStateOf(initialImageUrl) }
    var showError by remember { mutableStateOf(false) }
    var showNewWardrobeDialog by remember { mutableStateOf(false) }
    var showCamera by remember { mutableStateOf(false) }
    
    val wardrobes by viewModel.wardrobes.collectAsState(initial = emptyList())
    var expandedSeason by remember { mutableStateOf(false) }
    var expandedWardrobe by remember { mutableStateOf(false) }
    var expandedCategory by remember { mutableStateOf(false) }
    
    val categories by viewModel.categories.collectAsState(initial = emptyList())
    var showCategoryDeleteConfirmation by remember { mutableStateOf<String?>(null) }
    
    val seasons = listOf("primavera", "estate", "autunno", "inverno", "tutte le stagioni")
    val scrollState = rememberScrollState()

    LaunchedEffect(wardrobes) {
        val lastWardrobe = wardrobes.maxByOrNull { it.wardrobeId }
        if (lastWardrobe != null && wardrobeId == null) {
            wardrobeId = lastWardrobe.wardrobeId
        }
    }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Text(
                text = if (initialName.isEmpty()) "Nuovo Vestito" else "Modifica Vestito",
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

                ExposedDropdownMenuBox(
                    expanded = expandedCategory,
                    onExpandedChange = { expandedCategory = !expandedCategory }
                ) {
                    OutlinedTextField(
                        value = category,
                        onValueChange = { category = it },
                        label = { Text("Categoria") },
                        trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expandedCategory) },
                        modifier = Modifier.fillMaxWidth().menuAnchor(),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = MaterialTheme.colorScheme.primary,
                            focusedLabelColor = MaterialTheme.colorScheme.primary
                        )
                    )
                    
                    if (categories.isNotEmpty()) {
                        ExposedDropdownMenu(
                            expanded = expandedCategory,
                            onDismissRequest = { expandedCategory = false }
                        ) {
                            categories.forEach { savedCategory ->
                                DropdownMenuItem(
                                    text = {
                                        Row(
                                            modifier = Modifier.fillMaxWidth(),
                                            horizontalArrangement = Arrangement.SpaceBetween,
                                            verticalAlignment = Alignment.CenterVertically
                                        ) {
                                            Text(savedCategory)
                                            IconButton(
                                                onClick = { showCategoryDeleteConfirmation = savedCategory },
                                                modifier = Modifier.size(24.dp)
                                            ) {
                                                Image(
                                                    painter = painterResource(id = R.drawable.ic_delete),
                                                    contentDescription = "Elimina categoria"
                                                )
                                            }
                                        }
                                    },
                                    onClick = {
                                        category = savedCategory
                                        expandedCategory = false
                                    }
                                )
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(8.dp))

                OutlinedTextField(
                    value = color,
                    onValueChange = { color = it },
                    label = { Text("Colore") },
                    modifier = Modifier.fillMaxWidth(),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = MaterialTheme.colorScheme.primary,
                        focusedLabelColor = MaterialTheme.colorScheme.primary
                    )
                )

                Spacer(modifier = Modifier.height(8.dp))

                ExposedDropdownMenuBox(
                    expanded = expandedSeason,
                    onExpandedChange = { expandedSeason = !expandedSeason }
                ) {
                    OutlinedTextField(
                        value = season.ifBlank { "tutte le stagioni" },
                        onValueChange = {},
                        readOnly = true,
                        label = { Text("Stagione") },
                        trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expandedSeason) },
                        modifier = Modifier.fillMaxWidth().menuAnchor(),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = MaterialTheme.colorScheme.primary,
                            focusedLabelColor = MaterialTheme.colorScheme.primary
                        )
                    )
                    ExposedDropdownMenu(
                        expanded = expandedSeason,
                        onDismissRequest = { expandedSeason = false }
                    ) {
                        seasons.forEach { option ->
                            DropdownMenuItem(
                                text = { Text(option) },
                                onClick = {
                                    season = option
                                    expandedSeason = false
                                }
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(8.dp))

                OutlinedTextField(
                    value = position,
                    onValueChange = { position = it },
                    label = { Text("Posizione") },
                    modifier = Modifier.fillMaxWidth(),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = MaterialTheme.colorScheme.primary,
                        focusedLabelColor = MaterialTheme.colorScheme.primary
                    )
                )

                Spacer(modifier = Modifier.height(8.dp))

                ExposedDropdownMenuBox(
                    expanded = expandedWardrobe,
                    onExpandedChange = { expandedWardrobe = !expandedWardrobe }
                ) {
                    OutlinedTextField(
                        value = wardrobes.find { it.wardrobeId == wardrobeId }?.name ?: "",
                        onValueChange = {},
                        readOnly = true,
                        label = { Text("Armadio") },
                        trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expandedWardrobe) },
                        modifier = Modifier.fillMaxWidth().menuAnchor(),
                        isError = showError && wardrobeId == null,
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = MaterialTheme.colorScheme.primary,
                            focusedLabelColor = MaterialTheme.colorScheme.primary
                        )
                    )
                    ExposedDropdownMenu(
                        expanded = expandedWardrobe,
                        onDismissRequest = { expandedWardrobe = false }
                    ) {
                        wardrobes.forEach { wardrobe ->
                            DropdownMenuItem(
                                text = { Text(wardrobe.name) },
                                onClick = {
                                    wardrobeId = wardrobe.wardrobeId
                                    expandedWardrobe = false
                                }
                            )
                        }
                        
                        DropdownMenuItem(
                            text = { 
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.Center,
                                    modifier = Modifier.fillMaxWidth()
                                ) {
                                    Text(
                                        "Aggiungi nuovo armadio", 
                                        color = MaterialTheme.colorScheme.primary
                                    )
                                }
                            },
                            onClick = {
                                expandedWardrobe = false
                                showNewWardrobeDialog = true
                            }
                        )
                    }
                }
                if (showError && wardrobeId == null) {
                    Text(
                        "L'armadio è obbligatorio",
                        color = MaterialTheme.colorScheme.error,
                        style = MaterialTheme.typography.bodySmall
                    )
                }
            }
        },
        confirmButton = {
            TextButton(
                onClick = {
                    if (name.isBlank() || wardrobeId == null) {
                        showError = true
                    } else {
                        val confirmedSeason = season.ifBlank { "tutte le stagioni" }
                        
                        if (category.isNotBlank() && !categories.contains(category)) {
                            viewModel.addCategory(category)
                        }
                        
                        if (wardrobeId == -1L) {
                            val newWardrobeName = wardrobes.find { it.wardrobeId == wardrobeId }?.name ?: "Nuovo Armadio"
                            viewModel.addWardrobe(newWardrobeName, "")
                            onConfirm(name, category, color, confirmedSeason, position, null, imageUrl)
                        } else {
                            onConfirm(name, category, color, confirmedSeason, position, wardrobeId, imageUrl)
                        }
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

    showCategoryDeleteConfirmation?.let { categoryToDelete ->
        AlertDialog(
            onDismissRequest = { showCategoryDeleteConfirmation = null },
            title = { Text("Conferma eliminazione") },
            text = { Text("Sei sicuro di voler eliminare la categoria \"$categoryToDelete\"?") },
            confirmButton = {
                TextButton(
                    onClick = {
                        viewModel.removeCategory(categoryToDelete)
                        showCategoryDeleteConfirmation = null
                    }
                ) {
                    Text("Elimina")
                }
            },
            dismissButton = {
                TextButton(
                    onClick = { showCategoryDeleteConfirmation = null }
                ) {
                    Text("Annulla")
                }
            }
        )
    }

    if (showNewWardrobeDialog) {
        WardrobeDialog(
            onDismiss = { showNewWardrobeDialog = false },
            onConfirm = { wardrobeName, description ->
                viewModel.addWardrobe(wardrobeName, description)
                showNewWardrobeDialog = false
            }
        )
    }

    if (showCamera) {
        CameraDialog(
            onImageCaptured = { uri ->
                imageUrl = uri.toString()
                showCamera = false
            },
            onDismiss = { showCamera = false }
        )
    }
}

@Composable
fun WardrobeDialog(
    onDismiss: () -> Unit,
    onConfirm: (name: String, description: String) -> Unit
) {
    var name by remember { mutableStateOf("") }
    var description by remember { mutableStateOf("") }
    var showError by remember { mutableStateOf(false) }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Nuovo Armadio") },
        text = {
            Column(
                modifier = Modifier.fillMaxWidth()
            ) {
                OutlinedTextField(
                    value = name,
                    onValueChange = { name = it },
                    label = { Text("Nome") },
                    modifier = Modifier.fillMaxWidth(),
                    isError = showError && name.isBlank()
                )
                if (showError && name.isBlank()) {
                    Text(
                        text = "Il nome è obbligatorio",
                        color = MaterialTheme.colorScheme.error,
                        style = MaterialTheme.typography.bodySmall
                    )
                }
                
                Spacer(modifier = Modifier.height(8.dp))
                
                OutlinedTextField(
                    value = description,
                    onValueChange = { description = it },
                    label = { Text("Descrizione") },
                    modifier = Modifier.fillMaxWidth()
                )
            }
        },
        confirmButton = {
            TextButton(
                onClick = {
                    if (name.isBlank()) {
                        showError = true
                    } else {
                        onConfirm(name, description)
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
}