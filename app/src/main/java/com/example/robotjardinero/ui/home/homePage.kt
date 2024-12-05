package com.example.robotjardinero.ui.home

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.Card
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.robotjardinero.db.RealtimeManager
import com.example.robotjardinero.models.PlantModel

@Composable
fun HomePage(navController: NavController, realTimeManager: RealtimeManager) {
    val plants = remember { mutableStateOf<List<PlantModel>>(emptyList()) }
    val errorMessage = remember { mutableStateOf<String?>(null) }

    // Lanza la carga de datos al iniciar la Composable
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
        // Mostrar un mensaje de error si existe
        errorMessage.value?.let {
            Text(text = it, color = Color.Red)
        }

        // Mostrar un indicador de carga si la lista está vacía
        if (plants.value.isEmpty()) {
            CircularProgressIndicator(
                modifier = Modifier
                    .align(Alignment.CenterHorizontally)
                    .padding(16.dp)
            )
            Text(
                text = "Cargando las plantas...",
                modifier = Modifier.align(Alignment.CenterHorizontally)
            )
        } else {
            // Mostrar la lista de plantas en un LazyColumn
            LazyColumn(
                modifier = Modifier.fillMaxSize()
            ) {
                items(plants.value.size) { plantName ->
                    Card(
                        onClick = {
                            navController.navigate("cameraPage/${plants.value[plantName].id}")
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(8.dp)
                    ) {
                        Text(
                            text = plants.value[plantName].name,
                            modifier = Modifier.padding(16.dp)
                        )
                    }
                }
            }
        }
    }
}

