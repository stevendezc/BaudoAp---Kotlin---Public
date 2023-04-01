package com.abstractcoder.baudoapp

data class User(val name: String, val email: String, val password: String)

data class GoogleUser(val name: String, val email: String)

data class FirebaseUser(
    val name: String = "",
    val password: String = "",
    val provider: String = "",
    val saved_posts: ArrayList<String> = arrayListOf<String>(),
    val liked_posts: ArrayList<String> = arrayListOf<String>(),
    val disliked_posts: ArrayList<String> = arrayListOf<String>(),
    val indifferent_posts: ArrayList<String> = arrayListOf<String>(),
    val user_pic: String = "",
    val verified: Boolean = false
    )