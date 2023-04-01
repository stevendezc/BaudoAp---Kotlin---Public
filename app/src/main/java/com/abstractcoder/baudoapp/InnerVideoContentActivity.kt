package com.abstractcoder.baudoapp

import android.content.Context
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.inputmethod.InputMethodManager
import android.widget.Toast
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.abstractcoder.baudoapp.databinding.ActivityInnerVideoContentBinding
import com.abstractcoder.baudoapp.recyclers.CommentaryAdapter
import com.abstractcoder.baudoapp.recyclers.VideoPostMain
import com.abstractcoder.baudoapp.utils.Firestore
import com.bumptech.glide.Glide
import com.google.firebase.Timestamp
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore

class InnerVideoContentActivity : AppCompatActivity() {

    private lateinit var binding: ActivityInnerVideoContentBinding
    private val db = FirebaseFirestore.getInstance()
    private var firestoreInst = Firestore()

    private lateinit var postId: String
    private lateinit var layoutManager: LinearLayoutManager
    private lateinit var commentAdapter: CommentaryAdapter
    private lateinit var commentRecyclerView: RecyclerView
    private var videoCommentList: ArrayList<Commentary> = arrayListOf<Commentary>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityInnerVideoContentBinding.inflate(layoutInflater)
        setContentView(binding.root)

        layoutManager = LinearLayoutManager(this.baseContext)
        val videoContent = intent.getParcelableExtra<VideoPostMain>("video_content")
        postId = videoContent?.id.toString()

        val sharedPref = getSharedPreferences(getString(R.string.prefs_file), Context.MODE_PRIVATE)
        val name = sharedPref.getString("name", "")
        val email = sharedPref.getString("email", "")
        println("Name on innerVideoContentActivity $name")

        firestoreInst.activateSubscribers(this, email!!)
        firestoreInst.userLiveData.observe(this, Observer { user ->
            // Update your UI with the new data
            val userName = user.name
            println("currentUser in InnerImage: $user")
        })

        setup(videoContent!!, name!!)
    }

    private fun setCommentsOnRecycler(commentaryList: ArrayList<Commentary>) {
        videoCommentList = commentaryList
        commentRecyclerView = binding.imageCommentListRecycler
        commentRecyclerView.layoutManager = layoutManager
        commentRecyclerView.setHasFixedSize(true)
        commentAdapter = CommentaryAdapter(videoCommentList)
        commentRecyclerView.adapter = commentAdapter
    }

    private fun getComments() {
        // Load Posts
        videoCommentList.clear()
        firestoreInst.subscribeToPostCommentariesUpdates(this, postId)
        firestoreInst.postCommentsLiveData.observe(this, Observer { commentaries ->
            // Update your UI with the new data
            val organizedCommentaries = commentaries.sortedByDescending { it.timestamp }.toCollection(ArrayList())
            println("organizedCommentaries: $organizedCommentaries")
            setCommentsOnRecycler(organizedCommentaries)
        })
    }

    private fun addComment(userName: String) {
        val commentText = binding.videoCommentary.text
        val timestamp = Timestamp.now()

        // Add comment
        db.collection("commentaries").add(
            hashMapOf("author" to userName,
                "text" to commentText.toString(),
                "timestamp" to timestamp,
                "post" to postId)
        ).addOnSuccessListener { documentReference ->
            val commentId = documentReference.id
            println("Firestore New document created with ID: $commentId")

            db.collection("posts").document(postId!!).update(
                "commentaries", FieldValue.arrayUnion(commentId)
            )

            val imm = getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
            imm.hideSoftInputFromWindow(binding.videoCommentary.windowToken, 0)
            binding.videoCommentary.text.clear()
            binding.videoCommentary.clearFocus()

            Toast.makeText(this, "Comentario guardado", Toast.LENGTH_SHORT).show()
        }
    }

    private fun setup(videoContent: VideoPostMain, userName: String) {
        if (videoContent != null) {

            binding.innerVideoBack.setOnClickListener {
                finish()
                overridePendingTransition(com.facebook.R.anim.abc_slide_in_top, com.facebook.R.anim.abc_slide_out_bottom)
            }

            val imageUrl = videoContent.thumbnail
            Glide.with(binding.innerVideoMedia)
                .load(imageUrl)
                .into(binding.innerVideoMedia)

            binding.innerVideoTitle.text = videoContent.title
            binding.innerVideoDescription.text = videoContent.description
            binding.innerVideoCategory.text = videoContent.category

            binding.sendVideoCommentary.setOnClickListener {
                addComment(userName)
            }

            getComments()
        }
    }
}