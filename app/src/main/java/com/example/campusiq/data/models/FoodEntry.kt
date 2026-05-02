package com.example.campusiq.data.models

data class FoodEntry(
    val id: Int = 0,
    val firestoreId: String = "",
    val mealType: String,
    val foodItem: String,
    val cost: Double,
    val location: String,
    val date: String,
    val mood: String
)