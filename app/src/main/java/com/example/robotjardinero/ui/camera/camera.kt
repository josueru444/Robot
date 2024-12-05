package com.example.robotjardinero.ui.camera

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.util.Log
import android.view.ViewGroup
import android.widget.Toast
import androidx.camera.core.ImageCapture
import androidx.camera.core.ImageCaptureException
import androidx.camera.view.LifecycleCameraController
import androidx.camera.view.PreviewView
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.core.content.ContextCompat
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.compose.LocalLifecycleOwner
import androidx.navigation.NavController
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.isGranted
import com.google.accompanist.permissions.rememberPermissionState
import com.google.firebase.database.FirebaseDatabase
import java.io.File
import java.util.concurrent.Executor

@OptIn(ExperimentalPermissionsApi::class, ExperimentalMaterial3Api::class)
@Composable
fun CameraPage(navController: NavController) {
    val databaseReference = FirebaseDatabase.getInstance().getReference("plants")
    val plantId = navController.currentBackStackEntry?.arguments?.getString("plantId") ?: ""
    Log.d("CameraPage", "Plant ID: $plantId")
    var chlorophyllLevel by remember { mutableStateOf<Float?>(null) }
    val permissions = rememberPermissionState(permission = android.Manifest.permission.CAMERA)
    val context = LocalContext.current
    val cameraController = remember { LifecycleCameraController(context) }
    val lifeCycle = LocalLifecycleOwner.current

    if (plantId.isNotBlank() && (chlorophyllLevel != null && chlorophyllLevel != 0f)) {
        val updates = mapOf<String, Any>(
            "clorophyllIndex" to chlorophyllLevel!!
        )
        databaseReference.child(plantId).updateChildren(updates)
            .addOnSuccessListener {
                Toast.makeText(context, "Nivel de clorofila actualizado", Toast.LENGTH_SHORT)
                    .show()
                println("Campo actualizado exitosamente.")
                navController.popBackStack()
            }
            .addOnFailureListener { exception ->
                println("Error al actualizar el campo: ${exception.message}")
                Toast.makeText(context, "Error al actualizar el campo", Toast.LENGTH_SHORT).show()
            }
    }

    if (!permissions.status.isGranted) {
        LaunchedEffect(Unit) {
            permissions.launchPermissionRequest()
        }
    }

    Scaffold(
        modifier = Modifier.fillMaxSize(),
        bottomBar = {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.Center,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Button(onClick = {
                    takePicture(
                        cameraController = cameraController,
                        executor = ContextCompat.getMainExecutor(context)

                    ) { level ->
                        chlorophyllLevel = level
                    }
                }) {
                    Icon(Icons.Filled.Add, contentDescription = "")
                }
            }
        },
        content = { innerPadding ->
            if (permissions.status.isGranted) {
                Column(modifier = Modifier.padding(innerPadding)) {
                    CameraPreview(
                        cameraController = cameraController,
                        lifeCycle = lifeCycle,
                        modifier = Modifier.weight(1f)
                    )
                    chlorophyllLevel?.let {
                        Text("Chlorophyll Index: $it", modifier = Modifier.padding(16.dp))
                    }
                }
            } else {
                Text("Camera permission denied", modifier = Modifier.padding(innerPadding))
            }
        }
    )
}

private fun takePicture(
    cameraController: LifecycleCameraController,
    executor: Executor,
    onResult: (Float) -> Unit
) {
    val file = File.createTempFile("image", ".jpg")
    val outputDirectory = ImageCapture.OutputFileOptions.Builder(file).build()
    cameraController.takePicture(
        outputDirectory,
        executor,
        object : ImageCapture.OnImageSavedCallback {
            override fun onImageSaved(outputFileResults: ImageCapture.OutputFileResults) {
                val bitmap = BitmapFactory.decodeFile(file.absolutePath) ?: return
                file.delete()
                val chlorophyllIndex = calculateChlorophyllIndex(bitmap)
                onResult(chlorophyllIndex)
            }

            override fun onError(exception: ImageCaptureException) {
                Log.e("CameraPage", "Image capture failed", exception)
            }
        })
}

fun calculateChlorophyllIndex(bitmap: Bitmap): Float {
    val scaledBitmap = Bitmap.createScaledBitmap(bitmap, 100, 100, true)

    var totalGreen = 0.0
    var totalRed = 0.0
    var greenPixels = 0

    var minGreenRed = Float.MAX_VALUE
    var maxGreenRed = Float.MIN_VALUE
    val greenRedRatios = mutableListOf<Float>()

    for (y in 0 until scaledBitmap.height) {
        for (x in 0 until scaledBitmap.width) {
            val pixel = scaledBitmap.getPixel(x, y)
            val red = (pixel shr 16) and 0xFF
            val green = (pixel shr 8) and 0xFF

            if (green > red * 1.1) {
                val greenRedRatio = green.toFloat() / (red + 1f)
                greenRedRatios.add(greenRedRatio)

                minGreenRed = minOf(minGreenRed, greenRedRatio)
                maxGreenRed = maxOf(maxGreenRed, greenRedRatio)
            }
        }
    }

    return if (greenRedRatios.isNotEmpty()) {
        val avgRatio = greenRedRatios.average().toFloat()

        if (minGreenRed != maxGreenRed) {
            (avgRatio - minGreenRed) / (maxGreenRed - minGreenRed)
        } else {
            0f
        }
    } else {
        0f
    }
}

@Composable
private fun CameraPreview(
    cameraController: LifecycleCameraController,
    lifeCycle: LifecycleOwner,
    modifier: Modifier = Modifier,
) {
    cameraController.bindToLifecycle(lifeCycle)

    AndroidView(modifier = modifier, factory = { context ->
        val previewView = PreviewView(context).apply {
            layoutParams = ViewGroup.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.MATCH_PARENT
            )
        }
        previewView.controller = cameraController
        previewView
    })
}
