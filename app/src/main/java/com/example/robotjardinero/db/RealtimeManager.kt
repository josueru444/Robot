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

    fun deletePlant(idPlant: String) {
        databaseReference.child(idPlant).removeValue()
    }

    fun getPlants(onSuccess: (List<String>) -> Unit, onFailure: (Exception) -> Unit) {
        databaseReference.get().addOnSuccessListener { snapshot ->
            val plantList = mutableListOf<String>()
            for (plantSnapshot in snapshot.children) {
                val plant = plantSnapshot.getValue(PlantModel::class.java)
                if (plant != null) {
                    // Agregamos la planta al listado como un String
                    plantList.add("Name: ${plant.name}, Temp: ${plant.temperature}, Humidity: ${plant.humidity}")
                }
            }
            onSuccess(plantList) // Retorna la lista de plantas en formato String
        }.addOnFailureListener { exception ->
            onFailure(exception) // En caso de error, pasa el error
        }
    }



}