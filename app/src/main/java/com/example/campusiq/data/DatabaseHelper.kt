package com.example.campusiq.data

import android.content.ContentValues
import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import com.example.campusiq.data.models.Expense
import com.example.campusiq.data.models.FoodEntry
import com.example.campusiq.data.models.ShoppingItem

class DatabaseHelper(context: Context) :
    SQLiteOpenHelper(context, DATABASE_NAME, null, DATABASE_VERSION) {

    companion object {
        private const val DATABASE_NAME = "campusiq.db"
        private const val DATABASE_VERSION = 1

        const val TABLE_EXPENSE  = "expenses"
        const val COL_EXP_ID     = "id"
        const val COL_EXP_AMT    = "amount"
        const val COL_EXP_CAT    = "category"
        const val COL_EXP_DESC   = "description"
        const val COL_EXP_DATE   = "date"
        const val COL_EXP_IMP    = "is_impulsive"

        const val TABLE_FOOD     = "food_entries"
        const val COL_FOOD_ID    = "id"
        const val COL_FOOD_MEAL  = "meal_type"
        const val COL_FOOD_ITEM  = "food_item"
        const val COL_FOOD_COST  = "cost"
        const val COL_FOOD_LOC   = "location"
        const val COL_FOOD_DATE  = "date"
        const val COL_FOOD_MOOD  = "mood"

        const val TABLE_SHOP     = "shopping_items"
        const val COL_SHOP_ID    = "id"
        const val COL_SHOP_NAME  = "item_name"
        const val COL_SHOP_AMT   = "amount"
        const val COL_SHOP_CAT   = "category"
        const val COL_SHOP_PLAN  = "is_planned"
        const val COL_SHOP_DATE  = "date"
    }

    override fun onCreate(db: SQLiteDatabase) {
        db.execSQL(
            "CREATE TABLE $TABLE_EXPENSE (" +
                    "$COL_EXP_ID INTEGER PRIMARY KEY AUTOINCREMENT," +
                    "$COL_EXP_AMT REAL NOT NULL," +
                    "$COL_EXP_CAT TEXT NOT NULL," +
                    "$COL_EXP_DESC TEXT DEFAULT ''," +
                    "$COL_EXP_DATE TEXT NOT NULL," +
                    "$COL_EXP_IMP INTEGER DEFAULT 0)"
        )
        db.execSQL(
            "CREATE TABLE $TABLE_FOOD (" +
                    "$COL_FOOD_ID INTEGER PRIMARY KEY AUTOINCREMENT," +
                    "$COL_FOOD_MEAL TEXT NOT NULL," +
                    "$COL_FOOD_ITEM TEXT NOT NULL," +
                    "$COL_FOOD_COST REAL NOT NULL," +
                    "$COL_FOOD_LOC TEXT NOT NULL," +
                    "$COL_FOOD_DATE TEXT NOT NULL," +
                    "$COL_FOOD_MOOD TEXT DEFAULT 'Normal')"
        )
        db.execSQL(
            "CREATE TABLE $TABLE_SHOP (" +
                    "$COL_SHOP_ID INTEGER PRIMARY KEY AUTOINCREMENT," +
                    "$COL_SHOP_NAME TEXT NOT NULL," +
                    "$COL_SHOP_AMT REAL NOT NULL," +
                    "$COL_SHOP_CAT TEXT NOT NULL," +
                    "$COL_SHOP_PLAN INTEGER DEFAULT 0," +
                    "$COL_SHOP_DATE TEXT NOT NULL)"
        )
    }

    override fun onUpgrade(db: SQLiteDatabase, old: Int, new: Int) {
        db.execSQL("DROP TABLE IF EXISTS $TABLE_EXPENSE")
        db.execSQL("DROP TABLE IF EXISTS $TABLE_FOOD")
        db.execSQL("DROP TABLE IF EXISTS $TABLE_SHOP")
        onCreate(db)
    }

    // ── EXPENSE ───────────────────────────────────────────────────────────────

    fun insertExpense(e: Expense): Long {
        val cv = ContentValues().apply {
            put(COL_EXP_AMT,  e.amount)
            put(COL_EXP_CAT,  e.category)
            put(COL_EXP_DESC, e.description)
            put(COL_EXP_DATE, e.date)
            put(COL_EXP_IMP,  if (e.isImpulsive) 1 else 0)
        }
        return writableDatabase.insert(TABLE_EXPENSE, null, cv)
    }

    fun getAllExpenses(): List<Expense> {
        val list = mutableListOf<Expense>()
        readableDatabase.rawQuery(
            "SELECT * FROM $TABLE_EXPENSE ORDER BY $COL_EXP_DATE DESC", null
        ).use { c ->
            while (c.moveToNext()) {
                list.add(Expense(
                    id          = c.getInt(c.getColumnIndexOrThrow(COL_EXP_ID)),
                    amount      = c.getDouble(c.getColumnIndexOrThrow(COL_EXP_AMT)),
                    category    = c.getString(c.getColumnIndexOrThrow(COL_EXP_CAT)),
                    description = c.getString(c.getColumnIndexOrThrow(COL_EXP_DESC)) ?: "",
                    date        = c.getString(c.getColumnIndexOrThrow(COL_EXP_DATE)),
                    isImpulsive = c.getInt(c.getColumnIndexOrThrow(COL_EXP_IMP)) == 1
                ))
            }
        }
        return list
    }

    fun deleteExpense(id: Int) =
        writableDatabase.delete(TABLE_EXPENSE, "$COL_EXP_ID=?", arrayOf(id.toString()))

    fun getTotalExpense(): Double {
        readableDatabase.rawQuery("SELECT SUM($COL_EXP_AMT) FROM $TABLE_EXPENSE", null)
            .use { c -> return if (c.moveToFirst() && !c.isNull(0)) c.getDouble(0) else 0.0 }
    }

    fun getExpenseByCategory(): Map<String, Double> {
        val map = mutableMapOf<String, Double>()
        readableDatabase.rawQuery(
            "SELECT $COL_EXP_CAT, SUM($COL_EXP_AMT) FROM $TABLE_EXPENSE GROUP BY $COL_EXP_CAT", null
        ).use { c -> while (c.moveToNext()) map[c.getString(0)] = c.getDouble(1) }
        return map
    }

    // ── FOOD ──────────────────────────────────────────────────────────────────

    fun insertFoodEntry(f: FoodEntry): Long {
        val cv = ContentValues().apply {
            put(COL_FOOD_MEAL, f.mealType)
            put(COL_FOOD_ITEM, f.foodItem)
            put(COL_FOOD_COST, f.cost)
            put(COL_FOOD_LOC,  f.location)
            put(COL_FOOD_DATE, f.date)
            put(COL_FOOD_MOOD, f.mood)
        }
        return writableDatabase.insert(TABLE_FOOD, null, cv)
    }

    fun getAllFoodEntries(): List<FoodEntry> {
        val list = mutableListOf<FoodEntry>()
        readableDatabase.rawQuery(
            "SELECT * FROM $TABLE_FOOD ORDER BY $COL_FOOD_DATE DESC", null
        ).use { c ->
            while (c.moveToNext()) {
                list.add(FoodEntry(
                    id       = c.getInt(c.getColumnIndexOrThrow(COL_FOOD_ID)),
                    mealType = c.getString(c.getColumnIndexOrThrow(COL_FOOD_MEAL)),
                    foodItem = c.getString(c.getColumnIndexOrThrow(COL_FOOD_ITEM)),
                    cost     = c.getDouble(c.getColumnIndexOrThrow(COL_FOOD_COST)),
                    location = c.getString(c.getColumnIndexOrThrow(COL_FOOD_LOC)),
                    date     = c.getString(c.getColumnIndexOrThrow(COL_FOOD_DATE)),
                    mood     = c.getString(c.getColumnIndexOrThrow(COL_FOOD_MOOD)) ?: "Normal"
                ))
            }
        }
        return list
    }

    fun deleteFoodEntry(id: Int) =
        writableDatabase.delete(TABLE_FOOD, "$COL_FOOD_ID=?", arrayOf(id.toString()))

    fun getTotalFoodCost(): Double {
        readableDatabase.rawQuery("SELECT SUM($COL_FOOD_COST) FROM $TABLE_FOOD", null)
            .use { c -> return if (c.moveToFirst() && !c.isNull(0)) c.getDouble(0) else 0.0 }
    }

    fun getFoodCostByLocation(): Map<String, Double> {
        val map = mutableMapOf<String, Double>()
        readableDatabase.rawQuery(
            "SELECT $COL_FOOD_LOC, SUM($COL_FOOD_COST) FROM $TABLE_FOOD GROUP BY $COL_FOOD_LOC", null
        ).use { c -> while (c.moveToNext()) map[c.getString(0)] = c.getDouble(1) }
        return map
    }

    // ── SHOPPING ──────────────────────────────────────────────────────────────

    fun insertShoppingItem(s: ShoppingItem): Long {
        val cv = ContentValues().apply {
            put(COL_SHOP_NAME, s.itemName)
            put(COL_SHOP_AMT,  s.amount)
            put(COL_SHOP_CAT,  s.category)
            put(COL_SHOP_PLAN, if (s.isPlanned) 1 else 0)
            put(COL_SHOP_DATE, s.date)
        }
        return writableDatabase.insert(TABLE_SHOP, null, cv)
    }

    fun getAllShoppingItems(): List<ShoppingItem> {
        val list = mutableListOf<ShoppingItem>()
        readableDatabase.rawQuery(
            "SELECT * FROM $TABLE_SHOP ORDER BY $COL_SHOP_DATE DESC", null
        ).use { c ->
            while (c.moveToNext()) {
                list.add(ShoppingItem(
                    id        = c.getInt(c.getColumnIndexOrThrow(COL_SHOP_ID)),
                    itemName  = c.getString(c.getColumnIndexOrThrow(COL_SHOP_NAME)),
                    amount    = c.getDouble(c.getColumnIndexOrThrow(COL_SHOP_AMT)),
                    category  = c.getString(c.getColumnIndexOrThrow(COL_SHOP_CAT)),
                    isPlanned = c.getInt(c.getColumnIndexOrThrow(COL_SHOP_PLAN)) == 1,
                    date      = c.getString(c.getColumnIndexOrThrow(COL_SHOP_DATE))
                ))
            }
        }
        return list
    }

    fun deleteShoppingItem(id: Int) =
        writableDatabase.delete(TABLE_SHOP, "$COL_SHOP_ID=?", arrayOf(id.toString()))

    // Returns: Triple(impulsiveCount, plannedCount, impulsiveSpend)
    fun getShoppingStats(): Triple<Int, Int, Double> {
        var impCount = 0; var impAmt = 0.0; var planCount = 0
        readableDatabase.rawQuery(
            "SELECT COUNT(*), SUM($COL_SHOP_AMT) FROM $TABLE_SHOP WHERE $COL_SHOP_PLAN=0", null
        ).use { c ->
            if (c.moveToFirst()) {
                impCount = c.getInt(0)
                impAmt   = if (c.isNull(1)) 0.0 else c.getDouble(1)
            }
        }
        readableDatabase.rawQuery(
            "SELECT COUNT(*) FROM $TABLE_SHOP WHERE $COL_SHOP_PLAN=1", null
        ).use { c -> if (c.moveToFirst()) planCount = c.getInt(0) }
        return Triple(impCount, planCount, impAmt)
    }
}