package com.pereira.baudoapp.utils

import com.pereira.baudoapp.FirebaseUser
import com.pereira.baudoapp.PostData
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore

class ReactionHandler {

    fun addReaction(
        email: String,
        postId: String,
        userData: FirebaseUser,
        postData: PostData,
        db: FirebaseFirestore) {
        println("postId: $postId")
        println("userData: $userData")
        println("postData: $postData")
        val reactions = userData.reactions.filter { it == postId }
        println("reactions.size: ${reactions.size}")
        if (reactions.isNotEmpty()) {
            val updatedReactions = userData.reactions.filter { it != postId }
            println("updatedReactions: $updatedReactions")
            removeSameReaction(email, postId, userData, postData, db)
            /*if (updatedReactions.isEmpty()) {
                removeSameReaction(email, postId, userData, postData, db)
            }*/
        } else {
            // Add reaction
            db.collection("users").document(email!!).update(
                "reactions", FieldValue.arrayUnion(postId)
            )
            increaseReactionCounter(postId, postData, db)
        }
    }

    fun increaseReactionCounter(
        postId: String,
        postData: PostData,
        db: FirebaseFirestore) {
        val currentNumericValue = postData.likes ?: 0
        val newReactionValue = currentNumericValue + 1
        db.collection("posts").document(postId).update(
            "likes", newReactionValue
        )
    }

    fun decreaseReactionCounter(
        postId: String,
        postData: PostData,
        db: FirebaseFirestore) {
        val currentNumericValue = postData.likes
        println("Current likes: $currentNumericValue")
        val newReactionValue = currentNumericValue!! - 1
        db.collection("posts").document(postId).update(
            "likes", newReactionValue
        )
    }

    fun removeSameReaction(
        email: String,
        postId: String,
        userData: FirebaseUser,
        postData: PostData,
        db:FirebaseFirestore) {
        val updatedReactions = userData.reactions.filter { it != postId }
        println("updatedReactions: $updatedReactions")
        println("Email of document to remove reactions: $email")
        db.collection("users").document(email!!).update(
            "reactions", updatedReactions)
        decreaseReactionCounter(postId, postData, db)
    }

}