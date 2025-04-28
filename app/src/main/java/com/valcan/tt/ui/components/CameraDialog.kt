package com.valcan.tt.ui.components

import android.Manifest
import android.content.Context
import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.camera.core.*
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.camera.view.PreviewView
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.core.content.ContextCompat
import com.valcan.tt.R
import java.io.File
import java.text.SimpleDateFormat
import java.util.*
import java.util.concurrent.Executor
import kotlin.coroutines.resume
import kotlin.coroutines.suspendCoroutine

@Composable
fun CameraDialog(
    onImageCaptured: (Uri) -> Unit,
    onDismiss: () -> Unit
) {
    val context = LocalContext.current

    var hasCameraPermission by remember {
        mutableStateOf(
            ContextCompat.checkSelfPermission(
                context,
                Manifest.permission.CAMERA
            ) == android.content.pm.PackageManager.PERMISSION_GRANTED
        )
    }

    val launcher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission(),
        onResult = { granted ->
            hasCameraPermission = granted
        }
    )

    LaunchedEffect(Unit) {
        launcher.launch(Manifest.permission.CAMERA)
    }

    if (hasCameraPermission) {
        AlertDialog(
            onDismissRequest = onDismiss,
            text = {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(400.dp)
                ) {
                    CameraPreview(
                        onImageCaptured = onImageCaptured,
                        onError = { /* Gestisci l'errore */ }
                    )
                }
            },
            confirmButton = { }
        )
    }
}

@Composable
fun CameraPreview(
    onImageCaptured: (Uri) -> Unit,
    onError: (ImageCaptureException) -> Unit
) {
    val context = LocalContext.current
    val lifecycleOwner = LocalLifecycleOwner.current
    
    val preview = Preview.Builder().build()
    val previewView = remember { PreviewView(context) }
    val imageCapture: ImageCapture = remember { ImageCapture.Builder().build() }
    val cameraSelector = CameraSelector.DEFAULT_BACK_CAMERA
    
    LaunchedEffect(previewView) {
        val cameraProvider = context.getCameraProvider()
        cameraProvider.unbindAll()
        cameraProvider.bindToLifecycle(
            lifecycleOwner,
            cameraSelector,
            preview,
            imageCapture
        )
        
        preview.setSurfaceProvider(previewView.surfaceProvider)
    }

    Box(contentAlignment = Alignment.BottomCenter, modifier = Modifier.fillMaxSize()) {
        AndroidView(
            factory = { previewView },
            modifier = Modifier.fillMaxSize()
        )
        
        IconButton(
            modifier = Modifier
                .padding(bottom = 20.dp)
                .size(56.dp),
            onClick = {
                val file = File(
                    context.getOutputDirectory(),
                    SimpleDateFormat("yyyy-MM-dd-HH-mm-ss-SSS", Locale.ITALIAN)
                        .format(System.currentTimeMillis()) + ".jpg"
                )

                val outputOptions = ImageCapture.OutputFileOptions.Builder(file).build()

                imageCapture.takePicture(
                    outputOptions,
                    context.executor,
                    object : ImageCapture.OnImageSavedCallback {
                        override fun onImageSaved(output: ImageCapture.OutputFileResults) {
                            output.savedUri?.let { uri ->
                                onImageCaptured(uri)
                            }
                        }

                        override fun onError(exc: ImageCaptureException) {
                            onError(exc)
                        }
                    }
                )
            }
        ) {
            Image(
                painter = painterResource(id = R.drawable.photo),
                contentDescription = "Scatta foto",
                modifier = Modifier.size(40.dp)
            )
        }
    }
}

private suspend fun Context.getCameraProvider(): ProcessCameraProvider = suspendCoroutine { continuation ->
    val cameraProviderFuture = ProcessCameraProvider.getInstance(this)
    cameraProviderFuture.addListener({
        try {
            val provider = cameraProviderFuture.get()
            continuation.resume(provider)
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }, ContextCompat.getMainExecutor(this))
}

private val Context.executor: Executor
    get() = ContextCompat.getMainExecutor(this)

private fun Context.getOutputDirectory(): File {
    val mediaDir = getExternalFilesDir(android.os.Environment.DIRECTORY_PICTURES)?.let {
        File(it, "TrendyTracker").apply { mkdirs() }
    }
    return if (mediaDir != null && mediaDir.exists())
        mediaDir else filesDir
} 