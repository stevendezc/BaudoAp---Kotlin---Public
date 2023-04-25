package com.abstractcoder.baudoapp

import android.content.Context
import android.content.Intent
import android.graphics.Typeface
import android.os.Bundle
import android.text.Spannable
import android.text.SpannableString
import android.text.style.StyleSpan
import android.view.inputmethod.InputMethodManager
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.abstractcoder.baudoapp.databinding.ActivityInnerImageContentBinding
import com.abstractcoder.baudoapp.recyclers.CommentaryAdapter
import com.abstractcoder.baudoapp.recyclers.ImagePostMain
import com.abstractcoder.baudoapp.utils.Firestore
import com.abstractcoder.baudoapp.utils.ReactionHandler
import com.bumptech.glide.Glide
import com.google.firebase.Timestamp
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore


class innerImageContentActivity : AppCompatActivity() {

    private lateinit var binding: ActivityInnerImageContentBinding
    private val db = FirebaseFirestore.getInstance()
    val firestoreInst = Firestore()

    private lateinit var postId: String
    private lateinit var userData: FirebaseUser
    private lateinit var postData: PostData
    private lateinit var layoutManager: LinearLayoutManager
    private lateinit var commentAdapter: CommentaryAdapter
    private lateinit var commentRecyclerView: RecyclerView
    private var imageCommentList: ArrayList<Commentary> = arrayListOf<Commentary>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityInnerImageContentBinding.inflate(layoutInflater)
        setContentView(binding.root)

        layoutManager = LinearLayoutManager(this.baseContext)
        val imageContent = intent.getParcelableExtra<ImagePostMain>("image")
        postId = imageContent?.id.toString()

        val sharedPref = getSharedPreferences(getString(R.string.prefs_file), Context.MODE_PRIVATE)
        val name = sharedPref.getString("name", "")
        val email = sharedPref.getString("email", "")

        firestoreInst.activateSubscribers(this, email!!)
        firestoreInst.userLiveData.observe(this, Observer { user ->
            // Update your UI with the new data
            userData = user
            setReactionIcons(userData)
        })

        firestoreInst.subscribeToSinglePostUpdates(this, postId)
        firestoreInst.singlePostLiveData.observe(this, Observer { post ->
            // Update your UI with the new data
            postData = post
        })

