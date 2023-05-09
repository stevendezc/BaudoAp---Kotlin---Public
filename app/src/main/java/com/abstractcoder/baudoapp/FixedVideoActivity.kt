package com.abstractcoder.baudoapp

import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.animation.AlphaAnimation
import android.view.animation.Animation
import android.widget.ImageView
import android.widget.Toast
import com.abstractcoder.baudoapp.databinding.ActivityFixedVideoBinding
import com.google.android.exoplayer2.ExoPlayer
import com.google.android.exoplayer2.MediaItem
import com.google.android.exoplayer2.PlaybackException
import com.google.android.exoplayer2.Player
import com.google.android.exoplayer2.ui.StyledPlayerView

class FixedVideoActivity : AppCompatActivity() {

    private lateinit var binding: ActivityFixedVideoBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityFixedVideoBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setup()
    }

    private fun setup() {

        val link = "https://firebasestorage.googleapis.com/v0/b/baudoapp-c89ed.appspot.com/o/Fijo%2FBaud%C3%B3%20AP%20-%20Naveguemos%20juntos.mp4?alt=media&token=9d5ae5d6-9fea-46a0-98a7-b6e1d301f0e3"

        binding.fixedVideoBack.setOnClickListener {
            finish()
        }

        val videoView = binding.videoView

        videoView.setVideoURI(Uri.parse(link))
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
                binding.fullSizeVideoAction.setImageResource(R.drawable.pause)
                binding.fullSizeVideoAction.visibility = ImageView.VISIBLE
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
}