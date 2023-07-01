package com.abstractcoder.baudoapp

import android.Manifest
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.content.pm.PackageManager
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import com.abstractcoder.baudoapp.databinding.ActivityHomeBinding
import com.abstractcoder.baudoapp.fragments.*
import com.abstractcoder.baudoapp.utils.Connection
import com.abstractcoder.baudoapp.utils.Firestore
import com.bumptech.glide.Glide
import com.facebook.login.LoginManager
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

enum class ProviderType {
    BASIC,
    GOOGLE,
    FACEBOOK
}
data class BottomMenuOption(val icon: ImageView, val label: TextView)

class HomeActivity : AppCompatActivity() {
    lateinit var topMenu: LinearLayout
    val firestoreInst = Firestore()
    val networkConnection = Connection()

    private lateinit var userData: FirebaseUser
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

        checkPermissions()

        firestoreInst.activateSubscribers(this, email)

        // Setup fragments
        fragmentSetup()

    }

    override fun onBackPressed() {
        moveTaskToBack(true)
    }

    private fun checkPermissions() {
        val permission = Manifest.permission.POST_NOTIFICATIONS
        var notificationPermission = ContextCompat.checkSelfPermission(this, permission)
        if (notificationPermission == PackageManager.PERMISSION_DENIED) {
            println("Permiso no garantizado")
            //ActivityCompat.requestPermissions(this, arrayOf(permission), 1)
        } else {
            println("Permiso garantizado")
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == 1) {
            if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                // Permission granted, handle the action
                Toast.makeText(this, "Permisos garantizados", Toast.LENGTH_SHORT).show()
            } else {
                // Permission denied, handle accordingly (e.g., show a message or disable functionality)
                Toast.makeText(this, "Permisos no garantizados", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun setup(email: String, provider: String) {
        title = "Inicio"

        binding.userImage.setOnClickListener {
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
        switchBottomMenuColor(0)

        // Fragment Navigation
        binding.menuHome.setOnClickListener {
            makeCurrentFragment(homeFragment)
            switchBottomMenuColor(0)
        }
        binding.menuStore.setOnClickListener {
            makeCurrentFragment(storeFragment)
            switchBottomMenuColor(1)
        }
        binding.menuCommunity.setOnClickListener {
            makeCurrentFragment(communityFragment)
            switchBottomMenuColor(2)
        }
        binding.menuEvents.setOnClickListener {
            makeCurrentFragment(eventsFragment)
            switchBottomMenuColor(3)
        }
        binding.menuNavegantes.setOnClickListener {
            makeCurrentFragment(navegantesFragment)
            switchBottomMenuColor(4)
        }
    }

    private fun switchBottomMenuColor(pos: Int) {
        val selectedColor = ContextCompat.getColor(this, R.color.baudo_yellow)
        val unselectedColor = ContextCompat.getColor(this, R.color.white)
        val optionsItems = arrayOf(
            BottomMenuOption(binding.menuHomeIcon,binding.menuHomeLabel),
            BottomMenuOption(binding.menuStoreIcon,binding.menuStoreLabel),
            BottomMenuOption(binding.menuCommunityIcon,binding.menuCommunityLabel),
            BottomMenuOption(binding.menuEventsIcon,binding.menuEventsLabel),
            BottomMenuOption(binding.menuNavegantesIcon,binding.menuNavegantesLabel)
        )
        for (index in optionsItems.indices) {
            val option = optionsItems[index]
            println(pos)
            println(option)
            if (index == pos) {
                option.icon.setColorFilter(selectedColor)
                option.label.setTextColor(selectedColor)
            } else {
                option.icon.setColorFilter(unselectedColor)
                option.label.setTextColor(unselectedColor)
            }
        }
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
            userData = user
            val userName = userData.name
            println("currentUser: $userData")
            val prefs = getSharedPreferences(getString(R.string.prefs_file), Context.MODE_PRIVATE).edit()
            prefs.putString("name", userName)
            prefs.apply()

            if (userData.user_pic != "") {
                binding.userImage.setContentPadding(0,0,0,0)
                Glide.with(binding.userImage)
                    .load(userData.user_pic)
                    .into(binding.userImage)
            }
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