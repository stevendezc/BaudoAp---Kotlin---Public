package com.abstractcoder.baudoapp

import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.FrameLayout
import android.widget.LinearLayout
import androidx.fragment.app.Fragment
import com.abstractcoder.baudoapp.databinding.ActivityConfigBinding
import com.abstractcoder.baudoapp.fragments.ContactFragment
import com.abstractcoder.baudoapp.fragments.FaqFragment
import com.abstractcoder.baudoapp.fragments.PrivacyFragment
import com.facebook.login.LoginManager
import com.google.firebase.auth.FirebaseAuth

interface FragmentButtonClickListener {
    fun onButtonClicked()
}

class ConfigActivity : AppCompatActivity(), FragmentButtonClickListener {

    private lateinit var binding: ActivityConfigBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityConfigBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setup()

        // Disable the device back button
        supportActionBar?.setDisplayHomeAsUpEnabled(false)
    }

    fun setup() {
        binding.backButton.setOnClickListener { finish() }
        binding.contextButton.setOnClickListener { showFixedVideo() }
        binding.contactButton.setOnClickListener { makeCurrentFragment(ContactFragment()) }
        binding.faqButton.setOnClickListener { makeCurrentFragment(FaqFragment()) }
        binding.privacyButton.setOnClickListener { makeCurrentFragment(PrivacyFragment()) }
        binding.disconnectButton.setOnClickListener {
            val prefs = getSharedPreferences(getString(R.string.prefs_file), Context.MODE_PRIVATE).edit()
            prefs.clear()
            prefs.apply()

            /*if (provider == ProviderType.FACEBOOK.name) {
                LoginManager.getInstance().logOut()
            }*/
            FirebaseAuth.getInstance().signOut()
            showLogIn()
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

    override fun onButtonClicked() {
        // Perform the desired action when the button is clicked in the fragment
        binding.settingsFragmentWrapper.visibility = FrameLayout.GONE
        binding.configMenu.visibility = LinearLayout.VISIBLE
    }

    private fun makeCurrentFragment(fragment: Fragment) {
        supportFragmentManager.beginTransaction().apply {
            replace(R.id.settingsFragmentWrapper, fragment)
            commit()
        }
        binding.settingsFragmentWrapper.visibility = FrameLayout.VISIBLE
        binding.configMenu.visibility = LinearLayout.GONE
    }

    override fun onBackPressed() {
        // Do nothing
    }
}