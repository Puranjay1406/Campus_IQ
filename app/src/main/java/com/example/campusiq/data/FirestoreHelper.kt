package com.example.campusiq.data

import com.example.campusiq.data.models.Expense
import com.example.campusiq.data.models.FoodEntry
import com.example.campusiq.data.models.ShoppingItem
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

class FirestoreHelper {

    private val db   = FirebaseFirestore.getInstance()
    private val auth = FirebaseAuth.getInstance()

    // Gets current user's UID — used as the root path
    private val uid: String
        get() = auth.currentUser?.uid ?: "anonymous"

    // ── Root references ───────────────────────────────────────────────────────
    private fun expensesRef()  = db.collection("users").document(uid).collection("expenses")
    private fun foodRef()      = db.collection("users").document(uid).collection("food_entries")
    private fun shoppingRef()  = db.collection("users").document(uid).collection("shopping_items")

    // ── EXPENSE ───────────────────────────────────────────────────────────────

    fun insertExpense(e: Expense, onResult: (Boolean) -> Unit) {
        val data = hashMapOf(
            "amount"      to e.amount,
            "category"    to e.category,
            "description" to e.description,
            "date"        to e.date,
            "isImpulsive" to e.isImpulsive
        )
        expensesRef().add(data)
            .addOnSuccessListener { onResult(true) }
            .addOnFailureListener { onResult(false) }
    }

    fun getAllExpenses(onResult: (List<Expense>) -> Unit) {
        expensesRef()
            .orderBy("date", com.google.firebase.firestore.Query.Direction.DESCENDING)
            .get()
            .addOnSuccessListener { snapshot ->
                val list = snapshot.documents.map { doc ->
                    Expense(
                        id          = doc.id.hashCode(),
                        firestoreId = doc.id,
                        amount      = doc.getDouble("amount") ?: 0.0,
                        category    = doc.getString("category") ?: "",
                        description = doc.getString("description") ?: "",
                        date        = doc.getString("date") ?: "",
                        isImpulsive = doc.getBoolean("isImpulsive") ?: false
                    )
                }
                onResult(list)
            }
            .addOnFailureListener { onResult(emptyList()) }
    }

    fun deleteExpense(firestoreId: String, onResult: (Boolean) -> Unit) {
        expensesRef().document(firestoreId).delete()
            .addOnSuccessListener { onResult(true) }
            .addOnFailureListener { onResult(false) }
    }

    fun getTotalExpense(onResult: (Double) -> Unit) {
        getAllExpenses { list ->
            onResult(list.sumOf { it.amount })
        }
    }

    fun getExpenseByCategory(onResult: (Map<String, Double>) -> Unit) {
        getAllExpenses { list ->
            onResult(list.groupBy { it.category }
                .mapValues { entry -> entry.value.sumOf { it.amount } })
        }
    }

    // ── FOOD ──────────────────────────────────────────────────────────────────

    fun insertFoodEntry(f: FoodEntry, onResult: (Boolean) -> Unit) {
        val data = hashMapOf(
            "mealType" to f.mealType,
            "foodItem" to f.foodItem,
            "cost"     to f.cost,
            "location" to f.location,
            "date"     to f.date,
            "mood"     to f.mood
        )
        foodRef().add(data)
            .addOnSuccessListener { onResult(true) }
            .addOnFailureListener { onResult(false) }
    }

    fun getAllFoodEntries(onResult: (List<FoodEntry>) -> Unit) {
        foodRef()
            .orderBy("date", com.google.firebase.firestore.Query.Direction.DESCENDING)
            .get()
            .addOnSuccessListener { snapshot ->
                val list = snapshot.documents.map { doc ->
                    FoodEntry(
                        id          = doc.id.hashCode(),
                        firestoreId = doc.id,
                        mealType    = doc.getString("mealType") ?: "",
                        foodItem    = doc.getString("foodItem") ?: "",
                        cost        = doc.getDouble("cost") ?: 0.0,
                        location    = doc.getString("location") ?: "",
                        date        = doc.getString("date") ?: "",
                        mood        = doc.getString("mood") ?: "Normal"
                    )
                }
                onResult(list)
            }
            .addOnFailureListener { onResult(emptyList()) }
    }

    fun deleteFoodEntry(firestoreId: String, onResult: (Boolean) -> Unit) {
        foodRef().document(firestoreId).delete()
            .addOnSuccessListener { onResult(true) }
            .addOnFailureListener { onResult(false) }
    }

    fun getTotalFoodCost(onResult: (Double) -> Unit) {
        getAllFoodEntries { list -> onResult(list.sumOf { it.cost }) }
    }

    fun getFoodCostByLocation(onResult: (Map<String, Double>) -> Unit) {
        getAllFoodEntries { list ->
            onResult(list.groupBy { it.location }
                .mapValues { entry -> entry.value.sumOf { it.cost } })
        }
    }

    // ── SHOPPING ──────────────────────────────────────────────────────────────

    fun insertShoppingItem(s: ShoppingItem, onResult: (Boolean) -> Unit) {
        val data = hashMapOf(
            "itemName"  to s.itemName,
            "amount"    to s.amount,
            "category"  to s.category,
            "isPlanned" to s.isPlanned,
            "date"      to s.date
        )
        shoppingRef().add(data)
            .addOnSuccessListener { onResult(true) }
            .addOnFailureListener { onResult(false) }
    }

    fun getAllShoppingItems(onResult: (List<ShoppingItem>) -> Unit) {
        shoppingRef()
            .orderBy("date", com.google.firebase.firestore.Query.Direction.DESCENDING)
            .get()
            .addOnSuccessListener { snapshot ->
                val list = snapshot.documents.map { doc ->
                    ShoppingItem(
                        id          = doc.id.hashCode(),
                        firestoreId = doc.id,
                        itemName    = doc.getString("itemName") ?: "",
                        amount      = doc.getDouble("amount") ?: 0.0,
                        category    = doc.getString("category") ?: "",
                        isPlanned   = doc.getBoolean("isPlanned") ?: false,
                        date        = doc.getString("date") ?: ""
                    )
                }
                onResult(list)
            }
            .addOnFailureListener { onResult(emptyList()) }
    }

    fun deleteShoppingItem(firestoreId: String, onResult: (Boolean) -> Unit) {
        shoppingRef().document(firestoreId).delete()
            .addOnSuccessListener { onResult(true) }
            .addOnFailureListener { onResult(false) }
    }

    fun getShoppingStats(onResult: (Triple<Int, Int, Double>) -> Unit) {
        getAllShoppingItems { list ->
            val impulsive = list.filter { !it.isPlanned }
            val planned   = list.filter { it.isPlanned }
            onResult(Triple(impulsive.size, planned.size, impulsive.sumOf { it.amount }))
        }
    }

    fun saveUserProfile(
        name: String,
        budget: Float,
        hostel: String,
        semester: Int,
        onResult: (Boolean) -> Unit
    ) {
        val data = hashMapOf(
            "studentName"   to name,
            "monthlyBudget" to budget,
            "hostelName"    to hostel,
            "semester"      to semester
        )
        db.collection("users").document(uid)
            .set(data, com.google.firebase.firestore.SetOptions.merge())
            .addOnSuccessListener { onResult(true) }
            .addOnFailureListener { onResult(false) }
    }

    fun loadUserProfile(onResult: (Map<String, Any>?) -> Unit) {
        db.collection("users").document(uid)
            .get()
            .addOnSuccessListener { doc ->
                onResult(if (doc.exists()) doc.data else null)
            }
            .addOnFailureListener { onResult(null) }
    }
}