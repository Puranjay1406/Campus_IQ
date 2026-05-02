package com.example.campusiq.data.models

data class ShoppingItem(
    val id: Int = 0,
    val firestoreId: String = "",
    val itemName: String,
    val amount: Double,
    val category: String,
    val isPlanned: Boolean,
    val date: String
)