package com.abstractcoder.baudoapp

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.appcompat.app.AlertDialog
import com.google.firebase.analytics.FirebaseAnalytics
import com.google.firebase.auth.FirebaseAuth
import kotlinx.android.synthetic.main.activity_auth.*

class AuthActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_auth)

        // Analytics event
        val analytics:FirebaseAnalytics = FirebaseAnalytics.getInstance(this)
        val bundle = Bundle()
        bundle.putString("message", "Integracion de Firebase Completa")
        analytics.logEvent("InitScreen", bundle)

        // Setup
        setup()
    }

    private fun setup() {
        title = "Autenticacion"
        signUpButton.setOnClickListener {
            if (emailEditText.text.isNotEmpty() && passwordEditText.text.isNotEmpty()) {
                FirebaseAuth.getInstance().createUserWithEmailAndPassword(emailEditText.text.toString(),
                    passwordEditText.text.toString()).addOnCompleteListener {
                        if (it.isSuccessful) {
                            showHome(it.result?.user?.email ?: "", ProviderType.BASIC,)
                        } else {
                            showAlert()
                        }
                }
            }
        }

        logInButton.setOnClickListener {
            if (emailEditText.text.isNotEmpty() && passwordEditText.text.isNotEmpty()) {
                FirebaseAuth.getInstance().signInWithEmailAndPassword(emailEditText.text.toString(),
                    passwordEditText.text.toString()).addOnCompleteListener {
                    if (it.isSuccessful) {
                        showHome(it.result?.user?.email ?: "", ProviderType.BASIC,)
                    } else {
                        showAlert()
                    }
                }
            }
        }
    }

    private fun showAlert() {
        val builder = AlertDialog.Builder(this)
        builder.setTitle("Error")
        builder.setMessage("Se ha producido un error autenticando al usuario")
        val dialog:AlertDialog = builder.create()
        dialog.show()
    }

    private fun showHome(email: String, provider: ProviderType) {
        val homeIntent = Intent(this, HomeActivity::class.java).apply {
            //putExtraData("email", email)
        }
        startActivity(homeIntent)
    }
}