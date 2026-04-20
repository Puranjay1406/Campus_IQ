package com.example.campusiq.utils

import android.content.Context

class PreferenceManager(context: Context) {

    private val prefs = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE)

    companion object {
        const val PREF_NAME     = "campusiq_prefs"
        const val KEY_NAME      = "student_name"
        const val KEY_BUDGET    = "monthly_budget"
        const val KEY_HOSTEL    = "hostel_name"
        const val KEY_SEMESTER  = "semester"
        const val KEY_ONBOARDED = "is_onboarded"
    }

    var studentName: String
        get() = prefs.getString(KEY_NAME, "") ?: ""
        set(v) { prefs.edit().putString(KEY_NAME, v).apply() }

    var monthlyBudget: Float
        get() = prefs.getFloat(KEY_BUDGET, 5000f)
        set(v) { prefs.edit().putFloat(KEY_BUDGET, v).apply() }

    var hostelName: String
        get() = prefs.getString(KEY_HOSTEL, "") ?: ""
        set(v) { prefs.edit().putString(KEY_HOSTEL, v).apply() }

    var semester: Int
        get() = prefs.getInt(KEY_SEMESTER, 1)
        set(v) { prefs.edit().putInt(KEY_SEMESTER, v).apply() }

    var isOnboarded: Boolean
        get() = prefs.getBoolean(KEY_ONBOARDED, false)
        set(v) { prefs.edit().putBoolean(KEY_ONBOARDED, v).apply() }
}