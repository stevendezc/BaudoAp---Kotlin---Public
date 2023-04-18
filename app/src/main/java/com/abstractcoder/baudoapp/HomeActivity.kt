package com.abstractcoder.baudoapp

import android.content.Context
import android.content.Intent
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.LinearLayout
import androidx.annotation.RequiresApi
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import com.abstractcoder.baudoapp.databinding.ActivityHomeBinding
import com.abstractcoder.baudoapp.fragments.*
import com.abstractcoder.baudoapp.utils.Connection
import com.abstractcoder.baudoapp.utils.Firestore
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
    val firestoreInst = Firestore()
    val networkConnection = Connection()

    private lateinit var binding: ActivityHomeBinding

    @RequiresApi(Build.VERSION_CODES.M)
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
        val connection = networkConnection.isOnline(this.applicationContext)
        println("connection: $connection")
        val prefs = getSharedPreferences(getString(R.string.prefs_file), Context.MODE_PRIVATE).edit()
        prefs.putString("email", email)
        prefs.putString("provider", provider)
        prefs.putBoolean("online", connection)
        prefs.apply()

        firestoreInst.activateSubscribers(this, email)

        // Setup fragments
        fragmentSetup()

    }

    override fun onBackPressed() {
        moveTaskToBack(true)
    }

    private fun setup(email: String, provider: String) {
        title = "Inicio"

        binding.userImageView.setOnClickListener {
            showUserActivity(email, provider)
        }

        binding.fixedVideo.setOnClickListener {
            showFixedVideo()
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

    private fun showFixedVideo() {
        val fixedVideoIntent = Intent(this, FixedVideoActivity::class.java)
        startActivity(fixedVideoIntent)
    }

    private fun showUserActivity(email: String, provider: String) {
        val userIntent = Intent(this, UserActivity::class.java).apply {
            putExtra("email", email)
            putExtra("provider", provider)
            putExtra("name", binding.nameTextView.text.toString())
        }
        startActivity(userIntent)
        overridePendingTransition(com.facebook.R.anim.abc_slide_in_top, com.facebook.R.anim.abc_slide_out_bottom)
    }

    private fun getUser(email: String) {
        firestoreInst.userLiveData.observe(this, Observer { user ->
            // Update your UI with the new data
            val userName = user.name
            println("currentUser: $user")
            val prefs = getSharedPreferences(getString(R.string.prefs_file), Context.MODE_PRIVATE).edit()
            prefs.putString("name", userName)
            prefs.apply()
            binding.nameTextView.text = userName
        })
    }

    private fun makeCurrentFragment(fragment: Fragment) {
        supportFragmentManager.beginTransaction().apply {
            replace(R.id.homeFragmentWrapper, fragment)
            commit()
        }
    }
}