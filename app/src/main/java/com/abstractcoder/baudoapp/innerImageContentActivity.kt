package com.abstractcoder.baudoapp

import android.content.Intent
import android.graphics.Typeface
import android.os.Bundle
import android.text.Spannable
import android.text.SpannableString
import android.text.style.StyleSpan
import androidx.appcompat.app.AppCompatActivity
import com.abstractcoder.baudoapp.databinding.ActivityInnerImageContentBinding
import com.abstractcoder.baudoapp.recyclers.ImagePostMain
import com.bumptech.glide.Glide


class innerImageContentActivity : AppCompatActivity() {

    private lateinit var binding: ActivityInnerImageContentBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityInnerImageContentBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val imageContent = intent.getParcelableExtra<ImagePostMain>("image")

        setup(imageContent!!)
    }

    private fun setup(imageContent: ImagePostMain) {
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
        }
    }
}