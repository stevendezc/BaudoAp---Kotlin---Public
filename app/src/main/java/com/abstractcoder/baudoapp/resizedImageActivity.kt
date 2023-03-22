package com.abstractcoder.baudoapp

import android.net.Uri
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.abstractcoder.baudoapp.databinding.ActivityResizedImageBinding
import com.bumptech.glide.Glide

class resizedImageActivity : AppCompatActivity() {

    private lateinit var binding: ActivityResizedImageBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityResizedImageBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val imageUrl = intent.getParcelableExtra<Uri>("image")
        println("ImageUrl")
        println(imageUrl)

        setup(imageUrl!!)
    }

    private fun setup(imageUrl: Uri) {
        binding.resizedImageClose.setOnClickListener {
            finish()
        }

        Glide.with(binding.resizedImage)
            .load(imageUrl)
            .centerCrop()
            .into(binding.resizedImage)

    }
}