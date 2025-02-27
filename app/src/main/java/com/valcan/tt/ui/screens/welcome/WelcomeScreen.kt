package com.valcan.tt.ui.screens.welcome

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
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
import com.valcan.tt.R
import com.valcan.tt.ui.navigation.Screen
import java.util.Date
import com.valcan.tt.ui.components.KawaiiDatePicker
import com.valcan.tt.ui.components.KawaiiButton

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun WelcomeScreen(
    navController: NavController,
    viewModel: WelcomeViewModel = hiltViewModel()
) {
    val usersState by viewModel.usersState.collectAsState()
    val showDialog by viewModel.showNewUserDialog.collectAsState()

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
                contentDescription = "Kawaii Logo",
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
                    LaunchedEffect(Unit) {
                        navController.navigate(Screen.Home.route)
                    }
                }
                is UsersState.MultipleUsers -> {
                    // TODO: Implementare la selezione dell'utente
                    Text("Seleziona il tuo profilo")
                }
            }
        }
    }

    if (showDialog) {
        NewUserDialog(
            onDismiss = { /* Non permettiamo di chiudere il dialog se non ci sono utenti */ },
            onConfirm = { name, birthDate, gender ->
                viewModel.createNewUser(name, birthDate, gender)
                navController.navigate(Screen.Home.route)
            }
        )
    }
}

@Composable
fun NewUserDialog(
    onDismiss: () -> Unit,
    onConfirm: (String, Date, String) -> Unit
) {
    var name by remember { mutableStateOf("") }
    var selectedGender by remember { mutableStateOf<String?>(null) }
    var showDatePicker by remember { mutableStateOf(false) }
    var selectedDate by remember { mutableStateOf<Date?>(null) }
    var showError by remember { mutableStateOf(false) }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Crea il tuo profilo", style = MaterialTheme.typography.titleLarge) },
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
                
                Spacer(modifier = Modifier.height(16.dp))
                
                // Data di nascita
                KawaiiButton(
                    onClick = { showDatePicker = true },
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(selectedDate?.let { 
                        it.toString().split(" ")[0] 
                    } ?: "Seleziona data di nascita")
                }
                
                if (showError && selectedDate == null) {
                    Text(
                        "La data di nascita è obbligatoria",
                        color = MaterialTheme.colorScheme.error,
                        style = MaterialTheme.typography.bodySmall
                    )
                }
                
                Spacer(modifier = Modifier.height(16.dp))
                
                // Selezione genere con icone
                Text("Seleziona il tuo genere", style = MaterialTheme.typography.titleMedium)
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
                            contentDescription = "Maschio",
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
                            contentDescription = "Femmina",
                            modifier = Modifier.size(60.dp)
                        )
                    }
                }
                
                if (showError && selectedGender == null) {
                    Text(
                        "La selezione del genere è obbligatoria",
                        color = MaterialTheme.colorScheme.error,
                        style = MaterialTheme.typography.bodySmall,
                        modifier = Modifier.align(Alignment.CenterHorizontally)
                    )
                }
            }
        },
        confirmButton = {
            KawaiiButton(
                onClick = {
                    if (name.isBlank() || selectedDate == null || selectedGender == null) {
                        showError = true
                    } else {
                        selectedDate?.let { date ->
                            selectedGender?.let { gender ->
                                onConfirm(name, date, gender)
                            }
                        }
                    }
                }
            ) {
                Text("Conferma")
            }
        }
    )

    if (showDatePicker) {
        KawaiiDatePicker(
            onDateSelected = { 
                selectedDate = it
                showDatePicker = false
            },
            onDismiss = { showDatePicker = false }
        )
    }
} 