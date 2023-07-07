package com.abstractcoder.baudoapp.utils

import android.content.Context
import android.util.Log
import android.widget.Toast
import androidx.lifecycle.MutableLiveData
import com.abstractcoder.baudoapp.*
import com.abstractcoder.baudoapp.recyclers.EventMain
import com.abstractcoder.baudoapp.recyclers.StoreItemMain
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.FirebaseFirestoreSettings
import com.google.firebase.firestore.ListenerRegistration
import com.google.firebase.firestore.ktx.firestoreSettings

class Firestore {
    private val db = FirebaseFirestore.getInstance()

    private lateinit var singleUserListener: ListenerRegistration
    private lateinit var usersListener: ListenerRegistration
    private lateinit var postsListener: ListenerRegistration
    private lateinit var communitiesListener: ListenerRegistration
    private lateinit var eventsListener: ListenerRegistration
    private lateinit var commentsListener: ListenerRegistration
    private lateinit var singlePostListener: ListenerRegistration
    private lateinit var productsListener: ListenerRegistration

    val userLiveData = MutableLiveData<FirebaseUser>()
    val usersLiveData = MutableLiveData<List<FirebaseUser>>()
    val postsLiveData = MutableLiveData<List<PostData>>()
    val communitiesLiveData = MutableLiveData<List<CommunityData>>()
    val eventsLiveData = MutableLiveData<List<EventMain>>()
    val postCommentsLiveData = MutableLiveData<List<Commentary>>()
    val productsLiveData = MutableLiveData<List<StoreItemMain>>()
    val singlePostLiveData = MutableLiveData<PostData>()

    val userCollectionRef = db.collection("users")
    val postsCollectionRef = db.collection("posts")
    val communitiesCollectionRef = db.collection("communities")
    val eventsCollectionRef = db.collection("events")
    val commentariesCollectionRef = db.collection("commentaries")
    val productsCollectionRef = db.collection("productos")

    fun activateSubscribers(context: Context, email: String) {
        db.firestoreSettings = firestoreSettings {
            this.isPersistenceEnabled = true
        }
        subscribeToUsersUpdates(context)
        subscribeToUserUpdates(context, email)
        subscribeToPostUpdates(context)
        subscribeToCommunityUpdates(context)
        subscribeToEventUpdates(context)
        subscribeToProductsUpdates(context)
    }

    private fun subscribeToUserUpdates(context: Context, email: String) {
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
        usersListener = userCollectionRef.addSnapshotListener { value, error ->
            error?.let {
                it.message?.let { it1 -> Log.d("TAG", it1) }
                //Toast.makeText(context, it.message, Toast.LENGTH_LONG).show()
                return@addSnapshotListener
            }
            value?.let {
                val usersDataList = mutableListOf<FirebaseUser>()
                for (document in it) {
                    var userData = document.toObject(FirebaseUser::class.java)
                    userData.id = document.id
                    usersDataList.add(userData)
                }
                usersLiveData.value = usersDataList
            }
        }
    }

    private fun subscribeToPostUpdates(context: Context) {
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
        println("postId on subscribe: $postId")
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