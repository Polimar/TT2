package com.valcan.tt.ui.components

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import com.valcan.tt.ui.viewmodel.BackupInfo
import com.valcan.tt.ui.viewmodel.UserDTO
import java.text.SimpleDateFormat
import java.util.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BackupSelectionDialog(
    backupInfo: BackupInfo,
    onDismissRequest: () -> Unit,
    onConfirm: (selectedUsers: List<Long>, importClothes: Boolean, importShoes: Boolean, importWardrobes: Boolean) -> Unit
) {
    val dateFormat = SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.getDefault())
    val usersSelectionState = remember { mutableStateMapOf<Long, Boolean>() }
    var importClothes by remember { mutableStateOf(true) }
    var importShoes by remember { mutableStateOf(true) }
    var importWardrobes by remember { mutableStateOf(true) }
    var selectAllUsers by remember { mutableStateOf(false) }
    
    // Inizializza la mappa di selezione
    LaunchedEffect(backupInfo) {
        backupInfo.users.forEach { user ->
            usersSelectionState[user.userId] = false
        }
    }
    
    // Funzione per gestire "Seleziona tutti"
    LaunchedEffect(selectAllUsers) {
        backupInfo.users.forEach { user ->
            usersSelectionState[user.userId] = selectAllUsers
        }
    }
    
    Dialog(onDismissRequest = onDismissRequest) {
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            Column(
                modifier = Modifier
                    .padding(16.dp)
                    .fillMaxWidth()
            ) {
                Text(
                    text = "Ripristino backup",
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.padding(bottom = 16.dp)
                )
                
                Text(
                    text = "Backup creato il: ${dateFormat.format(backupInfo.createdAt)}",
                    fontSize = 14.sp,
                    modifier = Modifier.padding(bottom = 8.dp)
                )
                
                Text(
                    text = "Contenuto: ${backupInfo.usersCount} utenti, ${backupInfo.wardrobesCount} armadi, " +
                            "${backupInfo.clothesCount} vestiti, ${backupInfo.shoesCount} scarpe",
                    fontSize = 14.sp,
                    modifier = Modifier.padding(bottom = 16.dp)
                )
                
                // Sezione di selezione utenti
                Text(
                    text = "Seleziona gli utenti da ripristinare:",
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.padding(bottom = 8.dp)
                )
                
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Checkbox(
                        checked = selectAllUsers,
                        onCheckedChange = { selectAllUsers = it }
                    )
                    Text("Seleziona tutti")
                }
                
                LazyColumn(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(120.dp)
                ) {
                    items(backupInfo.users) { user ->
                        UserSelectionItem(
                            user = user,
                            isSelected = usersSelectionState[user.userId] ?: false,
                            onSelectionChanged = { selected ->
                                usersSelectionState[user.userId] = selected
                            }
                        )
                    }
                }
                
                // Opzioni tipi di dati
                Text(
                    text = "Seleziona i tipi di dati da ripristinare:",
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.padding(top = 16.dp, bottom = 8.dp)
                )
                
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Checkbox(
                        checked = importWardrobes,
                        onCheckedChange = { importWardrobes = it }
                    )
                    Text("Armadi")
                }
                
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Checkbox(
                        checked = importClothes,
                        onCheckedChange = { importClothes = it }
                    )
                    Text("Vestiti")
                }
                
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Checkbox(
                        checked = importShoes,
                        onCheckedChange = { importShoes = it }
                    )
                    Text("Scarpe")
                }
                
                // Nota informativa
                Text(
                    text = "Nota: Se selezioni vestiti o scarpe, verranno importati anche gli armadi necessari.",
                    fontSize = 12.sp,
                    modifier = Modifier.padding(top = 8.dp)
                )
                
                // Pulsanti di azione
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 16.dp),
                    horizontalArrangement = Arrangement.End
                ) {
                    TextButton(onClick = onDismissRequest) {
                        Text("Annulla")
                    }
                    
                    Spacer(modifier = Modifier.width(8.dp))
                    
                    Button(
                        onClick = {
                            val selectedUsers = usersSelectionState
                                .filter { it.value }
                                .map { it.key }
                            onConfirm(selectedUsers, importClothes, importShoes, importWardrobes)
                        },
                        enabled = usersSelectionState.any { it.value }
                    ) {
                        Text("Ripristina")
                    }
                }
            }
        }
    }
}

@Composable
fun UserSelectionItem(
    user: UserDTO,
    isSelected: Boolean,
    onSelectionChanged: (Boolean) -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Checkbox(
            checked = isSelected,
            onCheckedChange = onSelectionChanged
        )
        Column {
            Text(
                text = user.name,
                fontWeight = FontWeight.Medium
            )
            Text(
                text = "Genere: ${user.gender}, Compleanno: ${user.birthday.take(10)}",
                fontSize = 12.sp
            )
        }
    }
} 