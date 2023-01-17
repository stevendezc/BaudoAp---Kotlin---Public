package com.abstractcoder.baudoapp

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.google.firebase.auth.FirebaseAuth
import kotlinx.android.synthetic.main.activity_home.*

enum class ProviderType {
    BASIC
}

class HomeActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_home)

        // Recover data with bundle
        val bundle = intent.extras
        val email = bundle?.getString("email")
        // Setup incoming data
        setup(email ?: "")
    }

    private fun setup(email: String) {
        title = "Inicio"
        emailTextView.text = email

        signOutbutton.setOnClickListener {
            FirebaseAuth.getInstance().signOut()
            onBackPressed()
        }
    }
}