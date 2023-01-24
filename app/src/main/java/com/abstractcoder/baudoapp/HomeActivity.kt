package com.abstractcoder.baudoapp

import android.content.ContentValues.TAG
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import kotlinx.android.synthetic.main.activity_home.*

enum class ProviderType {
    BASIC
}

class HomeActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_home)

        // Recover data with bundle
        val bundle = intent.extras
        val email: String = bundle?.getString("email").toString()
        getUser(email)
        // Setup incoming data
        setup()
    }

    private fun setup() {
        title = "Inicio"

        signOutbutton.setOnClickListener {
            FirebaseAuth.getInstance().signOut()
            //onBackPressed()
            showLogIn()
        }
    }

    private fun showLogIn() {
        val logInIntent = Intent(this, LogInActivity::class.java)
        startActivity(logInIntent)
    }

    private fun getUser(email: String) {
        val database = Firebase.database
        val myRef = database.getReference("DB")
        val users = myRef.child("Users")

        val postListener = object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                // Get Post object and use the values to update the UI
                val sb = StringBuilder()
                for (i in dataSnapshot.children) {
                    val userEmail: String = i.child("email").value.toString()
                    val userName: String = i.child("name").value.toString()
                    Log.e(TAG, "userEmail: $userEmail")
                    Log.e(TAG, "incoming : $email")
                    if (userEmail == email) {
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
}