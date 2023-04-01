package com.abstractcoder.baudoapp

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.drawable.Drawable
import android.media.MediaPlayer
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.os.Message
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
            val userName = user.name
            println("currentUser in InnerImage: $user")
        })

        setup(podcastContent!!, name!!)
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

    private fun addComment(userName: String) {
        val commentText = binding.podcastCommentary.text
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
            imm.hideSoftInputFromWindow(binding.podcastCommentary.windowToken, 0)
            binding.podcastCommentary.text.clear()
            binding.podcastCommentary.clearFocus()

            Toast.makeText(this, "Comentario guardado", Toast.LENGTH_SHORT).show()
        }
    }

    private fun setup(podcastContent: PodcastPostMain, userName: String) {
        if (podcastContent.thumbnail != null) {
            val imageUrl = podcastContent.thumbnail

            Glide.with(binding.podcastMainContainer)
                .load(imageUrl)
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
        val dateString = dateFormat.format(podcastContent.timestamp?.toDate() ?: null)
        binding.innerPodcastTimestamp.text = dateString
        binding.innerPodcastDescription.text = podcastContent.description

        podcastMedia = MediaPlayer.create(this, podcastContent.media)

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
        Thread(Runnable {
            while (podcastMedia != null) {
                try {
                    var msg = Message()
                    msg.what = podcastMedia.currentPosition
                    handler.sendMessage(msg)
                    Thread.sleep(1000)
                } catch (e: InterruptedException) {
                }
            }
        })

        @SuppressLint("HandlerLeak")
        var handler = object: Handler() {
            override fun handleMessage(msg: Message) {
                var currentPosition = msg.what
                binding.innerPodcastSeekbar.progress = currentPosition
            }
        }

        binding.sendPodcastCommentary.setOnClickListener {
            addComment(userName)
        }

        getComments()
    }

    override fun onDestroy() {
        super.onDestroy()
        podcastMedia.stop()
    }
}