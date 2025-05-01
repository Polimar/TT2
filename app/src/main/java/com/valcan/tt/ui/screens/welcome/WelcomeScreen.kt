package com.valcan.tt.ui.screens.welcome

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.valcan.tt.R
import com.valcan.tt.data.model.User
import com.valcan.tt.ui.navigation.Screen
import com.valcan.tt.ui.components.KawaiiDatePicker
import com.valcan.tt.ui.components.KawaiiButton
import java.util.Date


@Composable
fun WelcomeScreen(
    navController: NavController,
    viewModel: WelcomeViewModel = hiltViewModel()
) {
    val usersState by viewModel.usersState.collectAsState()
    val showDialog by viewModel.showNewUserDialog.collectAsState()

    // Collezione dell'evento di navigazione
    LaunchedEffect(Unit) {
        viewModel.navigationEvent.collect { shouldNavigate ->
            if (shouldNavigate) {
                navController.navigate(Screen.Home.route) {
                    popUpTo(Screen.Welcome.route) { inclusive = true }
                }
            }
        }
    }

    Surface(
        modifier = Modifier.fillMaxSize(),
        color = MaterialTheme.colorScheme.background
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            // Logo kawaii
            Image(
                painter = painterResource(id = R.drawable.kawaii_logo),
                contentDescription = stringResource(R.string.app_name),
                modifier = Modifier.size(500.dp),
                contentScale = ContentScale.Fit
            )

            Spacer(modifier = Modifier.height(24.dp))

            when (usersState) {
                is UsersState.Loading -> {
                    CircularProgressIndicator()
                }
                is UsersState.NoUsers -> {
                    // Gestito dal Dialog
                }
                is UsersState.SingleUser -> {
                    // La navigazione è gestita dal LaunchedEffect
                }
                is UsersState.MultipleUsers -> {
                    val users = (usersState as UsersState.MultipleUsers).users
                    UserSelectionDialog(
                        users = users,
                        onUserSelected = { selectedUser ->
                            viewModel.selectUser(selectedUser)
                        }
                    )
                }
            }
        }
    }

    if (showDialog) {
        NewUserDialog(
            onDismiss = { /* Non permettiamo di chiudere il dialog se non ci sono utenti */ },
            onConfirm = { name, birthDate, gender ->
                viewModel.createNewUser(name, birthDate, gender)
            }
        )
    }
}

