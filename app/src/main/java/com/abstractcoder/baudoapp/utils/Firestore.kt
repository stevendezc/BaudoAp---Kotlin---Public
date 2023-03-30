package com.abstractcoder.baudoapp.utils

import android.content.ContentValues
import android.util.Log
import com.abstractcoder.baudoapp.Commentary
import com.abstractcoder.baudoapp.CommunityData
import com.abstractcoder.baudoapp.PostData
import com.abstractcoder.baudoapp.Reaction
import com.google.firebase.Timestamp
import com.google.firebase.firestore.FirebaseFirestore

interface PostsCallback {
    fun onSuccess(result: ArrayList<PostData>)
}

interface CommunitiesCallback {
    fun onSuccess(result: ArrayList<CommunityData>)
}

interface CommentsCallback {
    fun onSuccess(result: ArrayList<Commentary>)
}

class Firestore {

    private val db = FirebaseFirestore.getInstance()

    private var postsMainList: ArrayList<PostData> = arrayListOf<PostData>()
    private var communityMainList: ArrayList<CommunityData> = arrayListOf<CommunityData>()

    fun retrieveDocuments(callback: PostsCallback) {
        // Recover DB documents
        db.collection("posts").get().addOnSuccessListener { posts ->
            for (post in posts) {
                var data = post.data
                Log.d(ContentValues.TAG, "community id: ${post.id}")
                var postData = PostData(
                    post.id,
                    data["author"] as String,
                    data["category"] as String,
                    data["commentaries"] as List<String>,
                    data["description"] as String,
                    data["main_media"] as String,
                    data["reactions"] as List<Reaction>,
                    data["thumbnail"] as String,
                    data["timestamp"] as Timestamp,
                    data["title"] as String,
                    data["type"] as String,
                )
                postsMainList.add(postData)
            }
            callback.onSuccess(postsMainList)
        }.addOnFailureListener { exception ->
            Log.w(ContentValues.TAG, "Error cargando posts.", exception)
        }
    }

    fun retrieveCommunities(callback: CommunitiesCallback) {
        // Recover DB documents
        db.collection("communities").get().addOnSuccessListener { communities ->
            for (community in communities) {
                var data = community.data
                Log.d(ContentValues.TAG, "community id: ${community.id}")
                var communityData = CommunityData(
                    community.id,
                    data["category"] as String,
                    data["description"] as String,
                    data["facebook"] as String,
                    data["instagram"] as String,
                    data["lastname"] as String,
                    data["name"] as String,
                    data["thumbnail"] as String,
                    data["twitter"] as String,
                    data["whatsapp"] as String
                )
                communityMainList.add(communityData)
            }
            callback.onSuccess(communityMainList)
        }.addOnFailureListener { exception ->
            Log.w(ContentValues.TAG, "Error cargando posts.", exception)
        }
    }

    fun retrievePostComments(callback: CommentsCallback, postId: String) {
        // Recover DB documents
        db.collection("commentaries")
            .whereEqualTo("post", postId)
            .get().addOnSuccessListener { querySnapshot ->
                if (!querySnapshot.isEmpty) {
                    val documentSnapshot = querySnapshot.documents
                    val commentariesList = arrayListOf<Commentary>()
                    for (commentary in documentSnapshot) {
                        val commentaryId = commentary.id
                        val commentaryData = commentary.data
                        val incomingCommentary = Commentary(
                            id = commentaryId,
                            post = commentaryData?.get("post").toString(),
                            author = commentaryData?.get("author").toString(),
                            text = commentaryData?.get("text").toString(),
                            timestamp = commentaryData?.get("timestamp") as Timestamp,
                            replies = listOf<Commentary>()
                        )
                        commentariesList.add(incomingCommentary)
                    }
                    callback.onSuccess(commentariesList)
                } else {
                    Log.d("Firestore", "No matching Commentary(ies) found")
                }
        }.addOnFailureListener { exception ->
            Log.w(ContentValues.TAG, "Error cargando comentarios.", exception)
        }
    }
}