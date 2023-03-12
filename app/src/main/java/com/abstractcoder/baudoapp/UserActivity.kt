package com.abstractcoder.baudoapp

import android.app.Dialog
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.abstractcoder.baudoapp.databinding.ActivityUserBinding
import com.abstractcoder.baudoapp.fragments.*
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.firebase.firestore.FirebaseFirestore

class UserActivity : AppCompatActivity() {
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
        binding.settingsButton.setOnClickListener {
            mDialog.setContentView(R.layout.fragment_settings_pop_up)
        }
    }

    private fun setup(email: String, name: String) {
        title = "Pagina de Usuario"

        binding.userNameTextView.text = name
    }
}