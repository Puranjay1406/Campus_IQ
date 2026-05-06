package com.example.campusiq.ui

import android.os.Bundle
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat

open class BaseActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    fun setStatusBarColor(colorHex: String = "#1A1A2E") {
        window.statusBarColor = android.graphics.Color.parseColor(colorHex)
    }

    fun setNavBarColor(colorHex: String = "#F4F6FB") {
        window.navigationBarColor = android.graphics.Color.parseColor(colorHex)
    }

    // Call this after setContentView — auto adds padding for nav bar
    fun applySystemBarInsets(rootViewId: Int) {
        val root = findViewById<View>(rootViewId)
        ViewCompat.setOnApplyWindowInsetsListener(root) { view, insets ->
            val navBar = insets.getInsets(WindowInsetsCompat.Type.navigationBars())
            val statusBar = insets.getInsets(WindowInsetsCompat.Type.statusBars())
            view.setPadding(
                view.paddingLeft,
                statusBar.top,
                view.paddingRight,
                navBar.bottom
            )
            insets
        }
    }
}