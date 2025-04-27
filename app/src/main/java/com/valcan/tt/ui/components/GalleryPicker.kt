package com.valcan.tt.ui.components

import android.Manifest
import android.content.Context
import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.runtime.*
import androidx.compose.ui.platform.LocalContext
import androidx.core.content.ContextCompat

/**
 * Hook Composable che fornisce il launcher per selezionare un'immagine dalla galleria.
 * Questo hook restituisce una funzione che può essere chiamata per avviare la selezione.
 */
@Composable
fun rememberGalleryLauncher(
    onImageSelected: (Uri) -> Unit
): () -> Unit {
    val context = LocalContext.current
    
    var hasStoragePermission by remember {
        mutableStateOf(
            ContextCompat.checkSelfPermission(
                context,
                Manifest.permission.READ_EXTERNAL_STORAGE
            ) == android.content.pm.PackageManager.PERMISSION_GRANTED
        )
    }
    
    val permissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission(),
        onResult = { granted ->
            hasStoragePermission = granted
        }
    )
    
    val galleryLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent(),
        onResult = { uri ->
            uri?.let { onImageSelected(it) }
        }
    )
    
    return {
        if (hasStoragePermission) {
            galleryLauncher.launch("image/*")
        } else {
            permissionLauncher.launch(Manifest.permission.READ_EXTERNAL_STORAGE)
        }
    }
} 