package com.abstractcoder.baudoapp.utils

import android.content.ContentValues
import android.util.Log
import com.abstractcoder.baudoapp.Commentary
import com.abstractcoder.baudoapp.PostData
import com.abstractcoder.baudoapp.Reaction
import com.google.firebase.Timestamp
import com.google.firebase.firestore.FirebaseFirestore

interface MyCallback {
    fun onSuccess(result: ArrayList<PostData>)
}

class Firestore {

    private val db = FirebaseFirestore.getInstance()

    private var postsMainList: ArrayList<PostData> = arrayListOf<PostData>()

    fun retrieveDocuments(callback: MyCallback) {
        // Recover DB documents
        db.collection("posts").get().addOnSuccessListener { posts ->
            for (post in posts) {
                var data = post.data
                Log.d(ContentValues.TAG, "post id: ${post.id}")
                var postData = PostData(
                    post.id,
                    data["author"] as String,
                    data["category"] as String,
                    data["commentaries"] as List<Commentary>,
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
}