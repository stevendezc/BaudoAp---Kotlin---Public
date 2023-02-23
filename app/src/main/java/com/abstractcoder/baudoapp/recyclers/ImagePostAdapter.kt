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

class ImagePostAdapter(private val imagePostList: ArrayList<ImagePostMain>) : RecyclerView.Adapter<ImagePostAdapter.ImagePostHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ImagePostHolder {
        val itemView = LayoutInflater.from(parent.context).inflate(
            R.layout.image_list_item,
            parent, false)
        return ImagePostHolder(itemView)
    }

    override fun onBindViewHolder(holder: ImagePostHolder, position: Int) {
        val currentItem = imagePostList[position]
        if (currentItem.thumbnail != null) {
            val imageUrl = currentItem.thumbnail
            Log.d(ContentValues.TAG, "Tumbnail => ${imageUrl}")
            Glide.with(holder.imageThumbnail)
                .load(imageUrl)
                .into(holder.imageThumbnail)
        }
        holder.imageThumbnail.setImageURI(currentItem.thumbnail)
        //holder.imageThumbnail.setImageResource(currentItem.thumbnail)
        holder.imageAuthor.text = currentItem.author
        holder.imageDescription.text = currentItem.description
    }

    override fun getItemCount(): Int {
        return imagePostList.size
    }

    class ImagePostHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

        val imageThumbnail: ShapeableImageView = itemView.findViewById(R.id.image_list_item_media)
        val imageAuthor: TextView = itemView.findViewById(R.id.image_list_item_author)
        val imageDescription: TextView = itemView.findViewById(R.id.image_list_item_description)

    }

}