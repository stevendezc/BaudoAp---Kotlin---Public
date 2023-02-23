package com.abstractcoder.baudoapp

import com.google.firebase.Timestamp

data class Commentary(
    val id: String,
    val author: String,
    val text: String,
    val timestamp: Timestamp,
    val replies: List<Commentary>
)

data class Reaction(
    val id: String,
    val author: String,
    val timestamp: Timestamp,
    val type: String
)

data class PostData(
    val id: String,
    val author: String,
    val category: String,
    val commentaries: List<Commentary>,
    val description: String,
    val main_media: String,
    val reactions: List<Reaction>,
    val thumbnail: String,
    val timestamp: Timestamp,
    val title: String,
    val type: String
)

data class Post(val id: String, val data: PostData)
