package com.abstractcoder.baudoapp

import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.GestureDetector
import android.view.MotionEvent
import android.view.animation.AlphaAnimation
import android.view.animation.Animation
import android.widget.ImageView
import androidx.core.view.GestureDetectorCompat
import androidx.lifecycle.Observer
import com.abstractcoder.baudoapp.databinding.ActivityFullSizeVideoBinding
import com.abstractcoder.baudoapp.recyclers.VideoPostMain
import com.abstractcoder.baudoapp.utils.Firestore
import com.abstractcoder.baudoapp.utils.ReactionHandler
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore

class FullSizeVideoActivity : AppCompatActivity(), GestureDetector.OnGestureListener {

    private lateinit var binding: ActivityFullSizeVideoBinding
    private lateinit var videoContent: VideoPostMain

    // Gesture detector
    private lateinit var gestureDetector: GestureDetectorCompat

    private val db = FirebaseFirestore.getInstance()
    private var firestoreInst = Firestore()

    private lateinit var postId: String
    private lateinit var userData: FirebaseUser
    private lateinit var postData: PostData
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityFullSizeVideoBinding.inflate(layoutInflater)
        setContentView(binding.root)

        videoContent = intent.getParcelableExtra<VideoPostMain>("video")!!
        postId = videoContent?.id.toString()

