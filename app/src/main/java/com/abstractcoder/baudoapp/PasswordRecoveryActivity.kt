package com.pereira.baudoapp

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import com.pereira.baudoapp.databinding.ActivityPasswordRecoveryBinding
import com.google.firebase.auth.FirebaseAuth

class PasswordRecoveryActivity : AppCompatActivity() {

    private lateinit var binding: ActivityPasswordRecoveryBinding
    private val authInstance = FirebaseAuth.getInstance()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityPasswordRecoveryBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setup()
    }

    private fun setup() {
        title = "Reset de contraseña"

        binding.backButton.setOnClickListener {
            finish()
        }

        binding.resetButton.setOnClickListener {
            val email = binding.resetEmailEditText.text.toString()
            if (email != null) {
                sendPassResetMail(email)
            }
        }
    }

    private fun showAlert(exception: Exception?) {
        val builder = AlertDialog.Builder(this)
        builder.setTitle("Error")
        builder.setMessage(exception?.message ?: "")
        val dialog: AlertDialog = builder.create()
        dialog.show()
    }

    private fun showLogIn() {
        val registerIntent = Intent(this, LogInActivity::class.java)
        startActivity(registerIntent)
    }

    private fun sendPassResetMail(email: String) {
        authInstance.sendPasswordResetEmail(email).addOnCompleteListener {
            if (it.isSuccessful) {
                Toast.makeText(this, "Correo de cambio de contraseña enviado", Toast.LENGTH_SHORT).show()
                showLogIn()
            } else {
                val exception = it.exception
                showAlert(exception)
            }
        }
    }
}