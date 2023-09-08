package com.pereira.baudoapp.utils

import android.content.Context
import android.util.Log
import android.widget.Toast
import androidx.lifecycle.MutableLiveData
import com.pereira.baudoapp.*
import com.pereira.baudoapp.recyclers.EventMain
import com.pereira.baudoapp.recyclers.StoreItemMain
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.FirebaseFirestoreSettings
import com.google.firebase.firestore.ListenerRegistration
import com.google.firebase.firestore.Source
import com.google.firebase.firestore.ktx.firestoreSettings

class Firestore {
    private var db = FirebaseFirestore.getInstance()

    lateinit var singleUserListener: ListenerRegistration
    lateinit var usersListener: ListenerRegistration
    lateinit var postsListener: ListenerRegistration
    lateinit var communitiesListener: ListenerRegistration
    lateinit var eventsListener: ListenerRegistration
    lateinit var commentsListener: ListenerRegistration
    lateinit var singlePostListener: ListenerRegistration
     lateinit var productsListener: ListenerRegistration

    val userLiveData = MutableLiveData<FirebaseUser>()
    val usersLiveData = MutableLiveData<List<FirebaseUser>>()
    val postsLiveData = MutableLiveData<List<PostData>>()
    val communitiesLiveData = MutableLiveData<List<CommunityData>>()
    val eventsLiveData = MutableLiveData<List<EventMain>>()
    val postCommentsLiveData = MutableLiveData<List<Commentary>>()
    val productsLiveData = MutableLiveData<List<StoreItemMain>>()
    val singlePostLiveData = MutableLiveData<PostData>()

    fun subscribeToUserUpdates(context: Context, email: String) {
        val userCollectionRef = db.collection("users")
        singleUserListener = userCollectionRef.document(email).addSnapshotListener { value, error ->
            error?.let {
                it.message?.let { it1 -> Log.d("TAG", it1) }
                //Toast.makeText(context, it.message, Toast.LENGTH_LONG).show()
                return@addSnapshotListener
            }
            value?.let {
                val myData = it.toObject(FirebaseUser::class.java) ?: FirebaseUser()
                userLiveData.value = myData
            }
        }
    }

    private fun subscribeToUsersUpdates(context: Context) {
        val userCollectionRef = db.collection("users")
        usersListener = userCollectionRef.addSnapshotListener { value, error ->
            error?.let {
                it.message?.let { it1 -> Log.d("TAG", it1) }
                //Toast.makeText(context, it.message, Toast.LENGTH_LONG).show()
                return@addSnapshotListener
            }
            value?.let {
                val usersDataList = mutableListOf<FirebaseUser>()
                for (document in it) {
                    println("DOCUMENT: $document")
                    var userData = document.toObject(FirebaseUser::class.java)
                    userData.id = document.id
                    usersDataList.add(userData)
                }
                usersLiveData.value = usersDataList
            }
        }
    }

    fun subscribeToPostUpdates(context: Context) {
        val postsCollectionRef = db.collection("posts")
        postsListener = postsCollectionRef.addSnapshotListener { value, error ->
            error?.let {
                it.message?.let { it1 -> Log.d("TAG", it1) }
                //Toast.makeText(context, it.message, Toast.LENGTH_LONG).show()
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
        val communitiesCollectionRef = db.collection("communities")
        communitiesListener = communitiesCollectionRef.addSnapshotListener { value, error ->
            error?.let {
                it.message?.let { it1 -> Log.d("TAG", it1) }
                //Toast.makeText(context, it.message, Toast.LENGTH_LONG).show()
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

    private fun subscribeToEventUpdates(context: Context) {
        val eventsCollectionRef = db.collection("events")
        eventsListener = eventsCollectionRef.addSnapshotListener { value, error ->
            error?.let {
                it.message?.let { it1 -> Log.d("TAG", it1) }
                //Toast.makeText(context, it.message, Toast.LENGTH_LONG).show()
                return@addSnapshotListener
            }
            value?.let {
                val eventDataList = mutableListOf<EventMain>()
                for (document in it) {
                    println("DOCUMENT: $document")
                    val myData = document.toObject(EventMain::class.java)
                    myData.id = document.id
                    eventDataList.add(myData)
                }
                eventsLiveData.value = eventDataList
            }
        }
    }

    fun subscribeToPostCommentariesUpdates(context: Context, postId: String) {
        val commentariesCollectionRef = db.collection("commentaries")
        commentsListener = commentariesCollectionRef.whereEqualTo("post", postId).addSnapshotListener { value, error ->
            error?.let {
                it.message?.let { it1 -> Log.d("TAG", it1) }
                //Toast.makeText(context, it.message, Toast.LENGTH_LONG).show()
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

    fun subscribeToSinglePostUpdates(context: Context, postId: String) {
        val postsCollectionRef = db.collection("posts")
        singlePostListener = postsCollectionRef.document(postId).addSnapshotListener { value, error ->
            error?.let {
                it.message?.let { it1 -> Log.d("TAG", it1) }
                //Toast.makeText(context, it.message, Toast.LENGTH_LONG).show()
                return@addSnapshotListener
            }
            value?.let {
                var postData = it.toObject(PostData::class.java) ?: PostData()
                postData.id = it.id
                println("singlePostLiveData: $postData")
                singlePostLiveData.value = postData
            }
        }
    }

    private fun subscribeToProductsUpdates(context: Context) {
        val productsCollectionRef = db.collection("productos")
        productsListener = productsCollectionRef.addSnapshotListener { value, error ->
            error?.let {
                it.message?.let { it1 -> Log.d("TAG", it1) }
                //Toast.makeText(context, it.message, Toast.LENGTH_LONG).show()
                return@addSnapshotListener
            }
            value?.let {
                val productsDataList = mutableListOf<StoreItemMain>()
                for (document in it) {
                    val myData = document.toObject(StoreItemMain::class.java)
                    myData.id = document.id.toString()
                    productsDataList.add(myData)
                }
                productsLiveData.value = productsDataList
            }
        }
    }

    fun clearListeners() {
        if (::singleUserListener.isInitialized) singleUserListener.remove()
        if (::usersListener.isInitialized) usersListener.remove()
        if (::postsListener.isInitialized) postsListener.remove()
        if (::communitiesListener.isInitialized) communitiesListener.remove()
        if (::eventsListener.isInitialized) eventsListener.remove()
        if (::commentsListener.isInitialized) commentsListener.remove()
        if (::singlePostListener.isInitialized) singlePostListener.remove()
        if (::productsListener.isInitialized) productsListener.remove()
    }
}