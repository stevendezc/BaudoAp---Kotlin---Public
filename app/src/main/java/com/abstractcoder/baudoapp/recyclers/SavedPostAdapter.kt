package com.abstractcoder.baudoapp.recyclers

import android.content.ContentValues
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.abstractcoder.baudoapp.R
import com.abstractcoder.baudoapp.databinding.SavedPostListItemBinding
import com.bumptech.glide.Glide

class SavedPostAdapter(private val savedPostList: ArrayList<SavedPostMain>) : RecyclerView.Adapter<SavedPostAdapter.SavedPostHolder>() {
    var onItemClick : ((SavedPostMain) -> Unit)? = null

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SavedPostHolder {
        val itemView = LayoutInflater.from(parent.context).inflate(
            R.layout.saved_post_list_item,
            parent, false)
        return SavedPostHolder(itemView)
    }

    override fun onBindViewHolder(holder: SavedPostHolder, position: Int) {
        val currentItem = savedPostList[position]
        if (currentItem.thumbnail != null) {
            val imageUrl = currentItem.thumbnail
            Log.d(ContentValues.TAG, "Tumbnail => ${imageUrl}")
            Glide.with(holder.savedPostThumbnail)
                .load(imageUrl)
                .into(holder.savedPostThumbnail)
        }
        holder.savedPostTitle.text = currentItem.title

        holder.savedPostSave.setImageResource(R.drawable.save_selected)
        holder.savedPostLike.setImageResource(if (currentItem.liked!!) R.drawable.like_selected else R.drawable.like)
        holder.savedPostIndifferent.setImageResource(if (currentItem.indifferent!!) R.drawable.indifferent_selected else R.drawable.indifferent)
        holder.savedPostDislike.setImageResource(if (currentItem.disliked!!) R.drawable.dislike_selected else R.drawable.dislike)

        holder.savedPostContainer.setOnClickListener {
            onItemClick?.invoke(currentItem)
        }
    }

    override fun getItemCount(): Int {
        return savedPostList.size
    }

    class SavedPostHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

        val binding = SavedPostListItemBinding.bind(itemView)

        val savedPostContainer = binding.savedPostListItemMediaContainer
        val savedPostThumbnail = binding.savedPostListItemMedia
        val savedPostTitle = binding.savedPostListItemTitle
        val savedPostSave = binding.savedPostSave
        val savedPostLike = binding.savedPostLike
        val savedPostIndifferent = binding.savedPostIndifferent
        val savedPostDislike = binding.savedPostDislike

    }
}