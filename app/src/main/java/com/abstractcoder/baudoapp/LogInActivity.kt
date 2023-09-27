package com.pereira.baudoapp

import android.content.Context
import android.content.Intent
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.method.HideReturnsTransformationMethod
import android.text.method.PasswordTransformationMethod
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import com.pereira.baudoapp.databinding.ActivityLogInBinding
import com.facebook.CallbackManager
import com.facebook.FacebookCallback
import com.facebook.FacebookException
import com.facebook.login.LoginManager
import com.facebook.login.LoginResult
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException
import com.google.firebase.analytics.FirebaseAnalytics
import com.google.firebase.auth.FacebookAuthProvider
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.GoogleAuthProvider
import com.google.firebase.database.ktx.database
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.ktx.Firebase

class LogInActivity : AppCompatActivity() {
    private val GOOGLE_SIGN_IN = 100
    private val callbackManager = CallbackManager.Factory.create()
    private val db = FirebaseFirestore.getInstance()
    private lateinit var binding: ActivityLogInBinding

    var visiblePassword = false
    val userCollectionRef = db.collection("users")

    override fun onCreate(savedInstanceState: Bundle?) {
        Thread.sleep(2000)
        setTheme(R.style.Theme_BaudoApp)
        super.onCreate(savedInstanceState)
        binding = ActivityLogInBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Analytics event
        val analytics: FirebaseAnalytics = FirebaseAnalytics.getInstance(this)
        val bundle = Bundle()
        bundle.putString("message", "Integracion de Firebase Completa")
        analytics.logEvent("LogInScreen", bundle)

        // Setup
        setup()
        session()
    }

    override fun onStart() {
        super.onStart()
        binding.authLayout.visibility = View.VISIBLE
    }

    // Session data retrieval
    private fun session() {
        val prefs = getSharedPreferences(getString(R.string.prefs_file), Context.MODE_PRIVATE)
        val email = prefs.getString("email", null)
        val provider = prefs.getString("provider", null)

        if (email != null && provider != null) {
            binding.authLayout.visibility = View.INVISIBLE
            showHome(email, ProviderType.valueOf(provider))
        }
    }

    private fun setup() {
        title = "Autenticacion"
        val authInstance = FirebaseAuth.getInstance()

        /*binding.googleButton.setOnClickListener {
            // Config
            val googleConf = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                .build()
            // Auth Client
            val googleClient = GoogleSignIn.getClient(this, googleConf)
            googleClient.signOut()
            startActivityForResult(googleClient.signInIntent, GOOGLE_SIGN_IN)
        }*/

        binding.showHideButton.setOnClickListener {
            visiblePassword = !visiblePassword
            if (visiblePassword) {
                binding.showHideButton.setImageResource(R.drawable.no_eye)
                binding.passwordEditText.transformationMethod = HideReturnsTransformationMethod.getInstance()
            } else {
                binding.showHideButton.setImageResource(R.drawable.eye)
                binding.passwordEditText.transformationMethod = PasswordTransformationMethod.getInstance()
            }
        }

        binding.logInButton.setOnClickListener {
            if (binding.emailEditText.text.isNotEmpty() && binding.passwordEditText.text.isNotEmpty()) {
                authInstance.signInWithEmailAndPassword(binding.emailEditText.text.toString(),
                    binding.passwordEditText.text.toString()).addOnCompleteListener { authResult ->
                    if (authResult.isSuccessful) {
                        val user = authInstance.currentUser
                        if (user != null) {
                            Log.d("Email Verified", user.isEmailVerified.toString())
                            if (!user.isEmailVerified) {
                                Toast.makeText(this, "Correo electronico no verificado", Toast.LENGTH_SHORT).show()
                            } else {
                                val email = authResult.result?.user?.email ?: ""
                                db.collection("users").document(binding.emailEditText.text.toString()).get().addOnSuccessListener { user ->
                                    val savedVerifiedState = user.get("verified").toString().toBoolean()
                                    Log.d("savedVerified", savedVerifiedState.toString())
                                    if (!savedVerifiedState) {
                                        db.collection("users").document(binding.emailEditText.text.toString()).update(
                                            mapOf("verified" to true)
                                        )
                                        showHome(email, ProviderType.BASIC)
                                    } else {
                                        showHome(email, ProviderType.BASIC)
                                    }
                                }
                            }
                        }
                    } else {
                        showAlert()
                    }
                }
            }
        }

        binding.registerButton.setOnClickListener {
            showRegister()
        }

        binding.forgotPassword.setOnClickListener {
            showPasswordRecovery()
        }
    }

    private fun registerUser(id: String, user: GoogleUser) {

        userCollectionRef.document(user.email).get().addOnSuccessListener {
            if (!it.exists()) {
                userCollectionRef.document(user.email).set(
                    mapOf("provider" to ProviderType.GOOGLE,
                        "uid" to id,
                        "verified" to true,
                        "name" to user.name,
                        "password" to "",
                        "user_pic" to user.user_pic.toString(),
                        "saved_posts" to emptyList<String>(),
                        "liked_posts" to emptyList<String>(),
                        "disliked_posts" to emptyList<String>(),
                        "indifferent_posts" to emptyList<String>()
                    )
                )
            }
        }
    }

    private fun showAlert() {
        val builder = AlertDialog.Builder(this)
        builder.setTitle("Error")
        builder.setMessage("Se ha producido un error autenticando al usuario")
        val dialog: AlertDialog = builder.create()
        dialog.show()
    }

    private fun showHome(email: String, provider: ProviderType) {
        val homeIntent = Intent(this, HomeActivity::class.java).apply {
            putExtra("email", email)
            putExtra("provider", provider.name)
        }
        startActivity(homeIntent)
    }

    private fun showRegister() {
        val registerIntent = Intent(this, SignUpActivity::class.java)
        startActivity(registerIntent)
    }

    private fun showPasswordRecovery() {
        val registerIntent = Intent(this, PasswordRecoveryActivity::class.java)
        startActivity(registerIntent)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        callbackManager.onActivityResult(requestCode, resultCode, data)
        super.onActivityResult(requestCode, resultCode, data)

        // Get Google credential from task
        if (requestCode == GOOGLE_SIGN_IN) {
            val task = GoogleSignIn.getSignedInAccountFromIntent(data)

            try {
                val account = task.getResult(ApiException::class.java)

                if (account != null) {
                    val credential = GoogleAuthProvider.getCredential(account.idToken, null)
                    FirebaseAuth.getInstance().signInWithCredential(credential).addOnCompleteListener {
                        if (it.isSuccessful) {
                            val id: String = it.result?.user?.uid ?: ""
                            val name: String = it.result?.user?.displayName ?: ""
                            val email: String = it.result?.user?.email ?: ""
                            val userImage: Uri = it.result?.user?.photoUrl!!
                            val user = GoogleUser(name, email, userImage)
                            registerUser(id, user)
                            showHome(account.email ?: "", ProviderType.GOOGLE)
                        } else {
                            showAlert()
                        }
                    }
                }
            } catch (e: ApiException) {
                showAlert()
            }
        }
    }
}