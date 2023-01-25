package com.abstractcoder.baudoapp

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import com.google.firebase.analytics.FirebaseAnalytics
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import kotlinx.android.synthetic.main.activity_sign_up.*

class SignUpActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_sign_up)

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
        signUpButton.setOnClickListener {
            val name: String = registerNameEditText.text.toString()
            val email: String = registerEmailEditText.text.toString()
            val password: String = registerPasswordEditText.text.toString()
            val user = User(name, email, password)
            if (registerNameEditText.text.isNotEmpty() && registerEmailEditText.text.isNotEmpty() && registerPasswordEditText.text.isNotEmpty()) {
                authInstance.createUserWithEmailAndPassword(registerEmailEditText.text.toString(),
                    registerPasswordEditText.text.toString()).addOnCompleteListener {
                    if (it.isSuccessful) {
                        val id: String = it.result?.user?.uid ?: ""
                        registerUser(id, user)
                        showHome(name, it.result?.user?.email ?: "", ProviderType.BASIC,)
                    } else {
                        val exception = it.exception
                        showAlert(exception)
                    }
                }
            }
        }

        registerUpperLogo.setOnClickListener {
            showLogIn()
        }
    }

    private fun registerUser(id: String, user: User) {
        val database = Firebase.database
        val myRef = database.getReference("DB")

        myRef.child("Users").child(id).setValue(user).addOnSuccessListener {
            registerNameEditText.text.clear()
            registerEmailEditText.text.clear()
            registerPasswordEditText.text.clear()

            Toast.makeText(this, "Usuario creado", Toast.LENGTH_SHORT).show()
        }
    }

    private fun showAlert(exception: Exception?) {
        val builder = AlertDialog.Builder(this)
        builder.setTitle("Error")
        builder.setMessage(exception?.message ?: "")
        val dialog: AlertDialog = builder.create()
        dialog.show()
    }

    private fun showHome(name: String, email: String, provider: ProviderType) {
        val homeIntent = Intent(this, HomeActivity::class.java).apply {
            putExtra("name", name)
            putExtra("email", email)
            putExtra("provider", provider.name)
        }
        startActivity(homeIntent)
    }

    private fun showLogIn() {
        val registerIntent = Intent(this, LogInActivity::class.java)
        startActivity(registerIntent)
    }
}