package com.example.campusiq.data.models

data class Expense(
    val id: Int = 0,
    val firestoreId: String = "",
    val amount: Double,
    val category: String,
    val description: String,
    val date: String,
    val isImpulsive: Boolean = false
)