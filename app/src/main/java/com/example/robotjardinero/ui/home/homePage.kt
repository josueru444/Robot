package com.example.robotjardinero.ui.home

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.robotjardinero.db.RealtimeManager

@Composable
fun HomePage(navController: NavController, realTimeManager: RealtimeManager) {
    val plants = remember { mutableStateOf<List<String>>(emptyList()) }
    val errorMessage = remember { mutableStateOf<String?>(null) }

    LaunchedEffect(Unit) {
        realTimeManager.getPlants(
            onSuccess = { plantList ->
                plants.value = plantList
            },
            onFailure = { exception ->
                errorMessage.value = "Error al cargar las plantas: ${exception.message}"
            }
        )
    }

    // Mostrar el contenido
    Column(modifier = Modifier.padding(16.dp)) {
        // Si hay un error, lo mostramos
        errorMessage.value?.let {
            Text(text = it, color = Color.Red)
        }

        // Mostrar la lista de plantas
        if (plants.value.isEmpty()) {
            Text(text = "Cargando las plantas...")
        } else {
            plants.value.forEach { plant ->
                Text(text = plant)
            }
        }
    }

}