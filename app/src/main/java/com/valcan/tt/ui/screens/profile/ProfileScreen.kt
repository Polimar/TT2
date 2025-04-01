package com.valcan.tt.ui.screens.profile

import android.content.Intent
import android.media.MediaPlayer
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
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.valcan.tt.R
import com.valcan.tt.data.model.User
import com.valcan.tt.ui.components.KawaiiButton
import com.valcan.tt.ui.screens.welcome.NewUserDialog
import com.valcan.tt.ui.viewmodel.ProfileViewModel
import androidx.compose.material3.AlertDialog
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import com.valcan.tt.ui.navigation.Screen
import com.valcan.tt.ui.components.BackupRestoreDialog
import com.valcan.tt.utils.LocaleHelper
import kotlinx.coroutines.delay
import androidx.compose.foundation.layout.heightIn
import androidx.core.net.toUri


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
    var showLanguageDialog by remember { mutableStateOf(false) }
    
    val context = LocalContext.current
    
    // Inizializza la lingua all'avvio
    LaunchedEffect(Unit) {
        viewModel.initLanguage(context)
    }

    Box(modifier = Modifier.fillMaxSize()) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Titolo
            Text(
                text = stringResource(id = R.string.profile_title),
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
                items(7) { index ->
                    when (index) {
                        0 -> SettingButton(
                            icon = R.drawable.ic_users_kawaii,
                            contentDescription = stringResource(id = R.string.profile_select_users),
                            onClick = { showUserSelectionDialog = true }
                        )
                        1 -> SettingButton(
                            icon = R.drawable.ic_wardrobe_kawaii,
                            contentDescription = stringResource(id = R.string.profile_wardrobes),
                            onClick = { navController.navigate(Screen.Wardrobe.route) }
                        )
                        2 -> SettingButton(
                            icon = R.drawable.ic_add_male_kawaii,
                            contentDescription = stringResource(id = R.string.profile_new_user_male),
                            onClick = { 
                                preselectedGender = "M"
                                showNewUserDialog = true
                            }
                        )
                        3 -> SettingButton(
                            icon = R.drawable.ic_add_female_kawaii,
                            contentDescription = stringResource(id = R.string.profile_new_user_female),
                            onClick = { 
                                preselectedGender = "F"
                                showNewUserDialog = true
                            }
                        )
                        4 -> SettingButton(
                            icon = R.drawable.ic_backup_kawaii,
                            contentDescription = stringResource(id = R.string.profile_backup),
                            onClick = { showBackupDialog = true }
                        )
                        5 -> SettingButton(
                            icon = R.drawable.ic_languages,
                            contentDescription = stringResource(id = R.string.profile_languages),
                            onClick = { showLanguageDialog = true }
                        )
                        6 -> SettingButton(
                            icon = R.drawable.ic_info_kawaii,
                            contentDescription = stringResource(id = R.string.profile_info),
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
    
    if (showLanguageDialog) {
        LanguageDialog(
            onDismiss = { showLanguageDialog = false },
            onSelectLanguage = { languageCode ->
                viewModel.changeLanguage(context, languageCode)
                // Riavvia l'Activity per applicare il cambio lingua
                val intent = context.packageManager.getLaunchIntentForPackage(context.packageName)
                intent?.apply {
                    addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
                    addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                    context.startActivity(this)
                }
                // Simula l'effetto di riavvio dell'Activity corrente
                (context as? androidx.activity.ComponentActivity)?.finish()
                showLanguageDialog = false
            },
            currentLanguage = viewModel.currentLanguage.collectAsState().value ?: LocaleHelper.getDeviceLanguage(context)
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
                stringResource(R.string.profile_select_users),
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
                        Text(stringResource(R.string.action_confirm))
                    }
                }
            }
        }
    )

    // Dialog di conferma eliminazione
    showDeleteConfirmation?.let { userToDelete ->
        AlertDialog(
            onDismissRequest = { showDeleteConfirmation = null },
            title = { Text(stringResource(R.string.profile_confirm_delete)) },
            text = { Text(stringResource(R.string.profile_confirm_delete_message, userToDelete.name)) },
            confirmButton = {
                KawaiiButton(
                    onClick = {
                        onDeleteUser(userToDelete)
                        showDeleteConfirmation = null
                    }
                ) {
                    Text(stringResource(R.string.action_delete))
                }
            },
            dismissButton = {
                KawaiiButton(
                    onClick = { showDeleteConfirmation = null }
                ) {
                    Text(stringResource(R.string.action_cancel))
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
                contentDescription = stringResource(R.string.action_edit),
                modifier = Modifier
                    .size(24.dp)
                    .clickable(onClick = onEdit)
            )
            Image(
                painter = painterResource(id = R.drawable.ic_delete),
                contentDescription = stringResource(R.string.action_delete),
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
    val scrollState = rememberScrollState()
    val mediaPlayer = remember { MediaPlayer.create(context, R.raw.trendytracker) }
    var showContent by remember { mutableStateOf(false) }
    var visible by remember { mutableStateOf(false) }

    // Effetto per lo scorrimento automatico
    LaunchedEffect(Unit) {

        showContent = true
        delay(1000)  // Piccolo ritardo prima di iniziare lo scorrimento
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
        val maxScrollPosition = scrollState.maxValue
        if (maxScrollPosition > 0) {
            // Scroll lento fino alla fine del contenuto
            val scrollDurationMillis = 30000L  // 30 secondi per scorrere tutto
            val startTime = System.currentTimeMillis()
            
            while (scrollState.value < maxScrollPosition) {
                val elapsedTime = System.currentTimeMillis() - startTime
                val scrollPosition = (elapsedTime.toFloat() / scrollDurationMillis * maxScrollPosition).toInt()
                    .coerceAtMost(maxScrollPosition)
                
                scrollState.scrollTo(scrollPosition)
                delay(30)  // ~60fps
            }
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
        onDismissRequest = onDismiss,
        properties = DialogProperties(dismissOnBackPress = true, dismissOnClickOutside = true)
    ) {
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.primaryContainer
            )
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .heightIn(max = 500.dp)
                    .verticalScroll(scrollState),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                AnimatedVisibility(
                    visible = showContent,
                    enter = fadeIn(animationSpec = tween(5000))
                ) {
                    Column(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        // Logo
                        Image(
                            painter = painterResource(id = R.drawable.kawaii_logo),
                            contentDescription = null,
                            modifier = Modifier
                                .size(120.dp)
                                .padding(vertical = 16.dp)
                        )
                        
                        // Titolo app
                        Text(
                            text = stringResource(R.string.app_name),
                            style = MaterialTheme.typography.headlineLarge,
                            color = MaterialTheme.colorScheme.onPrimaryContainer,
                            textAlign = TextAlign.Center,
                            modifier = Modifier.padding(bottom = 8.dp)
                        )
                        
                        // Versione
                        Text(
                            text = stringResource(R.string.credits_version),
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onPrimaryContainer,
                            textAlign = TextAlign.Center,
                            modifier = Modifier.padding(bottom = 24.dp)
                        )
                        
                        // Sviluppatore
                        Text(
                            text = stringResource(R.string.credits_developer),
                            style = MaterialTheme.typography.headlineSmall,
                            color = MaterialTheme.colorScheme.onPrimaryContainer,
                            textAlign = TextAlign.Center,
                            modifier = Modifier.padding(vertical = 16.dp)
                        )
                        
                        Text(
                            text = stringResource(R.string.credits_developer_name),
                            style = MaterialTheme.typography.titleLarge,
                            color = MaterialTheme.colorScheme.onPrimaryContainer,
                            textAlign = TextAlign.Center,
                            modifier = Modifier.padding(bottom = 32.dp)
                        )
                        
                        // Developer icon
                        Image(
                            painter = painterResource(id = R.drawable.settings),
                            contentDescription = null,
                            modifier = Modifier
                                .size(80.dp)
                                .padding(vertical = 16.dp)
                        )
                        
                        Text(
                            text = stringResource(R.string.credits_music),
                            style = MaterialTheme.typography.headlineSmall,
                            color = MaterialTheme.colorScheme.onPrimaryContainer,
                            textAlign = TextAlign.Center,
                            modifier = Modifier.padding(vertical = 16.dp)
                        )
                        
                        Text(
                            text = stringResource(R.string.credits_music_name),
                            style = MaterialTheme.typography.titleLarge,
                            color = MaterialTheme.colorScheme.onPrimaryContainer,
                            textAlign = TextAlign.Center,
                            modifier = Modifier.padding(bottom = 32.dp)
                        )
                        
                        // Kawaii immagine
                        Image(
                            painter = painterResource(id = R.drawable.music),
                            contentDescription = null,
                            modifier = Modifier
                                .size(80.dp)
                                .padding(vertical = 16.dp)
                        )
                        // Lorem ipsum per riempire
                        Text(
                            text = stringResource(R.string.credits_thanks),
                            style = MaterialTheme.typography.bodyLarge,
                            color = MaterialTheme.colorScheme.onPrimaryContainer,
                            textAlign = TextAlign.Center,
                            modifier = Modifier.padding(vertical = 16.dp)
                        )
                        Image(
                            painter = painterResource(id = R.drawable.thankyou),
                            contentDescription = null,
                            modifier = Modifier
                                .size(100.dp)
                                .padding(vertical = 16.dp)
                        )
                        Text(
                            text = stringResource(R.string.credits_fran),
                            style = MaterialTheme.typography.bodyLarge,
                            color = MaterialTheme.colorScheme.onPrimaryContainer,
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
                            text = stringResource(R.string.credits_ana),
                            style = MaterialTheme.typography.bodyLarge,
                            color = MaterialTheme.colorScheme.onPrimaryContainer,
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
                            text = stringResource(R.string.credits_ali),
                            style = MaterialTheme.typography.bodyLarge,
                            color = MaterialTheme.colorScheme.onPrimaryContainer,
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
                            text = stringResource(R.string.credits_wife),
                            style = MaterialTheme.typography.bodyLarge,
                            color = MaterialTheme.colorScheme.onPrimaryContainer,
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
                            text = stringResource(R.string.credits_title),
                            style = MaterialTheme.typography.headlineSmall,
                            color = MaterialTheme.colorScheme.onPrimaryContainer,
                            textAlign = TextAlign.Center,
                            modifier = Modifier.padding(top = 24.dp, bottom = 16.dp)
                        )

                        Text(
                            text = "Icons made by Freepik from www.flaticon.com",
                            style = MaterialTheme.typography.bodyLarge,
                            color = MaterialTheme.colorScheme.onPrimaryContainer,
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
                                color = MaterialTheme.colorScheme.onPrimaryContainer,
                            )

                            Text(
                                text = "www.freepik.com",
                                style = MaterialTheme.typography.bodyMedium,
                                color = Color.Cyan,
                                modifier = Modifier.clickable {
                                    val intent = Intent(
                                        Intent.ACTION_VIEW,
                                        "https://www.freepik.com".toUri()
                                    )
                                    context.startActivity(intent)
                                }
                            )

                            Text(
                                text = " • ",
                                style = MaterialTheme.typography.bodyMedium,
                                color = MaterialTheme.colorScheme.onPrimaryContainer,
                            )

                            Text(
                                text = "www.flaticon.com",
                                style = MaterialTheme.typography.bodyMedium,
                                color = Color.Cyan,
                                modifier = Modifier.clickable {
                                    val intent = Intent(
                                        Intent.ACTION_VIEW,
                                        "https://www.flaticon.com".toUri()
                                    )
                                    context.startActivity(intent)
                                }
                            )
                        }

                        Text(
                            text = stringResource(R.string.credits_copy),
                            style = MaterialTheme.typography.titleMedium,
                            color = MaterialTheme.colorScheme.onPrimaryContainer,
                            textAlign = TextAlign.Center,
                            modifier = Modifier.padding(vertical = 32.dp)
                        )

                        Spacer(modifier = Modifier.height(100.dp))
                        // Chiudi
                        Button(
                            onClick = onDismiss,
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(16.dp)
                        ) {
                            Text(stringResource(R.string.action_close))
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun LanguageDialog(
    onDismiss: () -> Unit,
    onSelectLanguage: (String) -> Unit,
    currentLanguage: String
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Text(
                text = stringResource(id = R.string.language_dialog_title),
                style = MaterialTheme.typography.headlineSmall,
                textAlign = TextAlign.Center,
                modifier = Modifier.fillMaxWidth()
            )
        },
        text = {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                // Italian
                LanguageButton(
                    languageCode = "it",
                    languageName = "Italiano",
                    iconRes = R.drawable.flag_it,
                    isSelected = currentLanguage == "it",
                    onClick = { onSelectLanguage("it") }
                )
                
                // English
                LanguageButton(
                    languageCode = "en",
                    languageName = "English",
                    iconRes = R.drawable.flag_en,
                    isSelected = currentLanguage == "en",
                    onClick = { onSelectLanguage("en") }
                )
                
                // French
                LanguageButton(
                    languageCode = "fr",
                    languageName = "Français",
                    iconRes = R.drawable.flag_fr,
                    isSelected = currentLanguage == "fr",
                    onClick = { onSelectLanguage("fr") }
                )
                
                // German
                LanguageButton(
                    languageCode = "de",
                    languageName = "Deutsch",
                    iconRes = R.drawable.flag_de,
                    isSelected = currentLanguage == "de",
                    onClick = { onSelectLanguage("de") }
                )
                
                // Spanish
                LanguageButton(
                    languageCode = "es",
                    languageName = "Español",
                    iconRes = R.drawable.flag_es,
                    isSelected = currentLanguage == "es",
                    onClick = { onSelectLanguage("es") }
                )
            }
        },
        confirmButton = {
            TextButton(onClick = onDismiss) {
                Text(stringResource(R.string.dialog_close))
            }
        }
    )
}

@Composable
fun LanguageButton(
    languageCode: String,
    languageName: String,
    iconRes: Int,
    isSelected: Boolean,
    onClick: () -> Unit
) {
    Button(
        onClick = onClick,
        modifier = Modifier.fillMaxWidth(),
        colors = ButtonDefaults.buttonColors(
            containerColor = if (isSelected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.surface,
            contentColor = if (isSelected) MaterialTheme.colorScheme.onPrimary else MaterialTheme.colorScheme.onSurface
        ),
        shape = RoundedCornerShape(8.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(8.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Image(
                painter = painterResource(id = iconRes),
                contentDescription = "$languageName ($languageCode)",
                modifier = Modifier.size(32.dp)
            )
            
            Text(
                text = languageName,
                style = MaterialTheme.typography.titleMedium
            )
            
            if (isSelected) {
                Box(
                    modifier = Modifier.fillMaxWidth(),
                    contentAlignment = Alignment.CenterEnd
                ) {
                    Icon(
                        painter = painterResource(id = R.drawable.ic_check_kawaii),
                        contentDescription = stringResource(R.string.profile_selected),
                        tint = MaterialTheme.colorScheme.onPrimary
                    )
                }
            }
        }
    }
}