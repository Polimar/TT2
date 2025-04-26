package com.valcan.tt.ui.components

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import com.valcan.tt.R
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
                .padding(8.dp)
        ) {
            Column(
                modifier = Modifier
                    .padding(8.dp)
                    .fillMaxWidth()
            ) {
                Text(
                    text = stringResource(R.string.backup_restore),
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.padding(bottom = 8.dp)
                )
                
                Text(
                    text = stringResource(R.string.backup_created_at, dateFormat.format(backupInfo.createdAt)),
                    fontSize = 10.sp,
                    modifier = Modifier.padding(bottom = 8.dp)
                )
                
                Text(
                    text = stringResource(
                        R.string.backup_content, 
                        backupInfo.usersCount, 
                        backupInfo.wardrobesCount,
                        backupInfo.clothesCount, 
                        backupInfo.shoesCount
                    ),
                    fontSize = 10.sp,
                    modifier = Modifier.padding(bottom = 16.dp)
                )
                
                // Sezione di selezione utenti
                Text(
                    text = stringResource(R.string.backup_select_users),
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.padding(bottom = 4.dp)
                )
                
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Checkbox(
                        checked = selectAllUsers,
                        onCheckedChange = { selectAllUsers = it }
                    )
                    Text(stringResource(R.string.backup_select_all))
                }
                
                LazyColumn(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(100.dp)
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
                    text = stringResource(R.string.backup_select_data_types),
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.padding(top = 4.dp, bottom = 2.dp)
                )
                
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Checkbox(
                        checked = importWardrobes,
                        onCheckedChange = { importWardrobes = it }
                    )
                    Text(stringResource(R.string.nav_wardrobes))
                }
                
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Checkbox(
                        checked = importClothes,
                        onCheckedChange = { importClothes = it }
                    )
                    Text(stringResource(R.string.nav_clothes))
                }
                
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Checkbox(
                        checked = importShoes,
                        onCheckedChange = { importShoes = it }
                    )
                    Text(stringResource(R.string.nav_shoes))
                }
                
                // Nota informativa
                Text(
                    text = stringResource(R.string.backup_note),
                    fontSize = 12.sp,
                    modifier = Modifier.padding(top = 8.dp)
                )
                
                // Pulsanti di azione
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 12.dp),
                    horizontalArrangement = Arrangement.End
                ) {
                    TextButton(onClick = onDismissRequest) {
                        Text(stringResource(R.string.action_cancel))
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
                        Text(stringResource(R.string.action_restore))
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
        Text(
            text = stringResource(R.string.backup_user_info, user.name, user.gender, user.birthday.take(10))
        )
    }
} 