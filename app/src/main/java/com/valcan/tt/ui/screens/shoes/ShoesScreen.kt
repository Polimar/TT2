package com.valcan.tt.ui.screens.shoes

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
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

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ShoesScreen(
    navController: NavController,
    viewModel: ShoesViewModel = hiltViewModel()
) {
    var showNewShoeDialog by remember { mutableStateOf(false) }
    val shoes by viewModel.shoes.collectAsState(initial = emptyList())
    var shoeToEdit by remember { mutableStateOf<Shoes?>(null) }

    Scaffold(
        bottomBar = { TTBottomNavigation(navController = navController) },
        floatingActionButton = {
            FloatingActionButton(
                onClick = { showNewShoeDialog = true },
                containerColor = MaterialTheme.colorScheme.primary,
                contentColor = MaterialTheme.colorScheme.onPrimary
            ) {
                Image(
                    painter = painterResource(id = R.drawable.ic_add_kawaii),
                    contentDescription = "Aggiungi Scarpe",
                    modifier = Modifier.size(24.dp)
                )
            }
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = "Scarpe",
                style = MaterialTheme.typography.headlineLarge,
                color = MaterialTheme.colorScheme.primary,
                modifier = Modifier.padding(vertical = 24.dp)
            )

            LazyColumn(
                verticalArrangement = Arrangement.spacedBy(8.dp),
                modifier = Modifier.fillMaxWidth()
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
    }

    if (showNewShoeDialog) {
        ShoeDialog(
            onDismiss = { showNewShoeDialog = false },
            onConfirm = { name, color, type, season, imageUrl, wardrobeId ->
                viewModel.addShoe(
                    Shoes(
                        name = name,
                        color = color,
                        type = type,
                        season = season,
                        imageUrl = imageUrl,
                        wardrobeId = wardrobeId,
                        userId = 0 // Sarà sostituito dal viewModel
                    )
                )
                showNewShoeDialog = false
            }
        )
    }

    shoeToEdit?.let { shoe ->
        ShoeDialog(
            onDismiss = { shoeToEdit = null },
            onConfirm = { name, color, type, season, imageUrl, wardrobeId ->
                viewModel.updateShoe(
                    shoe.copy(
                        name = name,
                        color = color,
                        type = type,
                        season = season,
                        imageUrl = imageUrl,
                        wardrobeId = wardrobeId
                    )
                )
                shoeToEdit = null
            },
            initialName = shoe.name,
            initialColor = shoe.color,
            initialType = shoe.type,
            initialSeason = shoe.season,
            initialImageUrl = shoe.imageUrl,
            initialWardrobeId = shoe.wardrobeId
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

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(16.dp)),
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
                        .clickable(onClick = onEdit)
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
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ShoeDialog(
    onDismiss: () -> Unit,
    onConfirm: (name: String, color: String?, type: String?, season: String?, imageUrl: String?, wardrobeId: Long?) -> Unit,
    initialName: String = "",
    initialType: String? = null,
    initialColor: String? = null,
    initialSeason: String? = null,
    initialWardrobeId: Long? = null,
    initialImageUrl: String? = null,
    viewModel: ShoesViewModel = hiltViewModel()
) {
    var name by remember { mutableStateOf(initialName) }
    var type by remember { mutableStateOf(initialType) }
    var color by remember { mutableStateOf(initialColor) }
    var season by remember { mutableStateOf(initialSeason) }
    var wardrobeId by remember { mutableStateOf(initialWardrobeId) }
    var imageUrl by remember { mutableStateOf(initialImageUrl) }
    var showError by remember { mutableStateOf(false) }
    var showNewWardrobeDialog by remember { mutableStateOf(false) }
    var showCamera by remember { mutableStateOf(false) }
    
    val wardrobes by viewModel.wardrobes.collectAsState(initial = emptyList())
    var expandedSeason by remember { mutableStateOf(false) }
    var expandedWardrobe by remember { mutableStateOf(false) }
    
    val seasons = listOf("primavera", "estate", "autunno", "inverno", "tutte le stagioni")
    
    // Gestisce il ritorno dal dialog di inserimento armadio
    LaunchedEffect(wardrobes) {
        // Se è appena stato aggiunto un armadio, selezionalo automaticamente
        val lastWardrobe = wardrobes.maxByOrNull { it.wardrobeId }
        if (lastWardrobe != null && wardrobeId == null) {
            wardrobeId = lastWardrobe.wardrobeId
        }
    }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Text(
                text = if (initialName.isEmpty()) "Nuova Scarpa" else "Modifica Scarpa",
                style = MaterialTheme.typography.titleLarge,
                modifier = Modifier.fillMaxWidth(),
                textAlign = TextAlign.Center
            )
        },
        text = {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(8.dp)
            ) {
                // Immagine e pulsante fotocamera
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
                            contentDescription = "Scatta foto",
                        )
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Nome
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

                // Tipo
                OutlinedTextField(
                    value = type ?: "",
                    onValueChange = { type = it.ifBlank { null } },
                    label = { Text("Tipo") },
                    modifier = Modifier.fillMaxWidth(),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = MaterialTheme.colorScheme.primary,
                        focusedLabelColor = MaterialTheme.colorScheme.primary
                    )
                )

                Spacer(modifier = Modifier.height(8.dp))

                // Colore
                OutlinedTextField(
                    value = color ?: "",
                    onValueChange = { color = it.ifBlank { null } },
                    label = { Text("Colore") },
                    modifier = Modifier.fillMaxWidth(),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = MaterialTheme.colorScheme.primary,
                        focusedLabelColor = MaterialTheme.colorScheme.primary
                    )
                )

                Spacer(modifier = Modifier.height(8.dp))

                // Stagione (Dropdown)
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

                // Armadio (Dropdown)
                if (wardrobes.isEmpty()) {
                    OutlinedButton(
                        onClick = { showNewWardrobeDialog = true },
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text("Aggiungi un nuovo armadio")
                    }
                } else {
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
                            
                            // Opzione per aggiungere un nuovo armadio
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
                }
            }
        },
        confirmButton = {
            TextButton(
                onClick = {
                    if (name.isBlank()) {
                        showError = true
                    } else {
                        onConfirm(name, color, type, season, imageUrl, wardrobeId)
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

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun WardrobeDialog(
    onDismiss: () -> Unit,
    onConfirm: (name: String, description: String?) -> Unit
) {
    var name by remember { mutableStateOf("") }
    var description by remember { mutableStateOf("") }
    var showError by remember { mutableStateOf(false) }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Text(
                text = "Nuovo Armadio",
                style = MaterialTheme.typography.titleLarge,
                modifier = Modifier.fillMaxWidth(),
                textAlign = TextAlign.Center
            )
        },
        text = {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(8.dp)
            ) {
                OutlinedTextField(
                    value = name,
                    onValueChange = { name = it },
                    label = { Text("Nome Armadio") },
                    modifier = Modifier.fillMaxWidth(),
                    isError = showError && name.isBlank(),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = MaterialTheme.colorScheme.primary,
                        focusedLabelColor = MaterialTheme.colorScheme.primary
                    )
                )

                Spacer(modifier = Modifier.height(8.dp))

                OutlinedTextField(
                    value = description,
                    onValueChange = { description = it },
                    label = { Text("Descrizione (opzionale)") },
                    modifier = Modifier.fillMaxWidth(),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = MaterialTheme.colorScheme.primary,
                        focusedLabelColor = MaterialTheme.colorScheme.primary
                    )
                )
            }
        },
        confirmButton = {
            TextButton(
                onClick = {
                    if (name.isBlank()) {
                        showError = true
                    } else {
                        onConfirm(name, description.ifBlank { null })
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