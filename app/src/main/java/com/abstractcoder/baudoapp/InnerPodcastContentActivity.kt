package com.abstractcoder.baudoapp

import android.content.Context
import android.graphics.drawable.Drawable
import android.media.MediaPlayer
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.view.inputmethod.InputMethodManager
import android.widget.ImageView
import android.widget.SeekBar
import android.widget.Toast
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
import kotlin.collections.ArrayList

class InnerPodcastContentActivity : AppCompatActivity() {

    lateinit var runnable: Runnable
    private var handler = Handler()
    lateinit var podcastMedia: MediaPlayer

    private lateinit var binding: ActivityInnerPodcastContentBinding
    private val db = FirebaseFirestore.getInstance()
    val firestore = Firestore()

    private lateinit var postId: String
    private lateinit var postStatus: String
    private var podcastCurrentPosition: Int = 0
    private var podcastIsFinished: Boolean = false

    private lateinit var userData: FirebaseUser
    private lateinit var userEmail: String
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
        println("podcastContent: $podcastContent")
        postId = podcastContent?.id.toString()
        postStatus = podcastContent?.status.toString()
        println("postStatus: $postStatus")

        val sharedPref = getSharedPreferences(getString(R.string.prefs_file), Context.MODE_PRIVATE)
        val name = sharedPref.getString("name", "")
        val email = sharedPref.getString("email", "")
        userEmail = email!!

