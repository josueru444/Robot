package com.example.robotjardinero.ui.forms

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Slider
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.robotjardinero.db.RealtimeManager
import com.example.robotjardinero.models.PlantModel
import kotlin.text.toInt

@Composable
fun AddPlant(navigation: NavController, realTimeManager: RealtimeManager) {
    var selectedOption by remember { mutableStateOf(2) }
    var enableButton by remember { mutableStateOf(true) }
    var isError by rememberSaveable { mutableStateOf(false) }

    var plantName by remember { mutableStateOf("") }
    var temperaturePlant by remember { mutableStateOf("") }

    fun validate(text: String) {
        isError = text.toInt() > 55
    }

    if (plantName.isNotEmpty() && temperaturePlant.isNotEmpty()) {
        enableButton = true
    } else {
        enableButton = false
    }
    Column(
        modifier = Modifier.fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(16.dp),
    ) {
        Text(
            text = "Registra tu planta",
            fontWeight = FontWeight.SemiBold,
            fontSize = 24.sp
        )
        OutlinedTextField(
            value = plantName,
            onValueChange = { plantName = it },
            label = { Text("Nombre de la planta") }
        )
        OutlinedTextField(
            value = temperaturePlant,
            isError = isError,
            supportingText = {
                if (isError) {

                    Text(
                        text = "La temperatura no puede ser mayor a 55°C \uD83E\uDD75",
                        color = MaterialTheme.colorScheme.error,
                        modifier = Modifier.fillMaxWidth(),
                    )
                }
            },
            onValueChange = { temperaturePlant = it },
            label = { Text("Temperatura ideal") },
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
            keyboardActions = KeyboardActions { validate(temperaturePlant) },
        )

        LikertScaleSlider(
            options = listOf(
                "Muy Seco",
                "Seco",
                "Moderadamente húmedo",
                "Húmedo",
                "Muy húmedo"
            ),
            selectedOption = selectedOption,
            onOptionSelected = { selectedOption = it }
        )
        Button(
            enabled = enableButton,
            onClick = {
                if (temperaturePlant.toInt() > 55) {
                    isError = true
                } else {
                    isError = false
                    realTimeManager.addPlant(
                        plant = PlantModel(
                            // id = UUID.randomUUID().toString(),
                            name = plantName,
                            temperature = (temperaturePlant.toInt() + 1),
                            humidity = selectedOption
                        )
                    )

                    navigation.navigate("homePage")

                }
            }
        ) {
            Text(text = "Guardar planta \uD83E\uDEB4")
        }

    }
}

@Composable
fun LikertScaleSlider(
    options: List<String>,
    selectedOption: Int,
    onOptionSelected: (Int) -> Unit
) {
    var sliderPosition by remember { mutableStateOf(selectedOption.toFloat()) }

    Column(
        modifier = Modifier.padding(vertical = 16.dp, horizontal = 45.dp)
    ) {
        Slider(
            value = sliderPosition,
            onValueChange = { newPosition ->
                sliderPosition = newPosition
                onOptionSelected(newPosition.toInt())
            },
            valueRange = 0f..(options.size - 1).toFloat(),
            steps = options.size - 2
        )
        Text(
            text = options[sliderPosition.toInt()],
            modifier = Modifier.align(Alignment.CenterHorizontally)
        )
    }
}