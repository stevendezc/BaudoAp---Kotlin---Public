package com.abstractcoder.baudoapp.utils

import com.abstractcoder.baudoapp.FirebaseUser
import com.abstractcoder.baudoapp.PostData
import com.abstractcoder.baudoapp.Reaction
import com.google.firebase.Timestamp
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
        val reactions = userData.reactions.filter { it.post == postId }
        if (reactions.size > 0) {
            val reaction = reactions[0]

            if (reactionType == reaction.type) {
                removeSameReaction(email, postId, userData, postData, db)
            } else {
                switchReaction(email, postId, userData, postData, db)
            }
        } else {
            val newReaction = Reaction(
                postId, Timestamp.now(), reactionType
            )
            // Add reaction
            db.collection("users").document(email!!).update(
                "reactions", FieldValue.arrayUnion(newReaction)
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

    fun switchReaction(
        email: String,
        postId: String,
        userData: FirebaseUser,
        postData: PostData,
        db: FirebaseFirestore) {
        // Switch reaction type on current Reaction object
        var currentReaction = userData.reactions.find { it.post == postId }
        var incomingReaction = Reaction(
            currentReaction?.post,
            currentReaction?.timestamp,
            "likes"
        )
        val updatedReactions = userData.reactions.filter { it.post != postId }.toCollection(ArrayList())
        updatedReactions.add(incomingReaction!!)
        db.collection("users").document(email!!).update(
            "reactions", updatedReactions)

        decreaseReactionCounter(postId, postData, db)

        increaseReactionCounter(postId, postData, db)
    }

    fun removeSameReaction(
        email: String,
        postId: String,
        userData: FirebaseUser,
        postData: PostData,
        db:FirebaseFirestore) {
        val updatedReactions = userData.reactions.filter { it.post != postId }
        db.collection("users").document(email!!).update(
            "reactions", updatedReactions)
        decreaseReactionCounter(postId, postData, db)
    }

}