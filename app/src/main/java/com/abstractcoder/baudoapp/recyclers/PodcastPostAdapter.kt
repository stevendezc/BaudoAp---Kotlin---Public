package com.abstractcoder.baudoapp.recyclers

import android.content.ContentValues
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.abstractcoder.baudoapp.R
import com.bumptech.glide.Glide
import com.google.android.material.imageview.ShapeableImageView
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList

class PodcastPostAdapter(private val podcastPostList: ArrayList<PodcastPostMain>) : RecyclerView.Adapter<PodcastPostAdapter.PodcastPostHolder>() {

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
        //holder.imageThumbnail.setImageURI(currentItem.thumbnail)
        holder.podcastThumbnail.setImageResource(currentItem.thumbnail)
        holder.podcastTitle.text = currentItem.title
        val dateFormat = SimpleDateFormat("dd MMMM ',' yyyy", Locale("es", "ES"))
        val dateString = dateFormat.format(currentItem.timestamp.toDate())
        holder.podcastTimestamp.text = dateString
        holder.podcastDescription.text = currentItem.description
    }

    override fun getItemCount(): Int {
        return podcastPostList.size
    }

    class PodcastPostHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

        val podcastThumbnail: ShapeableImageView = itemView.findViewById(R.id.podcast_list_item_media)
        val podcastTitle: TextView = itemView.findViewById(R.id.podcast_list_item_title)
        val podcastTimestamp: TextView = itemView.findViewById(R.id.podcast_list_item_timestamp)
        val podcastDescription: TextView = itemView.findViewById(R.id.podcast_list_item_description)

    }

}