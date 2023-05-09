package com.abstractcoder.baudoapp

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import com.abstractcoder.baudoapp.databinding.ActivitySignUpBinding
import com.google.firebase.analytics.FirebaseAnalytics
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

class SignUpActivity : AppCompatActivity() {

    private val db = FirebaseFirestore.getInstance()
    private lateinit var binding: ActivitySignUpBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySignUpBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Analytics event
        val analytics: FirebaseAnalytics = FirebaseAnalytics.getInstance(this)
        val bundle = Bundle()
        bundle.putString("message", "Integracion de Firebase Completa")
        analytics.logEvent("LogInScreen", bundle)

        // Setup
        setup()
    }

    private fun setup() {
        title = "Registro"
        val authInstance = FirebaseAuth.getInstance()
        binding.signUpButton.setOnClickListener {
            val name: String = binding.registerNameEditText.text.toString()
            val email: String = binding.registerEmailEditText.text.toString()
            val password: String = binding.registerPasswordEditText.text.toString()
            val rightsAccepted: Boolean = binding.rightsSwitch.isChecked
            val user = User(name, email, password)
            if (binding.registerNameEditText.text.isNotEmpty() && binding.registerEmailEditText.text.isNotEmpty() && binding.registerPasswordEditText.text.isNotEmpty()) {
                if (rightsAccepted) {
                    authInstance.createUserWithEmailAndPassword(
                        binding.registerEmailEditText.text.toString(),
                        binding.registerPasswordEditText.text.toString()
                    ).addOnCompleteListener {
                        if (it.isSuccessful) {
                            val fireUser = authInstance.currentUser
                            fireUser?.sendEmailVerification()
                            registerUser(user, ProviderType.BASIC)
                            Toast.makeText(
                                this,
                                "Correo de verificacion enviado",
                                Toast.LENGTH_SHORT
                            ).show()
                            showLogIn()
                        } else {
                            val exception = it.exception
                            showAlert(exception)
                        }
                    }
                } else {
                    Toast.makeText(
                        this,
                        "No ha aceptado la politica de tratamiento de datos",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }
        }

        binding.registerUpperLogo.setOnClickListener {
            showLogIn()
        }
    }

    private fun registerUser(user: User, provider: ProviderType) {
        db.collection("users").document(user.email).set(
            mapOf("provider" to provider,
                "verified" to false,
                "name" to user.name,
                "password" to user.password,
                "user_pic" to "",
                "saved_posts" to emptyList<String>(),
                "liked_posts" to emptyList<String>(),
                "disliked_posts" to emptyList<String>(),
                "indifferent_posts" to emptyList<String>()
            )
        )
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
}