        setup(imageContent!!, name!!, email!!)
    }

    private fun setCommentsOnRecycler(commentaryList: ArrayList<Commentary>) {
        imageCommentList = commentaryList
        commentRecyclerView = binding.imageCommentListRecycler
        commentRecyclerView.layoutManager = layoutManager
        commentRecyclerView.setHasFixedSize(true)
        commentAdapter = CommentaryAdapter(imageCommentList)
        commentRecyclerView.adapter = commentAdapter
    }

    private fun getComments() {
        // Load Posts
        imageCommentList.clear()
        firestoreInst.subscribeToPostCommentariesUpdates(this, postId)
        firestoreInst.postCommentsLiveData.observe(this, Observer { commentaries ->
            // Update your UI with the new data
            val organizedCommentaries = commentaries.sortedByDescending { it.timestamp }.toCollection(ArrayList())
            setCommentsOnRecycler(organizedCommentaries)
        })
    }

    private fun addComment(userName: String, authorEmail: String) {
        val commentText = binding.imageCommentary.text
        val timestamp = Timestamp.now()

        // Add comment
        db.collection("commentaries").add(
            hashMapOf("author" to userName,
                "author_email" to authorEmail,
                "text" to commentText.toString(),
                "timestamp" to timestamp,
                "post" to postId)
        ).addOnSuccessListener { documentReference ->
            val commentId = documentReference.id

            db.collection("posts").document(postId!!).update(
                "commentaries", FieldValue.arrayUnion(commentId)
            )

            val imm = getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
            imm.hideSoftInputFromWindow(binding.imageCommentary.windowToken, 0)
            binding.imageCommentary.text.clear()
            binding.imageCommentary.clearFocus()

            Toast.makeText(this, "Comentario guardado", Toast.LENGTH_SHORT).show()
        }
    }

    private fun savePost(email: String) {
        val isSaved = userData.saved_posts.contains(postId)
        if (isSaved) {
            db.collection("users").document(email!!).update(
                "saved_posts", FieldValue.arrayRemove(postId)
            )
            binding.imageSave.setImageResource(R.drawable.save)
        } else {
            db.collection("users").document(email!!).update(
                "saved_posts", FieldValue.arrayUnion(postId)
            )
            binding.imageSave.setImageResource(R.drawable.save_selected)
        }
    }

    private fun setReactionIcons(user: FirebaseUser) {
        val isSaved = user.saved_posts.contains(postId)
        binding.imageSave.setImageResource(if (isSaved) R.drawable.save_selected else R.drawable.save)
        val reaction = user.reactions.find { it.post == postId }
        println("reaction: $reaction")
        if (reaction == null) {
            binding.imageLike.setImageResource(R.drawable.like)
            binding.imageIndifferent.setImageResource(R.drawable.indifferent)
            binding.imageDislike.setImageResource(R.drawable.dislike)
        }
        when (reaction?.type) {
            "likes" -> {
                binding.imageLike.setImageResource(R.drawable.like_selected)
                binding.imageIndifferent.setImageResource(R.drawable.indifferent)
                binding.imageDislike.setImageResource(R.drawable.dislike)
            }
            "indifferents" -> {
                binding.imageLike.setImageResource(R.drawable.like)
                binding.imageIndifferent.setImageResource(R.drawable.indifferent_selected)
                binding.imageDislike.setImageResource(R.drawable.dislike)
            }
            "dislikes" -> {
                binding.imageLike.setImageResource(R.drawable.like)
                binding.imageIndifferent.setImageResource(R.drawable.indifferent)
                binding.imageDislike.setImageResource(R.drawable.dislike_selected)
            }
        }
    }

    private fun setup(imageContent: ImagePostMain, userName: String, email: String) {
        if (imageContent != null) {
            val imageUrl = imageContent.thumbnail
            Glide.with(binding.imageMainImage)
                .load(imageUrl)
                .into(binding.imageMainImage)

            binding.imageTitle.text = imageContent.location

            val descriptionTextComplement = " Foto por: " + imageContent.author
            val descriptionText = imageContent.description + descriptionTextComplement
            val spannableString = SpannableString(descriptionText)
            val startIndex: Int = descriptionText.indexOf(descriptionTextComplement)
            val endIndex = startIndex + descriptionTextComplement.length
            spannableString.setSpan(
                StyleSpan(Typeface.BOLD),
                startIndex,
                endIndex,
                Spannable.SPAN_EXCLUSIVE_EXCLUSIVE
            )
            binding.imageDescription.text = spannableString

            binding.imageClose.setOnClickListener {
                finish()
            }

            binding.imageExpand.setOnClickListener {
                Intent(this, resizedImageActivity::class.java).apply {
                    putExtra("image", imageUrl)
                    startActivity(this)
                }
            }

            binding.imageSave.setOnClickListener {
                savePost(email)
            }

            val reactionHandler = ReactionHandler()

            binding.imageLike.setOnClickListener {
                reactionHandler.addReaction(
                    email,
                    postId,
                    "likes",
                    userData,
                    postData,
                    db
                )
            }

            binding.imageIndifferent.setOnClickListener {
                reactionHandler.addReaction(
                    email,
                    postId,
                    "indifferents",
                    userData,
                    postData,
                    db
                )
            }

            binding.imageDislike.setOnClickListener {
                reactionHandler.addReaction(
                    email,
                    postId,
                    "dislikes",
                    userData,
                    postData,
                    db
                )
            }

            binding.sendImageCommentary.setOnClickListener {
                addComment(userName, email)
            }

            getComments()
        }
    }
}