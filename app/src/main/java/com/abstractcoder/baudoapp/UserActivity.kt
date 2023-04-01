package com.abstractcoder.baudoapp

import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import com.abstractcoder.baudoapp.databinding.ActivityUserBinding
import com.abstractcoder.baudoapp.fragments.*
import com.abstractcoder.baudoapp.utils.InfoDialog
import com.abstractcoder.baudoapp.utils.SettingsDialog
import com.facebook.login.LoginManager
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

class UserActivity : FragmentActivity() {
    private lateinit var bottomNav: BottomNavigationView
    private val db = FirebaseFirestore.getInstance()

    private lateinit var binding: ActivityUserBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityUserBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Recover data with bundle
        val bundle = intent.extras
        val email: String = bundle?.getString("email").toString()
        val provider: String = bundle?.getString("provider").toString()
        val name: String = bundle?.getString("name").toString()
        // Setup incoming data
        setup(email, name, provider)
    }

    private fun setup(email: String, name: String, provider: String) {
        title = "Pagina de Usuario"

        binding.userNameTextView.text = name

        binding.settingsButton.setOnClickListener {
            SettingsDialog(
                onSubmitClickListener = {
                    // saved prefs removal (session Data)
                    val prefs = getSharedPreferences(getString(R.string.prefs_file), Context.MODE_PRIVATE).edit()
                    prefs.clear()
                    prefs.apply()

                    if (provider == ProviderType.FACEBOOK.name) {
                        LoginManager.getInstance().logOut()
                    }
                    FirebaseAuth.getInstance().signOut()
                    showLogIn()
                },
                onBaudoVideoLinkListener = {
                    showFixedVideo()
                }
            ).show(supportFragmentManager, "settings dialog")
        }

        binding.infoButton.setOnClickListener {
            InfoDialog("perfil").show(supportFragmentManager, "info dialog")
        }
    }

    private fun showLogIn() {
        val logInIntent = Intent(this, LogInActivity::class.java)
        startActivity(logInIntent)
    }

    private fun showFixedVideo() {
        val fixedVideoIntent = Intent(this, FixedVideoActivity::class.java)
        startActivity(fixedVideoIntent)
    }
}