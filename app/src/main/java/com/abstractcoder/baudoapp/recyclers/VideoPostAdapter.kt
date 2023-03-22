package com.abstractcoder.baudoapp.recyclers

import android.content.ContentValues
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.abstractcoder.baudoapp.R
import com.abstractcoder.baudoapp.databinding.VideoListItemBinding
import com.bumptech.glide.Glide

class VideoPostAdapter(private val videoPostList: ArrayList<VideoPostMain>) : RecyclerView.Adapter<VideoPostAdapter.VideoPostHolder>() {

    var onItemClick : ((VideoPostMain) -> Unit)? = null

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): VideoPostHolder {
        val itemView = LayoutInflater.from(parent.context).inflate(
            R.layout.video_list_item,
            parent, false)
        return VideoPostHolder(itemView)
    }

    override fun onBindViewHolder(holder: VideoPostHolder, position: Int) {
        val currentItem = videoPostList[position]
        if (currentItem.thumbnail != null) {
            val imageUrl = currentItem.thumbnail
            Log.d(ContentValues.TAG, "Tumbnail => ${imageUrl}")
            Glide.with(holder.imageThumbnail)
                .load(imageUrl)
                .into(holder.imageThumbnail)
        }

        holder.imageThumbnail.setOnClickListener {
            onItemClick?.invoke(currentItem)
        }
    }

    override fun getItemCount(): Int {
        return videoPostList.size
    }

    class VideoPostHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

        val binding = VideoListItemBinding.bind(itemView)

        val imageThumbnail = binding.videoListItemMedia

    }
}