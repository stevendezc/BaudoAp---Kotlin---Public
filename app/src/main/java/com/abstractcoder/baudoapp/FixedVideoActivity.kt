package com.abstractcoder.baudoapp

import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
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

        val link = "https://firebasestorage.googleapis.com/v0/b/pereira-71536.appspot.com/o/videos%2Fvideo.mp4?alt=media&token=5e8a8c0a-23cd-4289-b3a3-451a96e90945"

        binding.fixedVideoBack.setOnClickListener {
            finish()
        }

        val videoView = binding.videoView

        videoView.setVideoURI(Uri.parse(link))
        videoView.requestFocus()
        videoView.setOnPreparedListener {
            it.start()
            it.setOnCompletionListener {
                finish()
            }
        }

    }
}