@Composable
fun NewUserDialog(
    onDismiss: () -> Unit,
    onConfirm: (name: String, birthDate: Date, gender: String) -> Unit,
    preselectedGender: String? = null,
    initialName: String? = null,
    initialBirthday: Date? = null
) {
    var name by remember { mutableStateOf(initialName ?: "") }
    var selectedDate by remember { mutableStateOf(initialBirthday ?: Date()) }
    var selectedGender by remember { mutableStateOf(preselectedGender) }
    var showDatePicker by remember { mutableStateOf(false) }
    var showError by remember { mutableStateOf(false) }
    
    // Importante: aggiungiamo questa variabile per tenere traccia se l'utente ha selezionato una data
    var isDateSelected by remember { mutableStateOf(false) }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text(stringResource(R.string.edit_profile_title), style = MaterialTheme.typography.titleLarge) },
        text = {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(8.dp)
            ) {
                // Nome
                OutlinedTextField(
                    value = name,
                    onValueChange = { name = it },
                    label = { Text(stringResource(R.string.edit_profile_name)) },
                    modifier = Modifier.fillMaxWidth(),
                    isError = showError && name.isBlank(),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = MaterialTheme.colorScheme.primary,
                        focusedLabelColor = MaterialTheme.colorScheme.primary
                    )
                )
                
                if (showError && name.isBlank()) {
                    Text(
                        stringResource(R.string.edit_profile_required),
                        color = MaterialTheme.colorScheme.error,
                        style = MaterialTheme.typography.bodySmall
                    )
                }
                
                Spacer(modifier = Modifier.height(16.dp))
                
                // Data di nascita
                val dateFormat = java.text.SimpleDateFormat("dd/MM/yyyy", java.util.Locale.getDefault())
                
                // Determina il testo da mostrare sul bottone
                val buttonText = if (isDateSelected) {
                    dateFormat.format(selectedDate)
                } else {
                    stringResource(R.string.edit_profile_birthday)
                }
                
                KawaiiButton(
                    onClick = { showDatePicker = true },
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(buttonText)
                }
                
                if (showError && !isDateSelected) {
                    Text(
                        stringResource(R.string.edit_profile_required),
                        color = MaterialTheme.colorScheme.error,
                        style = MaterialTheme.typography.bodySmall
                    )
                }
                
                Spacer(modifier = Modifier.height(16.dp))
                
                // Selezione genere con icone (solo se non è preselezionato)
                if (preselectedGender == null) {
                    Text(stringResource(R.string.edit_profile_gender), style = MaterialTheme.typography.titleMedium)
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceEvenly
                    ) {
                        IconButton(
                            onClick = { selectedGender = "M" },
                            modifier = Modifier
                                .size(80.dp)
                                .background(
                                    color = if (selectedGender == "M") 
                                        MaterialTheme.colorScheme.primary.copy(alpha = 0.2f)
                                    else MaterialTheme.colorScheme.surface,
                                    shape = RoundedCornerShape(16.dp)
                                )
                        ) {
                            Image(
                                painter = painterResource(id = R.drawable.ic_male),
                                contentDescription = stringResource(R.string.edit_profile_male),
                                modifier = Modifier.size(60.dp)
                            )
                        }
                        
                        IconButton(
                            onClick = { selectedGender = "F" },
                            modifier = Modifier
                                .size(80.dp)
                                .background(
                                    color = if (selectedGender == "F") 
                                        MaterialTheme.colorScheme.primary.copy(alpha = 0.2f)
                                    else MaterialTheme.colorScheme.surface,
                                    shape = RoundedCornerShape(16.dp)
                                )
                        ) {
                            Image(
                                painter = painterResource(id = R.drawable.ic_female),
                                contentDescription = stringResource(R.string.edit_profile_female),
                                modifier = Modifier.size(60.dp)
                            )
                        }
                    }
                    
                    if (showError && selectedGender == null) {
                        Text(
                            stringResource(R.string.edit_profile_required),
                            color = MaterialTheme.colorScheme.error,
                            style = MaterialTheme.typography.bodySmall,
                            modifier = Modifier.align(Alignment.CenterHorizontally)
                        )
                    }
                }
            }
        },
        confirmButton = {
            KawaiiButton(
                onClick = {
                    if (name.isBlank() || !isDateSelected || (preselectedGender == null && selectedGender == null)) {
                        showError = true
                    } else {
                        onConfirm(name, selectedDate, preselectedGender ?: selectedGender!!)
                    }
                }
            ) {
                Text(stringResource(R.string.action_confirm))
            }
        }
    )

    if (showDatePicker) {
        KawaiiDatePicker(
            onDateSelected = { date -> 
                selectedDate = date
                isDateSelected = true  // Impostiamo questo flag quando l'utente seleziona una data
                showDatePicker = false
            },
            onDismiss = { showDatePicker = false }
        )
    }
}

@Composable
fun UserSelectionDialog(
    users: List<User>,
    onUserSelected: (User) -> Unit
) {
    var selectedUser by remember { mutableStateOf<User?>(null) }

    AlertDialog(
        onDismissRequest = { /* Non permettiamo di chiudere il dialog */ },
        title = {
            Text(
                stringResource(R.string.app_name),
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
                    UserSelectionItem(
                        user = user,
                        isSelected = user == selectedUser,
                        onClick = { selectedUser = user }
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
}

@Composable
fun UserSelectionItem(
    user: User,
    isSelected: Boolean,
    onClick: () -> Unit
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier
            .clip(RoundedCornerShape(16.dp))
            .background(
                if (isSelected) MaterialTheme.colorScheme.primary.copy(alpha = 0.2f)
                else MaterialTheme.colorScheme.surface
            )
            .clickable(onClick = onClick)
            .padding(8.dp)
    ) {
        Image(
            painter = painterResource(
                id = if (user.gender == "M") R.drawable.ic_male
                else R.drawable.ic_female
            ),
            contentDescription = null,
            modifier = Modifier.size(60.dp)
        )
        Spacer(modifier = Modifier.height(8.dp))
        Text(
            text = user.name,
            style = MaterialTheme.typography.titleMedium,
            textAlign = TextAlign.Center
        )
    }
} 