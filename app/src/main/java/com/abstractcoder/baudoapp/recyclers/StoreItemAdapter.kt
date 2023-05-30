package com.abstractcoder.baudoapp.recyclers

import android.content.ContentValues
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.abstractcoder.baudoapp.R
import com.abstractcoder.baudoapp.databinding.StoreListItemBinding
import com.abstractcoder.baudoapp.databinding.VideoListItemBinding
import com.bumptech.glide.Glide

class StoreItemAdapter(private val storeList: ArrayList<StoreItemMain>) : RecyclerView.Adapter<StoreItemAdapter.StoreItemHolder>() {
    var onItemClick : ((StoreItemMain) -> Unit)? = null

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): StoreItemHolder {
        val itemView = LayoutInflater.from(parent.context).inflate(
            R.layout.store_list_item,
            parent, false)
        return StoreItemHolder(itemView)
    }

    override fun onBindViewHolder(holder: StoreItemHolder, position: Int) {
        val currentItem = storeList[position]
        if (currentItem.thumbnail != null) {
            val imageUrl = currentItem.thumbnail
            Log.d(ContentValues.TAG, "Tumbnail => ${imageUrl}")
            Glide.with(holder.storeItemThumbnail)
                .load(imageUrl)
                .into(holder.storeItemThumbnail)
        }
        holder.storeItemTitle.text = currentItem.title

        holder.storeItemThumbnail.setOnClickListener {
            onItemClick?.invoke(currentItem)
        }
    }

    override fun getItemCount(): Int {
        return storeList.size
    }

    class StoreItemHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

        val binding = StoreListItemBinding.bind(itemView)

        val storeItemThumbnail = binding.storeItemImageView
        val storeItemTitle = binding.storeItemTitle

    }
}