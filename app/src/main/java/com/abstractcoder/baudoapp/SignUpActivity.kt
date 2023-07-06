package com.abstractcoder.baudoapp

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Patterns
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import com.abstractcoder.baudoapp.databinding.ActivitySignUpBinding
import com.google.firebase.analytics.FirebaseAnalytics
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import java.util.regex.Pattern

class SignUpActivity : AppCompatActivity() {

    private val db = FirebaseFirestore.getInstance()
    private lateinit var binding: ActivitySignUpBinding

    private val PASSWORD_PATTERN: Pattern =
        Pattern.compile("^" +
                "(?=.*[A-Z])" +         // at least 1 uppercase letter
                "(?=.*[!@#\$&*])" +     // at least 1 special character
                "(?=.*[0-9])" +         // at least 1 numeric character
                "(?=.*[a-z])" +         // at least 1 lowercase letter
                "(?=\\S+$)" +           // no whitespaces
                ".{8,}" +                // min length of 8 characters
                "\$")

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

        binding.backButton.setOnClickListener {
            finish()
        }

        binding.signUpButton.setOnClickListener {
            val name: String = binding.registerNameEditText.text.toString()
            val email: String = binding.registerEmailEditText.text.toString()
            val password: String = binding.registerPasswordEditText.text.toString()
            val rightsAccepted: Boolean = binding.rightsSwitch.isChecked
            if (validateForm(name, email, password)) {
                if (rightsAccepted) {
                    authInstance.createUserWithEmailAndPassword(
                        binding.registerEmailEditText.text.toString(),
                        binding.registerPasswordEditText.text.toString()
                    ).addOnCompleteListener {
                        if (it.isSuccessful) {
                            var user = User(it.result.user!!.uid, name, email, password)
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

    private fun validateForm(name: String, email: String, password: String): Boolean {
        var validForm = true
        if (name.isEmpty()) {
            binding.registerNameEditText.error = "El nombre de usuario no puede estar vacio"
            validForm = false
        }
        if (email.isEmpty()) {
            binding.registerEmailEditText.error = "El correo electronico no puede estar vacio"
            validForm = false
        }
        if (email.isNotEmpty() && !Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            binding.registerEmailEditText.error = "El correo electronico no es valido"
            validForm = false
        }
        if (password.isEmpty()) {
            binding.registerPasswordEditText.error = "Contraseña no puede estar vacia"
            validForm = false
        }
        if (!PASSWORD_PATTERN.matcher(password).matches()) {
            binding.registerPasswordEditText.error = "La contraseña debe tener al menos 8 caracteres," +
                    "1 letra mayuscula, 1 numero y un simbolo ! @ # $ & *"
            validForm = false
        }
        return validForm
    }

    private fun registerUser(user: User, provider: ProviderType) {
        db.collection("users").document(user.email).set(
            mapOf("provider" to provider,
                "uid" to user.uid,
                "verified" to false,
                "name" to user.name,
                "password" to "",
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