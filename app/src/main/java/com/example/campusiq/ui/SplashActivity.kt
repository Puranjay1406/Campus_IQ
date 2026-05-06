package com.example.campusiq.ui

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import com.example.campusiq.R
import com.example.campusiq.ui.auth.LoginActivity
import com.google.firebase.auth.FirebaseAuth

class SplashActivity : BaseActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash)
        setStatusBarColor("#1A1A2E")
        setNavBarColor("#F4F6FB")

        window.statusBarColor = android.graphics.Color.parseColor("#1A1A2E")
        window.navigationBarColor = android.graphics.Color.parseColor("#1A1A2E")

        Handler(Looper.getMainLooper()).postDelayed({
            val currentUser = FirebaseAuth.getInstance().currentUser
            if (currentUser != null) {
                startActivity(Intent(this, HomeActivity::class.java))
            } else {
                startActivity(Intent(this, LoginActivity::class.java))
            }
            finish()
        }, 1000)
    }
}