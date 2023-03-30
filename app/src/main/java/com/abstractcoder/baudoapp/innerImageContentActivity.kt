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
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.abstractcoder.baudoapp.databinding.ActivityInnerImageContentBinding
import com.abstractcoder.baudoapp.recyclers.CommentaryAdapter
import com.abstractcoder.baudoapp.recyclers.ImagePostMain
import com.abstractcoder.baudoapp.utils.CommentsCallback
import com.abstractcoder.baudoapp.utils.Firestore
import com.bumptech.glide.Glide
import com.google.firebase.Timestamp
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore


class innerImageContentActivity : AppCompatActivity() {

    private lateinit var binding: ActivityInnerImageContentBinding
    private val db = FirebaseFirestore.getInstance()
    private var firestore = Firestore()

    private lateinit var postId: String
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
        println("Name on innerImageContentActivity $name")

        setup(imageContent!!, name!!)
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
        val posts = firestore.retrievePostComments(object: CommentsCallback {
            override fun onSuccess(result: ArrayList<Commentary>) {
                // Organize Commentaries by timestamp
                val organizedCommentaries = result.sortedByDescending { it.timestamp }.toCollection(ArrayList())
                setCommentsOnRecycler(organizedCommentaries)
            }
        }, postId!!)
    }

    private fun addComment(userName: String) {
        val commentText = binding.imageCommentary.text
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
            imm.hideSoftInputFromWindow(binding.imageCommentary.windowToken, 0)
            binding.imageCommentary.text.clear()
            binding.imageCommentary.clearFocus()

            Toast.makeText(this, "Comentario guardado", Toast.LENGTH_SHORT).show()

            getComments()
        }
    }

    private fun setup(imageContent: ImagePostMain, userName: String) {
        if (imageContent != null) {
            val imageUrl = imageContent.thumbnail
            Glide.with(binding.imageMainImage)
                .load(imageUrl)
                .into(binding.imageMainImage)

            binding.imageTitle.text = imageContent.title

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

            binding.sendImageCommentary.setOnClickListener {
                addComment(userName)
            }

            getComments()
        }
    }
}