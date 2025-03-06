package com.valcan.tt.ui.screens.profile

import android.content.Intent
import android.media.MediaPlayer
import android.net.Uri
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.valcan.tt.R
import com.valcan.tt.data.model.User
import com.valcan.tt.ui.components.TTBottomNavigation
import com.valcan.tt.ui.components.KawaiiButton
import com.valcan.tt.ui.screens.welcome.NewUserDialog
import com.valcan.tt.ui.viewmodel.ProfileViewModel
import androidx.compose.material3.AlertDialog
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import com.valcan.tt.ui.navigation.Screen
import com.valcan.tt.ui.components.BackupRestoreDialog
import kotlinx.coroutines.delay

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProfileScreen(
    navController: NavController,
    viewModel: ProfileViewModel = hiltViewModel()
) {
    var showUserSelectionDialog by remember { mutableStateOf(false) }
    var showNewUserDialog by remember { mutableStateOf(false) }
    var preselectedGender by remember { mutableStateOf<String?>(null) }
    val users by viewModel.users.collectAsState(initial = emptyList())
    var showBackupDialog by remember { mutableStateOf(false) }
    var showCreditsDialog by remember { mutableStateOf(false) }

    Scaffold(
        bottomBar = { TTBottomNavigation(navController = navController) }
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
                text = "Impostazioni",
                style = MaterialTheme.typography.headlineLarge,
                color = MaterialTheme.colorScheme.primary,
                modifier = Modifier.padding(vertical = 24.dp)
            )

            // Griglia di impostazioni
            LazyVerticalGrid(
                columns = GridCells.Fixed(2),
                horizontalArrangement = Arrangement.spacedBy(16.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                items(6) { index ->
                    when (index) {
                        0 -> SettingButton(
                            icon = R.drawable.ic_users_kawaii,
                            contentDescription = "Selezione Utenti",
                            onClick = { showUserSelectionDialog = true }
                        )
                        1 -> SettingButton(
                            icon = R.drawable.ic_wardrobe_kawaii,
                            contentDescription = "Armadi",
                            onClick = { navController.navigate(Screen.Wardrobe.route) }
                        )
                        2 -> SettingButton(
                            icon = R.drawable.ic_add_male_kawaii,
                            contentDescription = "Nuovo Utente M",
                            onClick = { 
                                preselectedGender = "M"
                                showNewUserDialog = true
                            }
                        )
                        3 -> SettingButton(
                            icon = R.drawable.ic_add_female_kawaii,
                            contentDescription = "Nuovo Utente F",
                            onClick = { 
                                preselectedGender = "F"
                                showNewUserDialog = true
                            }
                        )
                        4 -> SettingButton(
                            icon = R.drawable.ic_backup_kawaii,
                            contentDescription = "Backup",
                            onClick = { showBackupDialog = true }
                        )
                        5 -> SettingButton(
                            icon = R.drawable.ic_info_kawaii,
                            contentDescription = "Info App",
                            onClick = { showCreditsDialog = true }
                        )
                    }
                }
            }
        }
    }

    if (showUserSelectionDialog) {
        ProfileUserSelectionDialog(
            users = users,
            onUserSelected = { selectedUser ->
                viewModel.updateCurrentUser(selectedUser)
                showUserSelectionDialog = false
            },
            onEditUser = { userToEdit ->
                viewModel.showEditUser(userToEdit)
                showUserSelectionDialog = false
            },
            onDeleteUser = { userToDelete ->
                viewModel.deleteUser(userToDelete)
                // Il dialog si chiuderà automaticamente se l'utente viene eliminato
                // grazie al Flow di users che verrà aggiornato
            }
        )
    }

    if (showNewUserDialog && preselectedGender != null) {
        NewUserDialog(
            onDismiss = { showNewUserDialog = false },
            onConfirm = { name, birthDate, _ ->
                viewModel.createNewUser(name, birthDate, preselectedGender!!)
                showNewUserDialog = false
            },
            preselectedGender = preselectedGender
        )
    }

    // Dialog di modifica utente
    viewModel.showEditDialog.collectAsState().value?.let { userToEdit ->
        NewUserDialog(
            onDismiss = { viewModel.hideEditDialog() },
            onConfirm = { name, birthDate, gender ->
                viewModel.updateUser(userToEdit.userId, name, birthDate, gender)
                viewModel.hideEditDialog()
            },
            preselectedGender = userToEdit.gender,
            initialName = userToEdit.name,
            initialBirthday = userToEdit.birthday
        )
    }

    if (showBackupDialog) {
        BackupRestoreDialog(
            onDismissRequest = { showBackupDialog = false }
        )
    }

    if (showCreditsDialog) {
        CreditsDialog(onDismiss = { showCreditsDialog = false })
    }
}

