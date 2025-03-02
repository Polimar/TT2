package com.valcan.tt.ui.screens.clothes

import androidx.compose.foundation.Image
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
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.valcan.tt.R
import com.valcan.tt.data.model.Clothes
import com.valcan.tt.ui.components.TTBottomNavigation
import com.valcan.tt.ui.screens.clothes.ClothesViewModel
import java.text.SimpleDateFormat
import java.util.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ClothesScreen(
    navController: NavController,
    viewModel: ClothesViewModel = hiltViewModel()
) {
    var showNewClothDialog by remember { mutableStateOf(false) }
    val clothes by viewModel.clothes.collectAsState(initial = emptyList())
    var clothToEdit by remember { mutableStateOf<Clothes?>(null) }

    Scaffold(
        bottomBar = { TTBottomNavigation(navController = navController) },
        floatingActionButton = {
            FloatingActionButton(
                onClick = { showNewClothDialog = true },
                containerColor = MaterialTheme.colorScheme.primary,
                contentColor = MaterialTheme.colorScheme.onPrimary
            ) {
                Image(
                    painter = painterResource(id = R.drawable.ic_add_kawaii),
                    contentDescription = "Aggiungi Vestito",
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
            // Titolo
            Text(
                text = "Vestiti",
                style = MaterialTheme.typography.headlineLarge,
                color = MaterialTheme.colorScheme.primary,
                modifier = Modifier.padding(vertical = 24.dp)
            )

            // Lista dei vestiti
            LazyColumn(
                verticalArrangement = Arrangement.spacedBy(8.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                items(clothes) { cloth ->
                    ClothItem(
                        cloth = cloth,
                        onEdit = { clothToEdit = cloth },
                        onDelete = { viewModel.deleteCloth(cloth) }
                    )
                }
            }
        }
    }

    // Dialog per nuovo vestito
    if (showNewClothDialog) {
        ClothDialog(
            onDismiss = { showNewClothDialog = false },
            onConfirm = { name, category, season, wardrobeId ->
                viewModel.addCloth(Clothes(
                    name = name,
                    category = category,
                    season = season,
                    wardrobeId = wardrobeId
                ))
                showNewClothDialog = false
            }
        )
    }

    // Dialog per modifica vestito
    clothToEdit?.let { cloth ->
        ClothDialog(
            onDismiss = { clothToEdit = null },
            onConfirm = { name, category, season, wardrobeId ->
                viewModel.updateCloth(cloth.copy(
                    name = name,
                    category = category,
                    season = season,
                    wardrobeId = wardrobeId
                ))
                clothToEdit = null
            },
            initialName = cloth.name,
            initialCategory = cloth.category,
            initialSeason = cloth.season,
            initialWardrobeId = cloth.wardrobeId
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
            // Informazioni vestito
            Column(
                modifier = Modifier.weight(1f)
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
                    text = "Stagione: ${cloth.season}",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f)
                )
            }

            // Icone di modifica e cancellazione
            Row(
                horizontalArrangement = Arrangement.spacedBy(16.dp)
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
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ClothDialog(
    onDismiss: () -> Unit,
    onConfirm: (name: String, category: String, season: String, wardrobeId: Long?) -> Unit,
    initialName: String = "",
    initialCategory: String = "",
    initialSeason: String = "",
    initialWardrobeId: Long? = null
) {
    var name by remember { mutableStateOf(initialName) }
    var category by remember { mutableStateOf(initialCategory) }
    var season by remember { mutableStateOf(initialSeason) }
    var wardrobeId by remember { mutableStateOf(initialWardrobeId) }
    var showError by remember { mutableStateOf(false) }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Text(
                text = if (initialName.isEmpty()) "Nuovo Vestito" else "Modifica Vestito",
                style = MaterialTheme.typography.titleLarge
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
                    label = { Text("Nome Vestito") },
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

                Spacer(modifier = Modifier.height(16.dp))

                OutlinedTextField(
                    value = category,
                    onValueChange = { category = it },
                    label = { Text("Categoria") },
                    modifier = Modifier.fillMaxWidth(),
                    isError = showError && category.isBlank(),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = MaterialTheme.colorScheme.primary,
                        focusedLabelColor = MaterialTheme.colorScheme.primary
                    )
                )

                if (showError && category.isBlank()) {
                    Text(
                        "La categoria è obbligatoria",
                        color = MaterialTheme.colorScheme.error,
                        style = MaterialTheme.typography.bodySmall
                    )
                }

                Spacer(modifier = Modifier.height(16.dp))

                OutlinedTextField(
                    value = season,
                    onValueChange = { season = it },
                    label = { Text("Stagione") },
                    modifier = Modifier.fillMaxWidth(),
                    isError = showError && season.isBlank(),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = MaterialTheme.colorScheme.primary,
                        focusedLabelColor = MaterialTheme.colorScheme.primary
                    )
                )

                if (showError && season.isBlank()) {
                    Text(
                        "La stagione è obbligatoria",
                        color = MaterialTheme.colorScheme.error,
                        style = MaterialTheme.typography.bodySmall
                    )
                }
            }
        },
        confirmButton = {
            TextButton(
                onClick = {
                    if (name.isBlank() || category.isBlank() || season.isBlank()) {
                        showError = true
                    } else {
                        onConfirm(name, category, season, wardrobeId)
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