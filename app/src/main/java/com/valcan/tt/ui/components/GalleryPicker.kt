package com.valcan.tt.ui.components

import android.Manifest
import android.content.Context
import android.net.Uri
import android.os.Build
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
    
    // Determina quale permesso richiedere in base alla versione di Android
    val permission = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
        Manifest.permission.READ_MEDIA_IMAGES
    } else {
        Manifest.permission.READ_EXTERNAL_STORAGE
    }
    
    var hasStoragePermission by remember {
        mutableStateOf(
            ContextCompat.checkSelfPermission(
                context,
                permission
            ) == android.content.pm.PackageManager.PERMISSION_GRANTED
        )
    }
    
    val galleryLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent(),
        onResult = { uri ->
            uri?.let { onImageSelected(it) }
        }
    )
    
    val permissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission(),
        onResult = { granted ->
            hasStoragePermission = granted
            if (granted) {
                galleryLauncher.launch("image/*")
            }
        }
    )
    
    return {
        if (hasStoragePermission) {
            galleryLauncher.launch("image/*")
        } else {
            permissionLauncher.launch(permission)
        }
    }
} 