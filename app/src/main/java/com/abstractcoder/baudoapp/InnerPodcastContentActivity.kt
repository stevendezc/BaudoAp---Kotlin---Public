package com.abstractcoder.baudoapp

import android.content.Context
import android.graphics.drawable.Drawable
import android.media.MediaPlayer
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.view.inputmethod.InputMethodManager
import android.widget.SeekBar
import android.widget.Toast
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.abstractcoder.baudoapp.databinding.ActivityInnerPodcastContentBinding
import com.abstractcoder.baudoapp.recyclers.CommentaryAdapter
import com.abstractcoder.baudoapp.recyclers.PodcastPostMain
import com.abstractcoder.baudoapp.utils.Firestore
import com.abstractcoder.baudoapp.utils.ReactionHandler
import com.bumptech.glide.Glide
import com.bumptech.glide.request.target.CustomTarget
import com.google.firebase.Timestamp
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import java.text.SimpleDateFormat
import java.util.*

class InnerPodcastContentActivity : AppCompatActivity() {

    lateinit var runnable: Runnable
    private var handler = Handler()
    lateinit var podcastMedia: MediaPlayer

    private lateinit var binding: ActivityInnerPodcastContentBinding
    private val db = FirebaseFirestore.getInstance()
    private var firestoreInst = Firestore()

    private lateinit var postId: String
    private lateinit var userData: FirebaseUser
    private lateinit var postData: PostData
    private lateinit var layoutManager: LinearLayoutManager
    private lateinit var commentAdapter: CommentaryAdapter
    private lateinit var commentRecyclerView: RecyclerView
    private var podcastCommentList: ArrayList<Commentary> = arrayListOf<Commentary>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityInnerPodcastContentBinding.inflate(layoutInflater)
        setContentView(binding.root)

        layoutManager = LinearLayoutManager(this.baseContext)
        val podcastContent = intent.getParcelableExtra<PodcastPostMain>("podcast")
        postId = podcastContent?.id.toString()

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

