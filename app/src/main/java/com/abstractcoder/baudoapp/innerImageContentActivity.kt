package com.pereira.baudoapp

import android.content.Context
import android.content.Intent
import android.graphics.Typeface
import android.os.Bundle
import android.text.Spannable
import android.text.SpannableString
import android.text.style.StyleSpan
import android.util.Log
import android.view.inputmethod.InputMethodManager
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.MutableLiveData
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.pereira.baudoapp.databinding.ActivityInnerImageContentBinding
import com.pereira.baudoapp.recyclers.CommentaryAdapter
import com.pereira.baudoapp.recyclers.ImagePostMain
import com.pereira.baudoapp.utils.Firestore
import com.pereira.baudoapp.utils.ReactionHandler
import com.bumptech.glide.Glide
import com.google.firebase.Timestamp
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ListenerRegistration
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList

class innerImageContentActivity : AppCompatActivity() {

    private lateinit var binding: ActivityInnerImageContentBinding
    private val db = FirebaseFirestore.getInstance()
    val firestore = Firestore()

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

        firestore.subscribeToUserUpdates(this, email!!)
        firestore.subscribeToSinglePostUpdates(this, postId)
        firestore.userLiveData.observe(this, androidx.lifecycle.Observer { user ->
            // Update your UI with the new data
            userData = user
            println("Updated userData: $userData")
            val userImage = userData.user_pic
            Glide.with(binding.userImageView2)
                .load(userImage)
                .into(binding.userImageView2)
            setReactionIcons(userData)
        })
        firestore.singlePostLiveData.observe(this, androidx.lifecycle.Observer { post ->
            postData = post
        })
        setup(imageContent!!, name!!, email!!)
    }

    private fun setCommentsOnRecycler(commentaryList: ArrayList<Commentary>, userEmail: String) {
        imageCommentList = commentaryList
        commentRecyclerView = binding.imageCommentListRecycler
        commentRecyclerView.layoutManager = layoutManager
        commentRecyclerView.setHasFixedSize(true)
        commentAdapter = CommentaryAdapter(this, supportFragmentManager, postId, userEmail, imageCommentList)
        commentRecyclerView.adapter = commentAdapter
    }

    private fun getComments(userEmail: String) {
        // Load Posts
        imageCommentList.clear()
        firestore.subscribeToPostCommentariesUpdates(this, postId)
        firestore.postCommentsLiveData.observe(this, androidx.lifecycle.Observer { commentaries ->
            // Update your UI with the new data
            val organizedCommentaries = commentaries.sortedByDescending { it.timestamp }.toCollection(ArrayList())
            setCommentsOnRecycler(organizedCommentaries, userEmail)
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

            db.collection("users").document(authorEmail).update(
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
        val reaction = user.reactions.find { it == postId }
        println("reaction: $reaction")
        if (reaction == null) {
            binding.imageLike.setImageResource(R.drawable.like)
        } else {
            binding.imageLike.setImageResource(R.drawable.like_selected)
        }
    }

    private fun setup(imageContent: ImagePostMain, userName: String, email: String) {
        if (imageContent != null) {
            val imageThumbnail = imageContent.thumbnail
            val imageMainMedia = imageContent.main_media
            Glide.with(binding.imageMainImage)
                .load(imageThumbnail)
                .into(binding.imageMainImage)

            binding.imageTitle.text = imageContent.location

            binding.imageDescription.text = imageContent.description

            val authorComplementaryText = "\nFoto por:  "
            val descriptionText = authorComplementaryText + imageContent.author
            val spannableAuthorString = SpannableString(descriptionText)
            val startIndex: Int = descriptionText.indexOf(authorComplementaryText)
            val endIndex = startIndex + authorComplementaryText.length
            spannableAuthorString.setSpan(
                StyleSpan(Typeface.BOLD),
                startIndex,
                endIndex,
                Spannable.SPAN_EXCLUSIVE_EXCLUSIVE
            )
            binding.imageAuthor.text = spannableAuthorString

            val dateComplementaryText = "Publicado:  "
            val dateFormat = SimpleDateFormat("dd MMMM ',' yyyy", Locale("es", "ES"))
            val postDate = dateComplementaryText + dateFormat.format(imageContent.creation_date?.toDate() ?: null)
            val spannableDateString = SpannableString(postDate)
            val startIndex2: Int = postDate.indexOf(dateComplementaryText)
            val endIndex2 = startIndex2 + dateComplementaryText.length
            spannableDateString.setSpan(
                StyleSpan(Typeface.BOLD),
                startIndex2,
                endIndex2,
                Spannable.SPAN_EXCLUSIVE_EXCLUSIVE
            )
            binding.imageDate.text = spannableDateString

            binding.imageClose.setOnClickListener {
                finish()
            }

            binding.imageExpand.setOnClickListener {
                Intent(this, resizedImageActivity::class.java).apply {
                    putExtra("image", imageMainMedia)
                    startActivity(this)
                }
            }

            binding.imageMainImage.setOnClickListener {
                Intent(this, resizedImageActivity::class.java).apply {
                    putExtra("image", imageMainMedia)
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
                    userData,
                    postData,
                    db
                )
            }

            binding.sendImageCommentary.setOnClickListener {
                addComment(userName, email)
            }

            getComments(email)
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        firestore.clearListeners()
    }
}