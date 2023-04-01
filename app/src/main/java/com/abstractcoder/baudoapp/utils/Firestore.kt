package com.abstractcoder.baudoapp.utils

import android.content.Context
import android.widget.Toast
import androidx.lifecycle.MutableLiveData
import com.abstractcoder.baudoapp.*
import com.google.firebase.firestore.FirebaseFirestore

class Firestore {

    private val db = FirebaseFirestore.getInstance()

    val userLiveData = MutableLiveData<FirebaseUser>()
    val postsLiveData = MutableLiveData<List<PostData>>()
    val communitiesLiveData = MutableLiveData<List<CommunityData>>()
    val postCommentsLiveData = MutableLiveData<List<Commentary>>()

    val userCollectionRef = db.collection("users")
    val postsCollectionRef = db.collection("posts")
    val communitiesCollectionRef = db.collection("communities")
    val commentariesCollectionRef = db.collection("commentaries")

    fun activateSubscribers(context: Context, email: String) {
        subscribeToUserUpdates(context, email)
        subscribeToPostUpdates(context)
        subscribeToCommunityUpdates(context)
    }

    private fun subscribeToUserUpdates(context: Context, email: String) {
        userCollectionRef.document(email).addSnapshotListener { value, error ->
            error?.let {
                Toast.makeText(context, it.message, Toast.LENGTH_LONG).show()
                return@addSnapshotListener
            }
            value?.let {
                val myData = it.toObject(FirebaseUser::class.java) ?: FirebaseUser()
                userLiveData.value = myData
            }
        }
    }

    private fun subscribeToPostUpdates(context: Context) {
        postsCollectionRef.addSnapshotListener { value, error ->
            error?.let {
                Toast.makeText(context, it.message, Toast.LENGTH_LONG).show()
                return@addSnapshotListener
            }
            value?.let {
                val postDataList = mutableListOf<PostData>()
                for (document in it) {
                    val postData = document.toObject(PostData::class.java)
                    postData.id = document.id
                    postDataList.add(postData)
                }
                postsLiveData.value = postDataList
            }
        }
    }

    private fun subscribeToCommunityUpdates(context: Context) {
        communitiesCollectionRef.addSnapshotListener { value, error ->
            error?.let {
                Toast.makeText(context, it.message, Toast.LENGTH_LONG).show()
                return@addSnapshotListener
            }
            value?.let {
                val communityDataList = mutableListOf<CommunityData>()
                for (document in it) {
                    val myData = document.toObject(CommunityData::class.java)
                    communityDataList.add(myData)
                }
                communitiesLiveData.value = communityDataList
            }
        }
    }

    fun subscribeToPostCommentariesUpdates(context: Context, postId: String) {
        println("postId on subscribe: $postId")
        commentariesCollectionRef.whereEqualTo("post", postId).addSnapshotListener { value, error ->
            error?.let {
                Toast.makeText(context, it.message, Toast.LENGTH_LONG).show()
                return@addSnapshotListener
            }
            value?.let {
                val commentariesList = mutableListOf<Commentary>()
                for (commentary in it) {
                    var commentaryData = commentary.toObject(Commentary::class.java)
                    commentaryData.id = commentary.id
                    commentariesList.add(commentaryData)
                }
                println("commentariesList: $commentariesList")
                postCommentsLiveData.value = commentariesList
            }
        }
    }
}