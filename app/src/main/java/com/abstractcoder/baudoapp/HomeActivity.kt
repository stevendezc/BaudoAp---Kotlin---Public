package com.abstractcoder.baudoapp

import android.content.ContentValues.TAG
import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import kotlinx.android.synthetic.main.activity_home.*

enum class ProviderType {
    BASIC,
    GOOGLE
}

class HomeActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_home)

        // Recover data with bundle
        val bundle = intent.extras
        val email: String = bundle?.getString("email").toString()
        val provider: String = bundle?.getString("provider").toString()
        getUser(email)
        // Setup incoming data
        setup()

        // Data saving for sessions (session Data)
        val prefs = getSharedPreferences(getString(R.string.prefs_file), Context.MODE_PRIVATE).edit()
        prefs.putString("email", email)
        prefs.putString("provider", provider)
        prefs.apply()

    }

    private fun setup() {
        title = "Inicio"

        logOutbutton.setOnClickListener {
            // saved prefs removal (session Data)
            val prefs = getSharedPreferences(getString(R.string.prefs_file), Context.MODE_PRIVATE).edit()
            prefs.clear()
            prefs.apply()

            FirebaseAuth.getInstance().signOut()
            //onBackPressed()
            showLogIn()
        }
    }

    private fun showLogIn() {
        val logInIntent = Intent(this, LogInActivity::class.java)
        startActivity(logInIntent)
    }

    private fun retrieveUserFromProvider(incomingEmail: String, users: DatabaseReference) {
        val postListener = object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                // Get Post object and use the values to update the UI
                val sb = StringBuilder()
                for (i in dataSnapshot.children) {
                    val userEmail: String = i.child("email").value.toString()
                    val userName: String = i.child("name").value.toString()
                    Log.e(TAG, "userEmail: $userEmail")
                    Log.e(TAG, "incoming : $incomingEmail")
                    if (userEmail == incomingEmail) {
                        Log.e(TAG, "USUARIO: $userName")
                        sb.append(userName)
                    }
                }
                nameTextView.text = sb
            }

            override fun onCancelled(databaseError: DatabaseError) {
                // Getting Post failed, log a message
                Log.w(TAG, "loadPost:onCancelled", databaseError.toException())
            }
        }
        users.addValueEventListener(postListener)
        users.addListenerForSingleValueEvent(postListener)
    }

    private fun getUser(email: String) {
        val database = Firebase.database
        val myRef = database.getReference("DB")
        val bundle = intent.extras

        when (bundle?.getString("provider").toString()) {
            ProviderType.BASIC.toString() -> retrieveUserFromProvider(email, myRef.child("Users"))
            ProviderType.GOOGLE.toString() -> retrieveUserFromProvider(email, myRef.child("GoogleUsers"))
        }
    }
}