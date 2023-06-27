package com.abstractcoder.baudoapp

data class User(val name: String, val email: String, val password: String)

data class GoogleUser(val name: String, val email: String)

data class FirebaseUser(
    val name: String = "",
    val password: String = "",
    val provider: String = "",
    val commentaries: ArrayList<String> = arrayListOf<String>(),
    val saved_posts: ArrayList<String> = arrayListOf<String>(),
    val liked_posts: ArrayList<String> = arrayListOf<String>(),
    val indifferent_posts: ArrayList<String> = arrayListOf<String>(),
    val disliked_posts: ArrayList<String> = arrayListOf<String>(),
    val reactions: ArrayList<Reaction> = arrayListOf<Reaction>(),
    val active_podcasts: ArrayList<PodcastInfo> = arrayListOf<PodcastInfo>(),
    val user_pic: String = "",
    val verified: Boolean = false
    )

data class UserMetrics(
    val totalCommentaries: Int = 0,
    val totalReactions: Int = 0,
    val totalPositiveReactions: Int = 0,
    val totalSavedPosts: Int = 0,
    val likedAmbientalPosts: Int = 0,
    val likedMemoryPosts: Int = 0,
    val likedGenderPosts: Int = 0
)