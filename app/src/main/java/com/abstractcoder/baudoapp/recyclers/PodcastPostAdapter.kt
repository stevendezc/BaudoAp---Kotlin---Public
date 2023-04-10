package com.abstractcoder.baudoapp.recyclers

import android.content.ContentValues
import android.content.Context
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.abstractcoder.baudoapp.R
import com.abstractcoder.baudoapp.databinding.PodcastListItemBinding
import com.bumptech.glide.Glide
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList

class PodcastPostAdapter(private val context: Context, private val podcastPostList: ArrayList<PodcastPostMain>) : RecyclerView.Adapter<PodcastPostAdapter.PodcastPostHolder>() {

    var onItemClick : ((PodcastPostMain) -> Unit)? = null

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
        val dateString = dateFormat.format(currentItem.creation_date?.toDate() ?: null)
        holder.podcastTimestamp.text = dateString
        holder.podcastDescription.text = currentItem.description

        holder.podcastContainer.setOnClickListener {
            onItemClick?.invoke(currentItem)
        }

    }

    override fun getItemCount(): Int {
        return podcastPostList.size
    }

    class PodcastPostHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

        val binding = PodcastListItemBinding.bind(itemView)

        val podcastContainer = binding.podcastListItemMediaContainer
        val podcastThumbnail = binding.podcastListItemMedia
        val podcastTitle = binding.podcastListItemTitle
        val podcastTimestamp = binding.podcastListItemTimestamp
        val podcastDescription = binding.podcastListItemDescription

    }

}