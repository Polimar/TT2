package com.valcan.tt.ui.components

import android.content.Intent
import android.net.Uri
import android.webkit.MimeTypeMap
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import androidx.compose.ui.window.Dialog
import androidx.hilt.navigation.compose.hiltViewModel
import com.valcan.tt.ui.viewmodel.BackupRestoreViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BackupRestoreDialog(
    onDismissRequest: () -> Unit,
    viewModel: BackupRestoreViewModel = hiltViewModel()
) {
    var openDialog by remember { mutableStateOf(true) }
    var selectedTab by remember { mutableStateOf(0) }
    
    // Osserva i dati del backup selezionato per il restore
    val backupInfo by viewModel.backupInfo.collectAsState()
    
    // Registra i risultati degli intent per selezionare i file
    val backupLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.CreateDocument("application/tt"),
        onResult = { uri ->
            uri?.let {
                viewModel.createBackup(it)
                openDialog = false
                onDismissRequest()
            }
        }
    )
    
    val restoreLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.OpenDocument(),
        onResult = { uri ->
            uri?.let {
                viewModel.analyzeBackup(it)
            }
        }
    )
    
    // Se il dialogo è chiuso, non mostrare nulla
    if (!openDialog) {
        return
    }
    
    // Se abbiamo informazioni su un backup, mostra il dialogo di selezione
    backupInfo?.let { info ->
        BackupSelectionDialog(
            backupInfo = info,
            onDismissRequest = {
                viewModel._backupInfo.value = null
            },
            onConfirm = { selectedUsers, importClothes, importShoes, importWardrobes ->
                viewModel.restoreSelectedData(info, selectedUsers, importClothes, importShoes, importWardrobes)
                openDialog = false
                onDismissRequest()
            }
        )
        return
    }
    
    // Altrimenti mostra il dialogo principale
    Dialog(onDismissRequest = {
        openDialog = false
        onDismissRequest()
    }) {
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
                    text = "Backup e Ripristino",
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.padding(bottom = 16.dp)
                )
                
                TabRow(selectedTabIndex = selectedTab) {
                    Tab(
                        selected = selectedTab == 0,
                        onClick = { selectedTab = 0 },
                        text = { Text("Backup") }
                    )
                    Tab(
                        selected = selectedTab == 1,
                        onClick = { selectedTab = 1 },
                        text = { Text("Ripristino") }
                    )
                }
                
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(150.dp)
                        .padding(16.dp),
                    contentAlignment = Alignment.Center
                ) {
                    when (selectedTab) {
                        0 -> {
                            Column(
                                horizontalAlignment = Alignment.CenterHorizontally,
                                verticalArrangement = Arrangement.Center
                            ) {
                                Text(
                                    text = "Crea un backup completo dell'app",
                                    modifier = Modifier.padding(bottom = 16.dp)
                                )
                                Button(
                                    onClick = {
                                        backupLauncher.launch("TT_Backup_${System.currentTimeMillis()}.tt")
                                    }
                                ) {
                                    Text("Crea Backup")
                                }
                            }
                        }
                        1 -> {
                            Column(
                                horizontalAlignment = Alignment.CenterHorizontally,
                                verticalArrangement = Arrangement.Center
                            ) {
                                Text(
                                    text = "Ripristina da un backup esistente",
                                    modifier = Modifier.padding(bottom = 16.dp)
                                )
                                Button(
                                    onClick = {
                                        restoreLauncher.launch(arrayOf("application/tt", "*/*"))
                                    }
                                ) {
                                    Text("Seleziona file .tt")
                                }
                            }
                        }
                    }
                }
                
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 8.dp),
                    horizontalArrangement = Arrangement.End
                ) {
                    TextButton(
                        onClick = {
                            openDialog = false
                            onDismissRequest()
                        }
                    ) {
                        Text("Chiudi")
                    }
                }
            }
        }
    }
} 