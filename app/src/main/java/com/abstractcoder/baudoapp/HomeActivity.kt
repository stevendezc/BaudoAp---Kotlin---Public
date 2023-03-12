package com.abstractcoder.baudoapp

import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.LinearLayout
import androidx.fragment.app.Fragment
import com.abstractcoder.baudoapp.databinding.ActivityHomeBinding
import com.abstractcoder.baudoapp.fragments.*
import com.facebook.login.LoginManager
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

enum class ProviderType {
    BASIC,
    GOOGLE,
    FACEBOOK
}

class HomeActivity : AppCompatActivity() {
    lateinit var topMenu: LinearLayout
    private val db = FirebaseFirestore.getInstance()

    private lateinit var binding: ActivityHomeBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityHomeBinding.inflate(layoutInflater)
        setContentView(binding.root)
        topMenu = findViewById(R.id.topMenu)

        // Recover data with bundle
        val bundle = intent.extras
        val email: String = bundle?.getString("email").toString()
        val provider: String = bundle?.getString("provider").toString()
        getUser(email)
        // Setup incoming data
        setup(email, provider)

        // Data saving for sessions (session Data)
        val prefs = getSharedPreferences(getString(R.string.prefs_file), Context.MODE_PRIVATE).edit()
        prefs.putString("email", email)
        prefs.putString("provider", provider)
        prefs.apply()

        // Setup fragments
        fragmentSetup()

    }

    private fun setup(email: String, provider: String) {
        title = "Inicio"

        binding.userImageView.setOnClickListener {
            showUserActivity(email)
        }

        binding.logOutbutton.setOnClickListener {
            // saved prefs removal (session Data)
            val prefs = getSharedPreferences(getString(R.string.prefs_file), Context.MODE_PRIVATE).edit()
            prefs.clear()
            prefs.apply()

            if (provider == ProviderType.FACEBOOK.name) {
                LoginManager.getInstance().logOut()
            }
            FirebaseAuth.getInstance().signOut()
            //onBackPressed()
            showLogIn()
        }
    }

    private fun fragmentSetup() {
        val homeFragment = HomeFragment()
        val storeFragment = StoreFragment()
        val communityFragment = CommunityFragment()
        val eventsFragment = EventsFragment()
        val navegantesFragment = NavegantesFragment()
        makeCurrentFragment(homeFragment)

        // Fragment Navigation
        binding.bottomNavigationView.setOnNavigationItemSelectedListener {
            when (it.itemId) {
                R.id.ic_home -> makeCurrentFragment(homeFragment)
                R.id.ic_store -> makeCurrentFragment(storeFragment)
                R.id.ic_community -> makeCurrentFragment(communityFragment)
                R.id.ic_events -> makeCurrentFragment(eventsFragment)
                R.id.ic_navegantes -> makeCurrentFragment(navegantesFragment)
            }
            true
        }
    }

    private fun showLogIn() {
        val logInIntent = Intent(this, LogInActivity::class.java)
        startActivity(logInIntent)
    }

    private fun showUserActivity(email: String) {
        val userIntent = Intent(this, UserActivity::class.java).apply {
            putExtra("email", email)
            putExtra("name", binding.nameTextView.text.toString())
        }
        startActivity(userIntent)
    }

    private fun getUser(email: String) {
        db.collection("users").document(email).get().addOnSuccessListener {
            val userName = it.get("name").toString()
            binding.nameTextView.text = userName
        }
    }

    private fun makeCurrentFragment(fragment: Fragment) {
        supportFragmentManager.beginTransaction().apply {
            replace(R.id.homeFragmentWrapper, fragment)
            commit()
        }
    }
}