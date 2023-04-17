package com.abstractcoder.baudoapp

data class User(val name: String, val email: String, val password: String)

data class GoogleUser(val name: String, val email: String)

data class FirebaseUser(
    val name: String = "",
    val password: String = "",
    val provider: String = "",
    val saved_posts: ArrayList<String> = arrayListOf<String>(),
    val reactions: ArrayList<Reaction> = arrayListOf<Reaction>(),
    val user_pic: String = "",
    val verified: Boolean = false
    )

data class UserMetrics(
    val totalReactions: Int = 0,
    val totalPositiveReactions: Int = 0,
    val totalSavedPosts: Int = 0,
    val likedAmbientalPosts: Int = 0,
    val likedMemoryPosts: Int = 0,
    val likedGenderPosts: Int = 0
)