@Composable
fun SettingButton(
    icon: Int,
    contentDescription: String,
    onClick: () -> Unit
) {
    Box(
        modifier = Modifier
            .clip(RoundedCornerShape(16.dp))
            .background(MaterialTheme.colorScheme.surface)
            .clickable(onClick = onClick)
            .padding(16.dp),
        contentAlignment = Alignment.Center
    ) {
        Image(
            painter = painterResource(id = icon),
            contentDescription = contentDescription,
            modifier = Modifier.size(120.dp)
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProfileUserSelectionDialog(
    users: List<User>,
    onUserSelected: (User) -> Unit,
    onEditUser: (User) -> Unit,
    onDeleteUser: (User) -> Unit
) {
    var selectedUser by remember { mutableStateOf<User?>(null) }
    var showDeleteConfirmation by remember { mutableStateOf<User?>(null) }

    AlertDialog(
        onDismissRequest = { /* Non permettiamo di chiudere il dialog */ },
        title = {
            Text(
                "Seleziona Utente",
                style = MaterialTheme.typography.headlineSmall,
                textAlign = TextAlign.Center,
                modifier = Modifier.fillMaxWidth()
            )
        },
        text = {
            LazyVerticalGrid(
                columns = GridCells.Fixed(2),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                items(users) { user ->
                    ProfileUserItem(
                        user = user,
                        isSelected = user == selectedUser,
                        onSelect = { selectedUser = user },
                        onEdit = { onEditUser(user) },
                        onDelete = { showDeleteConfirmation = user }
                    )
                }
            }
        },
        confirmButton = {
            selectedUser?.let { user ->
                KawaiiButton(
                    onClick = { onUserSelected(user) }
                ) {
                    Row(
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Image(
                            painter = painterResource(id = R.drawable.ic_check_kawaii),
                            contentDescription = null,
                            modifier = Modifier.size(24.dp)
                        )
                        Text("Conferma")
                    }
                }
            }
        }
    )

    // Dialog di conferma eliminazione
    showDeleteConfirmation?.let { userToDelete ->
        AlertDialog(
            onDismissRequest = { showDeleteConfirmation = null },
            title = { Text("Conferma eliminazione") },
            text = { Text("Sei sicuro di voler eliminare l'utente ${userToDelete.name}?") },
            confirmButton = {
                KawaiiButton(
                    onClick = {
                        onDeleteUser(userToDelete)
                        showDeleteConfirmation = null
                    }
                ) {
                    Text("Elimina")
                }
            },
            dismissButton = {
                KawaiiButton(
                    onClick = { showDeleteConfirmation = null }
                ) {
                    Text("Annulla")
                }
            }
        )
    }
}

@Composable
fun ProfileUserItem(
    user: User,
    isSelected: Boolean,
    onSelect: () -> Unit,
    onEdit: () -> Unit,
    onDelete: () -> Unit
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier
            .clip(RoundedCornerShape(16.dp))
            .background(
                if (isSelected) MaterialTheme.colorScheme.primary.copy(alpha = 0.2f)
                else MaterialTheme.colorScheme.surface
            )
            .clickable(onClick = onSelect)
            .padding(8.dp)
    ) {
        // Icona utente
        Image(
            painter = painterResource(
                id = if (user.gender == "M") R.drawable.ic_male
                else R.drawable.ic_female
            ),
            contentDescription = null,
            modifier = Modifier.size(60.dp)
        )
        
        Spacer(modifier = Modifier.height(4.dp))
        
        // Nome utente
        Text(
            text = user.name,
            style = MaterialTheme.typography.titleMedium,
            textAlign = TextAlign.Center
        )
        
        Spacer(modifier = Modifier.height(8.dp))
        
        // Icone di modifica e cancellazione
        Row(
            horizontalArrangement = Arrangement.spacedBy(16.dp),
            modifier = Modifier.padding(top = 4.dp)
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
                    .clickable(onClick = onDelete)
            )
        }
    }
}

@Composable
fun CreditsDialog(onDismiss: () -> Unit) {
    val context = LocalContext.current
    val mediaPlayer = remember { MediaPlayer.create(context, R.raw.trendytracker) }
    val scrollState = rememberScrollState()
    var visible by remember { mutableStateOf(false) }
    
    // Gestione della durata della musica e della chiusura automatica
    LaunchedEffect(key1 = Unit) {
        // Avvia la musica quando il dialog appare
        try {
            mediaPlayer.isLooping = false  // Cambiato da true a false per permettere il completamento
            mediaPlayer.setOnCompletionListener {
                // Quando la musica finisce, avvia il fadeout
                visible = false
                // Piccolo ritardo prima di chiudere completamente il dialog
               // kotlinx.coroutines.delay(2000)
                // Rilascia le risorse e chiudi il dialog
                mediaPlayer.release()
                onDismiss()
            }
            mediaPlayer.start()
        } catch (e: Exception) {
            e.printStackTrace()
        }
        
        // Animazione di fade in
        visible = true
        
        // Scroll automatico
        while (scrollState.value < scrollState.maxValue) {
            delay(60)
            scrollState.scrollTo(scrollState.value + 2)
        }
    }
    
    DisposableEffect(Unit) {
        onDispose {
            try {
                if (mediaPlayer.isPlaying) {
                    mediaPlayer.stop()
                }
                mediaPlayer.release()
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }
    
    Dialog(
        onDismissRequest = {
            try {
                if (mediaPlayer.isPlaying) {
                    mediaPlayer.stop()
                }
                mediaPlayer.release()
            } catch (e: Exception) {
                e.printStackTrace()
            }
            onDismiss()
        },
        properties = DialogProperties(
            dismissOnBackPress = true,
            dismissOnClickOutside = true,
            usePlatformDefaultWidth = false
        )
    ) {
        AnimatedVisibility(
            visible = visible,
            enter = fadeIn(tween(2000)),
            exit = androidx.compose.animation.fadeOut(tween(2000))
        ) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(Color.Black.copy(alpha = 0.9f))
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .fillMaxHeight()
                        .padding(16.dp)
                        .verticalScroll(scrollState),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Spacer(modifier = Modifier.height(100.dp))
                    
                    // Titolo principale
                    Text(
                        text = "TrendyTracker",
                        style = MaterialTheme.typography.headlineLarge,
                        color = Color.White,
                        textAlign = TextAlign.Center,
                        modifier = Modifier.padding(bottom = 32.dp)
                    )
                    
                    // Immagine logo
                    Image(
                        painter = painterResource(id = R.drawable.kawaii_logo),
                        contentDescription = null,
                        modifier = Modifier
                            .size(120.dp)
                            .padding(vertical = 16.dp)
                    )
                    
                    // Ringraziamenti
                    Text(
                        text = "Sviluppato da:",
                        style = MaterialTheme.typography.headlineSmall,
                        color = Color.White,
                        textAlign = TextAlign.Center,
                        modifier = Modifier.padding(vertical = 16.dp)
                    )
                    
                    Text(
                        text = "Valerio Canulli",
                        style = MaterialTheme.typography.titleLarge,
                        color = Color.White,
                        textAlign = TextAlign.Center,
                        modifier = Modifier.padding(bottom = 32.dp)
                    )
                    
                    // Kawaii immagine
                    Image(
                        painter = painterResource(id = R.drawable.settings),
                        contentDescription = null,
                        modifier = Modifier
                            .size(80.dp)
                            .padding(vertical = 16.dp)
                    )
                    
                    // Lorem ipsum per riempire
                    Text(
                        text = "Ringraziamenti",
                        style = MaterialTheme.typography.bodyLarge,
                        color = Color.White,
                        textAlign = TextAlign.Center,
                        modifier = Modifier.padding(vertical = 24.dp)
                    )
                    
                    Image(
                        painter = painterResource(id = R.drawable.thankyou),
                        contentDescription = null,
                        modifier = Modifier
                            .size(100.dp)
                            .padding(vertical = 16.dp)
                    )
                    
                    Text(
                        text = "Francesca per l'esigenza",
                        style = MaterialTheme.typography.bodyLarge,
                        color = Color.White,
                        textAlign = TextAlign.Center,
                        modifier = Modifier.padding(vertical = 16.dp)
                    )
                    
                    Image(
                        painter = painterResource(id = R.drawable.speaking),
                        contentDescription = null,
                        modifier = Modifier
                            .size(100.dp)
                            .padding(vertical = 16.dp)
                    )
                    
                    Text(
                        text = "Anastasia per tutte le gare",
                        style = MaterialTheme.typography.bodyLarge,
                        color = Color.White,
                        textAlign = TextAlign.Center,
                        modifier = Modifier.padding(vertical = 16.dp)
                    )
                    
                    Image(
                        painter = painterResource(id = R.drawable.rollerskate),
                        contentDescription = null,
                        modifier = Modifier
                            .size(100.dp)
                            .padding(vertical = 16.dp)
                    )
                    
                    Text(
                        text = "Alice per la risata travolgente",
                        style = MaterialTheme.typography.bodyLarge,
                        color = Color.White,
                        textAlign = TextAlign.Center,
                        modifier = Modifier.padding(vertical = 16.dp)
                    )
                    
                    Image(
                        painter = painterResource(id = R.drawable.unicorn),
                        contentDescription = null,
                        modifier = Modifier
                            .size(100.dp)
                            .padding(vertical = 16.dp)
                    )
                    
                    Text(
                        text = "e alla fine mia moglie Alessandra che mi ama immensamente senza farlo notare",
                        style = MaterialTheme.typography.bodyLarge,
                        color = Color.White,
                        textAlign = TextAlign.Center,
                        modifier = Modifier.padding(vertical = 16.dp)
                    )
                    
                    Image(
                        painter = painterResource(id = R.drawable.loveyourself),
                        contentDescription = null,
                        modifier = Modifier
                            .size(100.dp)
                            .padding(vertical = 16.dp)
                    )
                    
                    // Aggiungo i crediti per le icone
                    Spacer(modifier = Modifier.height(24.dp))
                    
                    Text(
                        text = "Credits",
                        style = MaterialTheme.typography.headlineSmall,
                        color = Color.White,
                        textAlign = TextAlign.Center,
                        modifier = Modifier.padding(top = 24.dp, bottom = 16.dp)
                    )
                    
                    Text(
                        text = "Icons made by Freepik from www.flaticon.com",
                        style = MaterialTheme.typography.bodyLarge,
                        color = Color.White,
                        textAlign = TextAlign.Center,
                        modifier = Modifier.padding(bottom = 8.dp)
                    )
                    
                    Row(
                        horizontalArrangement = Arrangement.Center,
                        modifier = Modifier.padding(bottom = 24.dp)
                    ) {
                        Text(
                            text = "Visit: ",
                            style = MaterialTheme.typography.bodyMedium,
                            color = Color.White
                        )
                        
                        Text(
                            text = "www.freepik.com",
                            style = MaterialTheme.typography.bodyMedium,
                            color = Color.Cyan,
                            modifier = Modifier.clickable {
                                val intent = Intent(
                                    Intent.ACTION_VIEW,
                                    Uri.parse("https://www.freepik.com")
                                )
                                context.startActivity(intent)
                            }
                        )
                        
                        Text(
                            text = " • ",
                            style = MaterialTheme.typography.bodyMedium,
                            color = Color.White
                        )
                        
                        Text(
                            text = "www.flaticon.com",
                            style = MaterialTheme.typography.bodyMedium,
                            color = Color.Cyan,
                            modifier = Modifier.clickable {
                                val intent = Intent(
                                    Intent.ACTION_VIEW,
                                    Uri.parse("https://www.flaticon.com")
                                )
                                context.startActivity(intent)
                            }
                        )
                    }
                    
                    Text(
                        text = "© 2025 TrendyTracker. Tutti i diritti riservati.",
                        style = MaterialTheme.typography.titleMedium,
                        color = Color.White,
                        textAlign = TextAlign.Center,
                        modifier = Modifier.padding(vertical = 32.dp)
                    )
                    
                    Spacer(modifier = Modifier.height(100.dp))
                }
            }
        }
    }
}