        firestore.subscribeToUserUpdates(this, email!!)
        firestore.subscribeToSinglePostUpdates(this, postId)
        firestore.userLiveData.observe(this, androidx.lifecycle.Observer { user ->
            // Update your UI with the new data
            userData = user
            setReactionIcons(userData)
            setup(podcastContent!!, name!!)
        })
        firestore.singlePostLiveData.observe(this, androidx.lifecycle.Observer { post ->
            postData = post
        })
    }

    private fun setCommentsOnRecycler(commentaryList: ArrayList<Commentary>, userEmail: String) {
        podcastCommentList = commentaryList
        commentRecyclerView = binding.podcastCommentListRecycler
        commentRecyclerView.layoutManager = layoutManager
        commentRecyclerView.setHasFixedSize(true)
        commentAdapter = CommentaryAdapter(this, supportFragmentManager, postId, userEmail, podcastCommentList)
        commentRecyclerView.adapter = commentAdapter
    }

    private fun getComments() {
        // Load Posts
        podcastCommentList.clear()
        firestore.subscribeToPostCommentariesUpdates(this, postId)
        firestore.postCommentsLiveData.observe(this, androidx.lifecycle.Observer { commentaries ->
            // Update your UI with the new data
            val organizedCommentaries = commentaries.sortedByDescending { it.timestamp }.toCollection(ArrayList())
            setCommentsOnRecycler(organizedCommentaries, userEmail)
        })
    }

    private fun addComment(userName: String) {
        val commentText = binding.podcastCommentary.text
        val timestamp = Timestamp.now()

        // Add comment
        db.collection("commentaries").add(
            hashMapOf("author" to userName,
                "author_email" to userEmail,
                "text" to commentText.toString(),
                "timestamp" to timestamp,
                "post" to postId)
        ).addOnSuccessListener { documentReference ->
            val commentId = documentReference.id
            println("Firestore New document created with ID: $commentId")

            db.collection("posts").document(postId!!).update(
                "commentaries", FieldValue.arrayUnion(commentId)
            )

            db.collection("users").document(userEmail).update(
                "commentaries", FieldValue.arrayUnion(commentId)
            )

            val imm = getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
            imm.hideSoftInputFromWindow(binding.podcastCommentary.windowToken, 0)
            binding.podcastCommentary.text.clear()
            binding.podcastCommentary.clearFocus()

            Toast.makeText(this, "Comentario guardado", Toast.LENGTH_SHORT).show()
        }
    }

    private fun savePost() {
        val isSaved = userData.saved_posts.contains(postId)
        if (isSaved) {
            db.collection("users").document(userEmail).update(
                "saved_posts", FieldValue.arrayRemove(postId)
            )
            binding.podcastSave.setImageResource(R.drawable.save)
        } else {
            db.collection("users").document(userEmail).update(
                "saved_posts", FieldValue.arrayUnion(postId)
            )
            binding.podcastSave.setImageResource(R.drawable.save_selected)
        }
    }

    private fun setReactionIcons(user: FirebaseUser) {
        val isSaved = user.saved_posts.contains(postId)
        binding.podcastSave.setImageResource(if (isSaved) R.drawable.save_selected else R.drawable.save)
        val reaction = user.reactions.find { it == postId }
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

    private fun setup(podcastContent: PodcastPostMain, userName: String) {
        val userImage = userData.user_pic
        Glide.with(binding.userImageView2)
            .load(userImage)
            .into(binding.userImageView2)
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
        println("podcastCurrentPosition: $podcastCurrentPosition")
        podcastMedia.currentPosition.plus(podcastCurrentPosition)
        podcastMedia.seekTo(podcastCurrentPosition)

        val totalPodcastTime = formatPodcastTime(podcastMedia.duration)
        binding.podcastTotalTime.text = totalPodcastTime

        binding.innerPodcastBack.setOnClickListener {
            finish()
        }
        // Play button Event
        binding.innerPodcastPlay.visibility = ImageView.VISIBLE
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
        binding.innerPodcastSeekbar.visibility = SeekBar.VISIBLE
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
            podcastCurrentPosition = podcastMedia.currentPosition
            binding.innerPodcastSeekbar.progress = podcastMedia.currentPosition
            val currentPodcastTime = formatPodcastTime(podcastMedia.currentPosition)
            binding.podcastCurrentTime.text = currentPodcastTime
            handler.postDelayed(runnable, 1000)
        }
        handler.postDelayed(runnable, 1000)
        podcastMedia.setOnCompletionListener {
            binding.innerPodcastPlay.setImageResource(R.drawable.play)
            binding.innerPodcastSeekbar.progress = 0
            podcastIsFinished = true
            podcastCurrentPosition = 0
            podcastMedia.seekTo(0)
        }

        binding.podcastSave.setOnClickListener {
            savePost()
        }

        val reactionHandler = ReactionHandler()

        binding.podcastLike.setOnClickListener {
            reactionHandler.addReaction(
                userEmail,
                postId,
                userData,
                postData,
                db
            )
        }

        binding.sendPodcastCommentary.setOnClickListener {
            addComment(userName)
        }

        getComments()
    }

    private fun createPodcastRegistry() {
        val newPodcastInfo = PodcastInfo(
            postId, podcastIsFinished
        )
        // Add reaction
        db.collection("users").document(userEmail).update(
            "active_podcasts", FieldValue.arrayUnion(newPodcastInfo)
        )
    }

    private fun updatePodcastInfo() {
        val userActivePodcasts = userData.active_podcasts
        val currentActivePodcast = userActivePodcasts.find { it.post == postId }
        println("podcastIsFinished: $podcastIsFinished")
        println("currentActivePodcast: $currentActivePodcast")
        if (currentActivePodcast == null) {
            createPodcastRegistry()
        } else {
            userActivePodcasts.forEach {
                if (it.post == postId) {
                    if (postStatus != "Finalizado") {
                        it.finished = if (podcastIsFinished == true) true else false
                    }
                }
            }
            println("userActivePodcasts: $userActivePodcasts")
            db.collection("users").document(userEmail!!).update("active_podcasts", userActivePodcasts.toList())
                .addOnCompleteListener {
                    println("podcast $postId: Actualizado")
                }.addOnFailureListener {
                    println("podcast $postId: No pudo ser actualizado")
                }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        firestore.clearListeners()
        updatePodcastInfo()
        podcastMedia.stop()
    }
}