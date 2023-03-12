package com.abstractcoder.baudoapp

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.abstractcoder.baudoapp.databinding.ActivitySplashBinding
import com.google.firebase.analytics.FirebaseAnalytics

class SplashActivity : AppCompatActivity() {
    private lateinit var binding: ActivitySplashBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySplashBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Analytics event
        val analytics:FirebaseAnalytics = FirebaseAnalytics.getInstance(this)
        val bundle = Bundle()
        bundle.putString("message", "Integracion de Firebase Completa")
        analytics.logEvent("InitScreen", bundle)

        // Setup
        setup()
    }

    private fun setup() {
        title = "Splash"
        binding.splashButton.setOnClickListener {
            showLogIn()
        }
    }

    private fun showLogIn() {
        val logInIntent = Intent(this, LogInActivity::class.java)
        startActivity(logInIntent)
    }
}