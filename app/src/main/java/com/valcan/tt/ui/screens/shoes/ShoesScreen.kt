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
            onConfirm = { name, brand, size, color, type, season, price, imageUrl, wardrobeId ->
                viewModel.addShoe(
                    Shoes(
                        name = name,
                        brand = brand,
                        size = size,
                        color = color,
                        type = type,
                        season = season,
                        price = price,
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
            onConfirm = { name, brand, size, color, type, season, price, imageUrl, wardrobeId ->
                viewModel.updateShoe(
                    shoe.copy(
                        name = name,
                        brand = brand,
                        size = size,
                        color = color,
                        type = type,
                        season = season,
                        price = price,
                        imageUrl = imageUrl,
                        wardrobeId = wardrobeId
                    )
                )
                shoeToEdit = null
            },
            initialName = shoe.name,
            initialBrand = shoe.brand,
            initialSize = shoe.size,
            initialColor = shoe.color,
            initialType = shoe.type,
            initialSeason = shoe.season,
            initialPrice = shoe.price,
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
                    text = "Marca: ${shoe.brand}",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f)
                )
                Text(
                    text = "Taglia: ${shoe.size}",
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
    onConfirm: (
        name: String,
        brand: String,
        size: String,
        color: String?,
        type: String?,
        season: String?,
        price: Double?,
        imageUrl: String?,
        wardrobeId: Long?
    ) -> Unit,
    initialName: String = "",
    initialBrand: String = "",
    initialSize: String = "",
    initialColor: String? = null,
    initialType: String? = null,
    initialSeason: String? = null,
    initialPrice: Double? = null,
    initialImageUrl: String? = null,
    initialWardrobeId: Long? = null,
    viewModel: ShoesViewModel = hiltViewModel()
) {
    var name by remember { mutableStateOf(initialName) }
    var brand by remember { mutableStateOf(initialBrand) }
    var size by remember { mutableStateOf(initialSize) }
    var color by remember { mutableStateOf(initialColor ?: "") }
    var type by remember { mutableStateOf(initialType ?: "") }
    var season by remember { mutableStateOf(initialSeason ?: "") }
    var price by remember { mutableStateOf(initialPrice?.toString() ?: "") }
    var imageUrl by remember { mutableStateOf(initialImageUrl) }
    var wardrobeId by remember { mutableStateOf(initialWardrobeId) }
    var showError by remember { mutableStateOf(false) }
    var showNewWardrobeDialog by remember { mutableStateOf(false) }
    var showCamera by remember { mutableStateOf(false) }

    val wardrobes by viewModel.wardrobes.collectAsState(initial = emptyList())
    var expandedSeason by remember { mutableStateOf(false) }
    var expandedType by remember { mutableStateOf(false) }
    var expandedWardrobe by remember { mutableStateOf(false) }

    val seasons = listOf("primavera", "estate", "autunno", "inverno", "tutte le stagioni")
    val types = listOf("sneakers", "eleganti", "sportive", "sandali", "stivali", "altro")

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
                            contentDescription = "Scatta foto"
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

                Spacer(modifier = Modifier.height(8.dp))

                // Marca
                OutlinedTextField(
                    value = brand,
                    onValueChange = { brand = it },
                    label = { Text("Marca") },
                    modifier = Modifier.fillMaxWidth(),
                    isError = showError && brand.isBlank(),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = MaterialTheme.colorScheme.primary,
                        focusedLabelColor = MaterialTheme.colorScheme.primary
                    )
                )

                Spacer(modifier = Modifier.height(8.dp))

                // Taglia
                OutlinedTextField(
                    value = size,
                    onValueChange = { size = it },
                    label = { Text("Taglia") },
                    modifier = Modifier.fillMaxWidth(),
                    isError = showError && size.isBlank(),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = MaterialTheme.colorScheme.primary,
                        focusedLabelColor = MaterialTheme.colorScheme.primary
                    )
                )

                Spacer(modifier = Modifier.height(8.dp))

                // Colore
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

                // Tipo (Dropdown)
                ExposedDropdownMenuBox(
                    expanded = expandedType,
                    onExpandedChange = { expandedType = !expandedType }
                ) {
                    OutlinedTextField(
                        value = type,
                        onValueChange = {},
                        readOnly = true,
                        label = { Text("Tipo") },
                        trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expandedType) },
                        modifier = Modifier.fillMaxWidth().menuAnchor(),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = MaterialTheme.colorScheme.primary,
                            focusedLabelColor = MaterialTheme.colorScheme.primary
                        )
                    )
                    ExposedDropdownMenu(
                        expanded = expandedType,
                        onDismissRequest = { expandedType = false }
                    ) {
                        types.forEach { option ->
                            DropdownMenuItem(
                                text = { Text(option) },
                                onClick = {
                                    type = option
                                    expandedType = false
                                }
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(8.dp))

                // Stagione (Dropdown)
                ExposedDropdownMenuBox(
                    expanded = expandedSeason,
                    onExpandedChange = { expandedSeason = !expandedSeason }
                ) {
                    OutlinedTextField(
                        value = season,
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

                // Prezzo
                OutlinedTextField(
                    value = price,
                    onValueChange = { price = it },
                    label = { Text("Prezzo") },
                    modifier = Modifier.fillMaxWidth(),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = MaterialTheme.colorScheme.primary,
                        focusedLabelColor = MaterialTheme.colorScheme.primary
                    )
                )

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
                        }
                    }
                }
            }
        },
        confirmButton = {
            TextButton(
                onClick = {
                    if (name.isBlank() || brand.isBlank() || size.isBlank()) {
                        showError = true
                    } else {
                        onConfirm(
                            name,
                            brand,
                            size,
                            color.ifBlank { null },
                            type.ifBlank { null },
                            season.ifBlank { null },
                            price.toDoubleOrNull(),
                            imageUrl,
                            wardrobeId
                        )
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
        AlertDialog(
            onDismissRequest = { showNewWardrobeDialog = false },
            title = { Text("Nuovo Armadio") },
            text = {
                var wardrobeName by remember { mutableStateOf("") }
                var description by remember { mutableStateOf("") }
                
                Column {
                    OutlinedTextField(
                        value = wardrobeName,
                        onValueChange = { wardrobeName = it },
                        label = { Text("Nome") },
                        modifier = Modifier.fillMaxWidth()
                    )
                    
                    Spacer(modifier = Modifier.height(8.dp))
                    
                    OutlinedTextField(
                        value = description,
                        onValueChange = { description = it },
                        label = { Text("Descrizione") },
                        modifier = Modifier.fillMaxWidth()
                    )
                    
                    Button(
                        onClick = { 
                            viewModel.addWardrobe(wardrobeName, description.ifEmpty { null }) 
                            showNewWardrobeDialog = false
                        },
                        modifier = Modifier.align(Alignment.End).padding(top = 16.dp)
                    ) {
                        Text("Salva")
                    }
                }
            },
            confirmButton = { },
            dismissButton = {
                TextButton(onClick = { showNewWardrobeDialog = false }) {
                    Text("Annulla")
                }
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