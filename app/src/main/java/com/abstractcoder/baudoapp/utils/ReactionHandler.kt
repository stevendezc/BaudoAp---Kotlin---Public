package com.abstractcoder.baudoapp.utils

import com.abstractcoder.baudoapp.FirebaseUser
import com.abstractcoder.baudoapp.PostData
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore

class ReactionHandler {

    fun addReaction(
        email: String,
        postId: String,
        reactionType: String,
        userData: FirebaseUser,
        postData: PostData,
        db: FirebaseFirestore) {
        val reactions = userData.reactions.filter { it == postId }
        if (reactions.size > 0) {
            val reaction = reactions[0]

            val updatedReactions = userData.reactions.filter { it != postId }
            if (updatedReactions.size == 0) {
                removeSameReaction(email, postId, userData, postData, db)
            }
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
        db.collection("users").document(email!!).update(
            "reactions", updatedReactions)
        decreaseReactionCounter(postId, postData, db)
    }

}