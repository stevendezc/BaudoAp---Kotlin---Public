package com.abstractcoder.baudoapp

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.abstractcoder.baudoapp.databinding.ActivityFullSizeVideoBinding
import com.abstractcoder.baudoapp.recyclers.VideoPostMain

class FullSizeVideoActivity : AppCompatActivity() {

    private lateinit var binding: ActivityFullSizeVideoBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityFullSizeVideoBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val videoContent = intent.getParcelableExtra<VideoPostMain>("video")

        setup(videoContent!!)
    }

    private fun setup(videoContent: VideoPostMain) {
        if (videoContent != null) {
            val videoView = binding.fullSizeVideoView

            videoView.setVideoURI(videoContent.video)
            videoView.requestFocus()
            videoView.setOnPreparedListener {
                it.start()
                it.setOnCompletionListener {
                    finish()
                }
            }

            binding.fullSizeVideoBack.setOnClickListener {
                finish()
            }

            binding.fullSizeVideoCommentaries.setOnClickListener {
                val intent = Intent(it.context, InnerVideoContentActivity::class.java)
                intent.putExtra("video_content", videoContent)
                startActivity(intent)
            }
        }
    }
}