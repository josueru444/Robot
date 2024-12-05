package com.example.robotjardinero.db

import android.content.Context
import com.example.robotjardinero.models.PlantModel
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase

class RealtimeManager(context: Context) {
    private val databaseReference : DatabaseReference = FirebaseDatabase.getInstance().getReference("plants")

    fun addPlant(plant: PlantModel) {
        val key = databaseReference.push().key
        if (key != null) {
            databaseReference.child(key.toString()).setValue(plant)
        }
    }

    fun updatePlant(idPlant: String, plant: PlantModel) {
        databaseReference.child(idPlant).setValue(plant)
    }

    fun deletePlant(idPlant: String) {
        databaseReference.child(idPlant).removeValue()
    }

    fun getPlants(onSuccess: (List<PlantModel>) -> Unit, onFailure: (Exception) -> Unit) {
        databaseReference.get().addOnSuccessListener { snapshot ->
            val plantList = mutableListOf<PlantModel>()
            for (plantSnapshot in snapshot.children) {
                val plant = plantSnapshot.getValue(PlantModel::class.java)
                if (plant != null) {
                    // Agregamos la planta al listado como un String
                    val plantId = plantSnapshot.key

                    plantList.add(
                        PlantModel(
                            id = plantId ?: "",
                            name = plant.name,
                            temperature = plant.temperature,
                            connected = plant.connected,
                            humidity = plant.humidity
                        )
                    )
                }
            }
            onSuccess(plantList)
        }.addOnFailureListener { exception ->
            onFailure(exception) // En caso de error, pasa el error
        }
    }



}