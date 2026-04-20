package com.example.campusiq.ui

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import androidx.appcompat.app.AppCompatActivity
import com.example.campusiq.R
import com.example.campusiq.utils.PreferenceManager

class SplashActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash)
        window.statusBarColor = android.graphics.Color.parseColor("#1A1A2E")
        window.navigationBarColor = android.graphics.Color.parseColor("#1A1A2E")

        val prefs = PreferenceManager(this)

        Handler(Looper.getMainLooper()).postDelayed({
            val next = if (prefs.isOnboarded)
                Intent(this, MainActivity::class.java)
            else
                Intent(this, OnboardingActivity::class.java)
            startActivity(next)
            finish()
        }, 2000)
    }
}