package com.abstractcoder.baudoapp.recyclers

import android.annotation.SuppressLint
import android.content.ContentValues
import android.content.Context
import android.media.MediaPlayer
import android.os.Handler
import android.os.Message
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.SeekBar
import androidx.recyclerview.widget.RecyclerView
import com.abstractcoder.baudoapp.R
import com.abstractcoder.baudoapp.databinding.PodcastListItemBinding
import com.bumptech.glide.Glide
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList

class PodcastPostAdapter(private val context: Context, private val podcastPostList: ArrayList<PodcastPostMain>) : RecyclerView.Adapter<PodcastPostAdapter.PodcastPostHolder>() {

    lateinit var runnable: Runnable
    private var handler = Handler()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PodcastPostHolder {
        val itemView = LayoutInflater.from(parent.context).inflate(
            R.layout.podcast_list_item,
            parent, false)
        return PodcastPostHolder(itemView)
    }

    override fun onBindViewHolder(holder: PodcastPostHolder, position: Int) {
        val currentItem = podcastPostList[position]
        if (currentItem.thumbnail != null) {
            val imageUrl = currentItem.thumbnail
            Log.d(ContentValues.TAG, "Tumbnail => ${imageUrl}")
            Glide.with(holder.podcastThumbnail)
                .load(imageUrl)
                .into(holder.podcastThumbnail)
        }
        holder.podcastTitle.text = currentItem.title
        val dateFormat = SimpleDateFormat("dd MMMM ',' yyyy", Locale("es", "ES"))
        val dateString = dateFormat.format(currentItem.timestamp.toDate())
        holder.podcastTimestamp.text = dateString
        holder.podcastDescription.text = currentItem.description

        val podcastMedia = MediaPlayer.create(context, currentItem.media)

        // Play button Event
        holder.podcastPlay.setOnClickListener {
            if (!podcastMedia.isPlaying) {
                podcastMedia.start()
                holder.podcastPlay.setImageResource(R.drawable.pause)
            } else {
                podcastMedia.pause()
                holder.podcastPlay.setImageResource(R.drawable.play)
            }
        }
        // Seekbar functionalities
        holder.podcastSeekbar.progress = 0
        holder.podcastSeekbar.max = podcastMedia.duration
        holder.podcastSeekbar.setOnSeekBarChangeListener(object: SeekBar.OnSeekBarChangeListener{
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
                holder.podcastSeekbar.progress = currentPosition
            }
        }

    }

    override fun getItemCount(): Int {
        return podcastPostList.size
    }

    class PodcastPostHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

        val binding = PodcastListItemBinding.bind(itemView)

        val podcastThumbnail = binding.podcastListItemMedia
        val podcastTitle = binding.podcastListItemTitle
        val podcastTimestamp = binding.podcastListItemTimestamp
        val podcastDescription = binding.podcastListItemDescription
        val podcastPlay = binding.podcastListItemPlay
        val podcastSeekbar = binding.podcastListItemSeekbar

    }

}