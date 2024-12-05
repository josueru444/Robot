package com.example.robotjardinero.models

// PlantModel.kt
data class PlantModel(
    var id: String = "",
    var name: String = "",
    var temperature: Int = 0,
    var connected: Boolean = false,
    var humidity: Int = 0
) {
    constructor() : this("", "", 0, false, 0)
}

data class updatePlantModel(
    var clorophyllIndex: Double = 0.0 ,
)