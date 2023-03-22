package com.abstractcoder.baudoapp

import android.annotation.SuppressLint
import android.graphics.drawable.Drawable
import android.media.MediaPlayer
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.os.Message
import android.widget.SeekBar
import com.abstractcoder.baudoapp.databinding.ActivityInnerPodcastContentBinding
import com.abstractcoder.baudoapp.recyclers.PodcastPostMain
import com.bumptech.glide.Glide
import com.bumptech.glide.request.target.CustomTarget
import java.text.SimpleDateFormat
import java.util.*

class InnerPodcastContentActivity : AppCompatActivity() {

    lateinit var runnable: Runnable
    private var handler = Handler()
    lateinit var podcastMedia: MediaPlayer

    private lateinit var binding: ActivityInnerPodcastContentBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityInnerPodcastContentBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val podcastContent = intent.getParcelableExtra<PodcastPostMain>("podcast")

        setup(podcastContent!!)
    }

    private fun setup(podcastContent: PodcastPostMain) {
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
    }

    override fun onDestroy() {
        super.onDestroy()
        podcastMedia.stop()
    }
}