package com.pereira.baudoapp

import android.graphics.drawable.Drawable
import android.net.Uri
import android.os.Bundle
import android.view.ViewTreeObserver
import androidx.appcompat.app.AppCompatActivity
import com.pereira.baudoapp.databinding.ActivityResizedImageBinding
import com.bumptech.glide.Glide
import com.bumptech.glide.load.DataSource
import com.bumptech.glide.load.engine.GlideException
import com.bumptech.glide.request.RequestListener
import com.bumptech.glide.request.target.Target

class resizedImageActivity : AppCompatActivity() {

    private lateinit var binding: ActivityResizedImageBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityResizedImageBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val imageUrl = intent.getParcelableExtra<Uri>("image")

        setup(imageUrl!!)
    }

    private fun setup(imageUrl: Uri) {
        binding.resizedImageClose.setOnClickListener {
            finish()
        }

        Glide.with(binding.resizedImage)
            .load(imageUrl)
            .listener(object : RequestListener<Drawable> {
                override fun onLoadFailed(
                    e: GlideException?,
                    model: Any?,
                    target: Target<Drawable>?,
                    isFirstResource: Boolean
                ): Boolean {
                    // handle failed image loading here
                    return false
                }
                override fun onResourceReady(
                    resource: Drawable?,
                    model: Any?,
                    target: Target<Drawable>?,
                    dataSource: DataSource?,
                    isFirstResource: Boolean
                ): Boolean {
                    val imageWidth = resource?.intrinsicWidth
                    val scrollViewWidth = binding.imageScrollView.width
                    val desiredXPosition = (imageWidth!! / 2) - (scrollViewWidth / 2)
                    binding.resizedImage.viewTreeObserver.addOnGlobalLayoutListener(object :
                        ViewTreeObserver.OnGlobalLayoutListener {
                        override fun onGlobalLayout() {
                            // Remove the listener to avoid multiple calls
                            binding.resizedImage.viewTreeObserver.removeOnGlobalLayoutListener(this)
                            // Get the x-coordinate of the middle of the image view
                            val middleX = desiredXPosition
                            // Scroll to the middle of the image view
                            binding.imageScrollView.scrollTo(middleX.toInt(), 0)
                        }
                    })
                    return false
                }
            })
            .into(binding.resizedImage)
    }
}