        gestureDetector = GestureDetectorCompat(this, this)

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
        setup(videoContent!!, name!!, email!!)
    }

    private fun savePost(email: String) {
        val isSaved = userData.saved_posts.contains(postId)
        if (isSaved) {
            db.collection("users").document(email!!).update(
                "saved_posts", FieldValue.arrayRemove(postId)
            )
            binding.videoSave.setImageResource(R.drawable.save)
        } else {
            db.collection("users").document(email!!).update(
                "saved_posts", FieldValue.arrayUnion(postId)
            )
            binding.videoSave.setImageResource(R.drawable.save_selected)
        }
    }

    private fun setReactionIcons(user: FirebaseUser) {
        val isSaved = user.saved_posts.contains(postId)
        binding.videoSave.setImageResource(if (isSaved) R.drawable.save_selected else R.drawable.save)
        val reaction = user.reactions.find { it.post == postId }
        println("reaction: $reaction")
        if (reaction == null) {
            binding.videoLike.setImageResource(R.drawable.like)
            binding.videoIndifferent.setImageResource(R.drawable.indifferent)
            binding.videoDislike.setImageResource(R.drawable.dislike)
        }
        when (reaction?.type) {
            "likes" -> {
                binding.videoLike.setImageResource(R.drawable.like_selected)
                binding.videoIndifferent.setImageResource(R.drawable.indifferent)
                binding.videoDislike.setImageResource(R.drawable.dislike)
            }
            "indifferents" -> {
                binding.videoLike.setImageResource(R.drawable.like)
                binding.videoIndifferent.setImageResource(R.drawable.indifferent_selected)
                binding.videoDislike.setImageResource(R.drawable.dislike)
            }
            "dislikes" -> {
                binding.videoLike.setImageResource(R.drawable.like)
                binding.videoIndifferent.setImageResource(R.drawable.indifferent)
                binding.videoDislike.setImageResource(R.drawable.dislike_selected)
            }
        }
    }

    private fun fadeVideoAction() {
        val fadeOut = AlphaAnimation(1f, 0f)
        fadeOut.duration = 1000
        fadeOut.setAnimationListener(object: Animation.AnimationListener {
            override fun onAnimationStart(animation: Animation) {}
            override fun onAnimationEnd(animation: Animation) {
                binding.fullSizeVideoAction.visibility = ImageView.GONE // Oculta el elemento cuando finaliza la animación
            }
            override fun onAnimationRepeat(animation: Animation) {}
        })
        binding.fullSizeVideoAction.startAnimation(fadeOut)
    }

    private fun setup(videoContent: VideoPostMain, userName: String, email: String) {
        if (videoContent != null) {
            val videoView = binding.fullSizeVideoView

            videoView.setVideoURI(videoContent.video)
            videoView.requestFocus()
            videoView.setOnPreparedListener {
                // Get video size
                val videoWidth = it.videoWidth
                val videoHeight = it.videoHeight
                // Get device screen size
                val screenWidth = resources.displayMetrics.widthPixels
                val screenHeight = resources.displayMetrics.heightPixels
                // Get video scale proportion
                val scaleX = screenWidth.toFloat() / videoWidth.toFloat()
                val scaleY = screenHeight.toFloat() / videoHeight.toFloat()
                val scale = scaleX.coerceAtMost(scaleY)
                // Adjust video view to the video size
                val layoutParams = videoView.layoutParams
                layoutParams.width = (videoWidth * scale).toInt()
                layoutParams.height = (videoHeight * scale).toInt()
                videoView.layoutParams = layoutParams

                it.start()
                it.setOnCompletionListener {
                    finish()
                }
            }

            videoView.setOnClickListener {
                if (videoView.isPlaying) {
                    videoView.pause()
                    binding.fullSizeVideoAction.setImageResource(R.drawable.pause)
                    binding.fullSizeVideoAction.visibility = ImageView.VISIBLE
                } else {
                    binding.fullSizeVideoAction.setImageResource(R.drawable.play)
                    videoView.start()
                    fadeVideoAction()
                }
            }

            binding.fullSizeVideoAction.setOnClickListener {
                if (!videoView.isPlaying) {
                    binding.fullSizeVideoAction.setImageResource(R.drawable.play)
                    videoView.start()
                    fadeVideoAction()
                }
            }

            binding.fullSizeVideoBack.setOnClickListener {
                finish()
            }

            binding.videoCommentaries.setOnClickListener {
                val intent = Intent(this.baseContext, InnerVideoContentActivity::class.java)
                intent.putExtra("video_content", videoContent)
                startActivity(intent)
                overridePendingTransition(com.facebook.R.anim.abc_slide_in_bottom, com.facebook.R.anim.abc_slide_out_top)
            }

            binding.videoSave.setOnClickListener {
                savePost(email)
            }

            val reactionHandler = ReactionHandler()

            binding.videoLike.setOnClickListener {
                reactionHandler.addReaction(
                    email,
                    postId,
                    "likes",
                    userData,
                    postData,
                    db
                )
            }

            binding.videoIndifferent.setOnClickListener {
                reactionHandler.addReaction(
                    email,
                    postId,
                    "indifferents",
                    userData,
                    postData,
                    db
                )
            }

            binding.videoDislike.setOnClickListener {
                reactionHandler.addReaction(
                    email,
                    postId,
                    "dislikes",
                    userData,
                    postData,
                    db
                )
            }

        }
    }

    override fun onTouchEvent(event: MotionEvent?): Boolean {
        return gestureDetector.onTouchEvent(event!!) || super.onTouchEvent(event)
    }

    override fun onDown(e: MotionEvent): Boolean {
        return true
    }

    override fun onShowPress(e: MotionEvent) {}

    override fun onSingleTapUp(e: MotionEvent): Boolean {
        return false
    }

    override fun onScroll(
        e1: MotionEvent,
        e2: MotionEvent,
        distanceX: Float,
        distanceY: Float
    ): Boolean {
        return false
    }

    override fun onLongPress(e: MotionEvent) {}

    override fun onFling(
        e1: MotionEvent,
        e2: MotionEvent,
        velocityX: Float,
        velocityY: Float
    ): Boolean {
        if (e2.y < e1.y) {
            // Swipe from bottom to top detected
            // Start the new activity here
            val intent = Intent(this.baseContext, InnerVideoContentActivity::class.java)
            intent.putExtra("video_content", videoContent)
            startActivity(intent)
            overridePendingTransition(com.facebook.R.anim.abc_slide_in_bottom, com.facebook.R.anim.abc_slide_out_top)
            return true
        }
        return false
    }

}