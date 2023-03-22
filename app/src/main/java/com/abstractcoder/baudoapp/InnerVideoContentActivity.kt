package com.abstractcoder.baudoapp

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.abstractcoder.baudoapp.databinding.ActivityInnerVideoContentBinding
import com.abstractcoder.baudoapp.recyclers.VideoPostMain
import com.bumptech.glide.Glide

class InnerVideoContentActivity : AppCompatActivity() {

    private lateinit var binding: ActivityInnerVideoContentBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityInnerVideoContentBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val videoContent = intent.getParcelableExtra<VideoPostMain>("video_content")

        println("videoContent")
        println(videoContent)

        setup(videoContent!!)
    }

    private fun setup(videoContent: VideoPostMain) {
        if (videoContent != null) {

            binding.innerVideoBack.setOnClickListener {
                finish()
            }

            val imageUrl = videoContent.thumbnail
            Glide.with(binding.innerVideoMedia)
                .load(imageUrl)
                .into(binding.innerVideoMedia)

            binding.innerVideoTitle.text = videoContent.title
            binding.innerVideoDescription.text = videoContent.description
            binding.innerVideoCategory.text = videoContent.category
        }
    }
}