        setup(podcastContent!!, name!!, email!!)
    }

    private fun setCommentsOnRecycler(commentaryList: ArrayList<Commentary>) {
        podcastCommentList = commentaryList
        commentRecyclerView = binding.podcastCommentListRecycler
        commentRecyclerView.layoutManager = layoutManager
        commentRecyclerView.setHasFixedSize(true)
        commentAdapter = CommentaryAdapter(podcastCommentList)
        commentRecyclerView.adapter = commentAdapter
    }

    private fun getComments() {
        // Load Posts
        podcastCommentList.clear()
        firestoreInst.subscribeToPostCommentariesUpdates(this, postId)
        firestoreInst.postCommentsLiveData.observe(this, Observer { commentaries ->
            // Update your UI with the new data
            val organizedCommentaries = commentaries.sortedByDescending { it.timestamp }.toCollection(ArrayList())
            println("organizedCommentaries: $organizedCommentaries")
            setCommentsOnRecycler(organizedCommentaries)
        })
    }

    private fun addComment(userName: String, authorEmail: String) {
        val commentText = binding.podcastCommentary.text
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
            println("Firestore New document created with ID: $commentId")

            db.collection("posts").document(postId!!).update(
                "commentaries", FieldValue.arrayUnion(commentId)
            )

            db.collection("users").document(authorEmail).update(
                "commentaries", FieldValue.arrayUnion(commentId)
            )

            val imm = getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
            imm.hideSoftInputFromWindow(binding.podcastCommentary.windowToken, 0)
            binding.podcastCommentary.text.clear()
            binding.podcastCommentary.clearFocus()

            Toast.makeText(this, "Comentario guardado", Toast.LENGTH_SHORT).show()
        }
    }

    private fun savePost(email: String) {
        val isSaved = userData.saved_posts.contains(postId)
        if (isSaved) {
            db.collection("users").document(email!!).update(
                "saved_posts", FieldValue.arrayRemove(postId)
            )
            binding.podcastSave.setImageResource(R.drawable.save)
        } else {
            db.collection("users").document(email!!).update(
                "saved_posts", FieldValue.arrayUnion(postId)
            )
            binding.podcastSave.setImageResource(R.drawable.save_selected)
        }
    }

    private fun setReactionIcons(user: FirebaseUser) {
        val isSaved = user.saved_posts.contains(postId)
        binding.podcastSave.setImageResource(if (isSaved) R.drawable.save_selected else R.drawable.save)
        val reaction = user.reactions.find { it.post == postId }
        println("reaction: $reaction")
        if (reaction == null) {
            binding.podcastLike.setImageResource(R.drawable.like)
        } else {
            binding.podcastLike.setImageResource(R.drawable.like_selected)
        }
    }

    private fun formatPodcastTime(milliseconds: Int): String {
        val seconds = milliseconds / 1000
        val minutes = seconds / 60
        val remainingSeconds = seconds % 60
        return String.format("%02d:%02d", minutes, remainingSeconds)
    }

    private fun setup(podcastContent: PodcastPostMain, userName: String, email: String) {
        if (podcastContent.thumbnail != null) {
            val backgroundUrl = podcastContent.background
            val imageUrl = podcastContent.thumbnail

            Glide.with(binding.podcastMainContainer)
                .load(backgroundUrl)
                .centerCrop()
                .into(object : CustomTarget<Drawable>() {
                    override fun onResourceReady(
                        resource: Drawable,
                        transition: com.bumptech.glide.request.transition.Transition<in Drawable>?
                    ) {
                        binding.podcastMainContainer.background = resource
                    }

                    override fun onLoadCleared(placeholder: Drawable?) {
                        // Not implemented
                    }
                })

            Glide.with(binding.innerPodcastMedia)
                .load(imageUrl)
                .into(binding.innerPodcastMedia)
        }
        binding.innerPodcastTitle.text = podcastContent.title
        val dateFormat = SimpleDateFormat("dd MMMM ',' yyyy", Locale("es", "ES"))
        val dateString = dateFormat.format(podcastContent.creation_date?.toDate() ?: null)
        binding.innerPodcastTimestamp.text = dateString
        binding.innerPodcastDescription.text = podcastContent.description

        podcastMedia = MediaPlayer.create(this, podcastContent.media)
        val totalPodcastTime = formatPodcastTime(podcastMedia.duration)
        binding.podcastTotalTime.text = totalPodcastTime

        binding.innerPodcastBack.setOnClickListener {
            finish()
        }
        // Play button Event
        binding.innerPodcastPlay.setOnClickListener {
            if (!podcastMedia.isPlaying) {
                podcastMedia.start()
                binding.innerPodcastPlay.setImageResource(R.drawable.pause)
            } else {
                podcastMedia.pause()
                binding.innerPodcastPlay.setImageResource(R.drawable.play)
            }
        }
        // Seekbar functionalities
        binding.innerPodcastSeekbar.setProgress(0);
        binding.innerPodcastSeekbar.setMax(0);
        binding.innerPodcastSeekbar.setPadding(0, 0, 0, 0);
        binding.innerPodcastSeekbar.progress = 0
        binding.innerPodcastSeekbar.max = podcastMedia.duration
        binding.innerPodcastSeekbar.setOnSeekBarChangeListener(object: SeekBar.OnSeekBarChangeListener{
            override fun onProgressChanged(seekbar: SeekBar?, position: Int, changed: Boolean) {
                if (changed) {
                    podcastMedia.seekTo(position)
                }
            }

            override fun onStartTrackingTouch(p0: SeekBar?) {
            }

            override fun onStopTrackingTouch(p0: SeekBar?) {
            }
        })

        runnable = Runnable {
            binding.innerPodcastSeekbar.progress = podcastMedia.currentPosition
            val currentPodcastTime = formatPodcastTime(podcastMedia.currentPosition)
            binding.podcastCurrentTime.text = currentPodcastTime
            handler.postDelayed(runnable, 1000)
        }
        handler.postDelayed(runnable, 1000)
        podcastMedia.setOnCompletionListener {
            binding.innerPodcastPlay.setImageResource(R.drawable.play)
            binding.innerPodcastSeekbar.progress = 0
        }

        binding.podcastSave.setOnClickListener {
            savePost(email)
        }

        val reactionHandler = ReactionHandler()

        binding.podcastLike.setOnClickListener {
            reactionHandler.addReaction(
                email,
                postId,
                "likes",
                userData,
                postData,
                db
            )
        }

        binding.sendPodcastCommentary.setOnClickListener {
            addComment(userName, email)
        }

        getComments()
    }

    override fun onDestroy() {
        super.onDestroy()
        podcastMedia.stop()
    }
}