package com.abstractcoder.baudoapp

import android.app.Dialog
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.abstractcoder.baudoapp.fragments.*
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.android.synthetic.main.activity_user.*

class UserActivity : AppCompatActivity() {
    private lateinit var bottomNav: BottomNavigationView
    private val db = FirebaseFirestore.getInstance()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_user)

        // Recover data with bundle
        val bundle = intent.extras
        val email: String = bundle?.getString("email").toString()
        val name: String = bundle?.getString("name").toString()
        // Setup incoming data
        setup(email, name)

        bottomNav = findViewById(R.id.bottomNavigationView)
        bottomNav.setOnNavigationItemSelectedListener { item ->
            val fragment = when (item.itemId) {
                R.id.ic_home -> HomeFragment()
                R.id.ic_store -> StoreFragment()
                R.id.ic_community -> CommunityFragment()
                R.id.ic_events -> EventsFragment()
                R.id.ic_navegantes -> NavegantesFragment()
                else -> null
            }
            return@setOnNavigationItemSelectedListener false
        }

        val mDialog = Dialog(this)
        settingsButton.setOnClickListener {
            mDialog.setContentView(R.layout.fragment_settings_pop_up)
        }
    }

    private fun setup(email: String, name: String) {
        title = "Pagina de Usuario"

        userNameTextView.text = name
    }
}