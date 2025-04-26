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
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import androidx.compose.ui.window.Dialog
import androidx.hilt.navigation.compose.hiltViewModel
import com.valcan.tt.R
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
                    text = stringResource(R.string.backup_title),
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.padding(bottom = 16.dp)
                )
                
                TabRow(selectedTabIndex = selectedTab) {
                    Tab(
                        selected = selectedTab == 0,
                        onClick = { selectedTab = 0 },
                        text = { Text(stringResource(R.string.action_save)) }
                    )
                    Tab(
                        selected = selectedTab == 1,
                        onClick = { selectedTab = 1 },
                        text = { Text(stringResource(R.string.backup_restore)) }
                    )
                }
                
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(150.dp)
                        .padding(8.dp),
                    contentAlignment = Alignment.Center
                ) {
                    when (selectedTab) {
                        0 -> {
                            Column(
                                horizontalAlignment = Alignment.CenterHorizontally,
                                verticalArrangement = Arrangement.Center
                            ) {
                                Text(
                                    text = stringResource(R.string.backup_create),
                                    modifier = Modifier.padding(bottom = 12.dp)
                                )
                                Button(
                                    onClick = {
                                        backupLauncher.launch("TT_Backup_${System.currentTimeMillis()}.tt")
                                    }
                                ) {
                                    Text(stringResource(R.string.backup_create))
                                }
                            }
                        }
                        1 -> {
                            Column(
                                horizontalAlignment = Alignment.CenterHorizontally,
                                verticalArrangement = Arrangement.Center
                            ) {
                                Text(
                                    text = stringResource(R.string.backup_restore),
                                    modifier = Modifier.padding(bottom = 12.dp)
                                )
                                Button(
                                    onClick = {
                                        restoreLauncher.launch(arrayOf("application/tt", "*/*"))
                                    }
                                ) {
                                    Text(stringResource(R.string.backup_restore))
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
                        Text(stringResource(R.string.action_close))
                    }
                }
            }
        }
    }
} 