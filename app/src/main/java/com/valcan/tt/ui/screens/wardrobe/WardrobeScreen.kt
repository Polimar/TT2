package com.valcan.tt.ui.screens.wardrobe

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
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.valcan.tt.R
import com.valcan.tt.data.model.Wardrobe
import com.valcan.tt.ui.components.TTBottomNavigation
import com.valcan.tt.ui.viewmodel.WardrobeViewModel
import java.text.SimpleDateFormat
import java.util.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun WardrobeScreen(
    navController: NavController,
    viewModel: WardrobeViewModel = hiltViewModel()
) {
    var showNewWardrobeDialog by remember { mutableStateOf(false) }
    val wardrobes by viewModel.wardrobes.collectAsState(initial = emptyList())

    Scaffold(
        bottomBar = { TTBottomNavigation(navController = navController) },
        floatingActionButton = {
            FloatingActionButton(
                onClick = { showNewWardrobeDialog = true },
                containerColor = MaterialTheme.colorScheme.primary,
                contentColor = MaterialTheme.colorScheme.onPrimary
            ) {
                Image(
                    painter = painterResource(id = R.drawable.ic_add_kawaii),
                    contentDescription = "Aggiungi Armadio",
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
                text = "Armadi",
                style = MaterialTheme.typography.headlineLarge,
                color = MaterialTheme.colorScheme.primary,
                modifier = Modifier.padding(vertical = 24.dp)
            )

            // Lista degli armadi
            LazyColumn(
                verticalArrangement = Arrangement.spacedBy(8.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                items(wardrobes) { wardrobe ->
                    WardrobeItem(
                        wardrobe = wardrobe,
                        onEdit = { viewModel.showEditWardrobe(wardrobe) },
                        onDelete = { viewModel.deleteWardrobe(wardrobe) }
                    )
                }
            }
        }
    }

    // Dialog per nuovo armadio
    if (showNewWardrobeDialog) {
        WardrobeDialog(
            onDismiss = { showNewWardrobeDialog = false },
            onConfirm = { name, description ->
                viewModel.createWardrobe(name, description)
                showNewWardrobeDialog = false
            }
        )
    }

    // Dialog per modifica armadio
    viewModel.showEditDialog.collectAsState().value?.let { wardrobeToEdit ->
        WardrobeDialog(
            onDismiss = { viewModel.hideEditDialog() },
            onConfirm = { name, description ->
                viewModel.updateWardrobe(wardrobeToEdit.wardrobeId, name, description)
            },
            initialName = wardrobeToEdit.name,
            initialDescription = wardrobeToEdit.description ?: ""
        )
    }
}

@Composable
fun WardrobeItem(
    wardrobe: Wardrobe,
    onEdit: () -> Unit,
    onDelete: () -> Unit
) {
    var showDeleteConfirmation by remember { mutableStateOf(false) }
    val dateFormat = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())

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
            // Informazioni armadio
            Column(
                modifier = Modifier.weight(1f)
            ) {
                Text(
                    text = wardrobe.name,
                    style = MaterialTheme.typography.titleMedium
                )
                Text(
                    text = wardrobe.description ?: "",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f)
                )
                Text(
                    text = "Creato il ${dateFormat.format(wardrobe.createdAt)}",
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
            text = { Text("Sei sicuro di voler eliminare l'armadio ${wardrobe.name}?") },
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
fun WardrobeDialog(
    onDismiss: () -> Unit,
    onConfirm: (name: String, description: String) -> Unit,
    initialName: String = "",
    initialDescription: String = ""
) {
    var name by remember { mutableStateOf(initialName) }
    var description by remember { mutableStateOf(initialDescription) }
    var showError by remember { mutableStateOf(false) }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { 
            Text(
                text = if (initialName.isEmpty()) "Nuovo Armadio" else "Modifica Armadio",
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
                    label = { Text("Nome Armadio") },
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
                    value = description,
                    onValueChange = { description = it },
                    label = { Text("Descrizione") },
                    modifier = Modifier.fillMaxWidth(),
                    isError = showError && description.isBlank(),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = MaterialTheme.colorScheme.primary,
                        focusedLabelColor = MaterialTheme.colorScheme.primary
                    )
                )
                
                if (showError && description.isBlank()) {
                    Text(
                        "La descrizione è obbligatoria",
                        color = MaterialTheme.colorScheme.error,
                        style = MaterialTheme.typography.bodySmall
                    )
                }
            }
        },
        confirmButton = {
            TextButton(
                onClick = {
                    if (name.isBlank() || description.isBlank()) {
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