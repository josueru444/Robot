package com.example.robotjardinero.models
// PlantModel.kt
data class PlantModel(
    var id: String = "",
    var name: String = "",
    var temperature: Int = 0,
    var humidity: Int = 0
) {
    constructor() : this("", "", 0, 0)
}