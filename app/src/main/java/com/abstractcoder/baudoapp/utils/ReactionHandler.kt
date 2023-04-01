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
                removeSameReaction(email, reactionType, postId, userData, postData, db)
            } else {
                switchReaction(email, reactionType, postId, userData, postData, db)
            }
        } else {
            val newReaction = Reaction(
                postId, Timestamp.now(), reactionType
            )
            // Add reaction
            db.collection("users").document(email!!).update(
                "reactions", FieldValue.arrayUnion(newReaction)
            )
            increaseReactionCounter(reactionType, postId, postData, db)
        }
    }

    fun increaseReactionCounter(
        reactionType: String,
        postId: String,
        postData: PostData,
        db: FirebaseFirestore) {
        var newReactionValue = 0
        when (reactionType) {
            "likes" -> {
                val currentNumericValue = postData.likes ?: 0
                newReactionValue = currentNumericValue + 1
            }
            "indifferents" -> {
                val currentNumericValue = postData.indifferents ?: 0
                newReactionValue = currentNumericValue + 1
            }
            "dislikes" -> {
                val currentNumericValue = postData.dislikes ?: 0
                newReactionValue = currentNumericValue + 1
            }
        }
        db.collection("posts").document(postId).update(
            reactionType, newReactionValue
        )
    }

    fun decreaseReactionCounter(
        reactionType: String,
        postId: String,
        postData: PostData,
        db: FirebaseFirestore) {
        var newReactionValue = 0
        when (reactionType) {
            "likes" -> {
                val currentNumericValue = postData.likes
                println("Current likes: $currentNumericValue")
                newReactionValue = currentNumericValue!! - 1
            }
            "indifferents" -> {
                val currentNumericValue = postData.indifferents ?: 0
                newReactionValue = currentNumericValue - 1
            }
            "dislikes" -> {
                val currentNumericValue = postData.dislikes ?: 0
                newReactionValue = currentNumericValue - 1
            }
        }
        db.collection("posts").document(postId).update(
            reactionType, newReactionValue
        )
    }

    fun switchReaction(
        email: String,
        incomingReactionType: String,
        postId: String,
        userData: FirebaseUser,
        postData: PostData,
        db: FirebaseFirestore) {
        // Switch reaction type on current Reaction object
        var currentReaction = userData.reactions.find { it.post == postId }
        val currentReactionType = currentReaction?.type
        var incomingReaction = Reaction(
            currentReaction?.post,
            currentReaction?.timestamp,
            incomingReactionType
        )
        val updatedReactions = userData.reactions.filter { it.post != postId }.toCollection(ArrayList())
        updatedReactions.add(incomingReaction!!)
        db.collection("users").document(email!!).update(
            "reactions", updatedReactions)

        decreaseReactionCounter(currentReactionType!!, postId, postData, db)

        increaseReactionCounter(incomingReactionType, postId, postData, db)
    }

    fun removeSameReaction(
        email: String,
        reactionType: String,
        postId: String,
        userData: FirebaseUser,
        postData: PostData,
        db:FirebaseFirestore) {
        val updatedReactions = userData.reactions.filter { it.post != postId }
        db.collection("users").document(email!!).update(
            "reactions", updatedReactions)
        decreaseReactionCounter(reactionType, postId, postData, db)
    }

}