package com.abstractcoder.baudoapp.recyclers

import android.content.ContentValues
import android.graphics.Color
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.abstractcoder.baudoapp.R
import com.abstractcoder.baudoapp.databinding.CommunityListItemBinding
import com.bumptech.glide.Glide

class CommunityAdapter(private val communityList: ArrayList<CommunityMain>) : RecyclerView.Adapter<CommunityAdapter.CommunityHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CommunityHolder {
        val itemView = LayoutInflater.from(parent.context).inflate(
            R.layout.community_list_item,
            parent, false)
        return CommunityHolder(itemView)
    }

    override fun onBindViewHolder(holder: CommunityHolder, position: Int) {
        val currentItem = communityList[position]
        if (currentItem.thumbnail != null) {
            val imageUrl = currentItem.thumbnail
            Log.d(ContentValues.TAG, "Tumbnail => ${imageUrl}")
            Glide.with(holder.communityThumbnail)
                .load(imageUrl)
                .into(holder.communityThumbnail)
        }
        if (currentItem.category == "productivos") {
            holder.communityThumbnail.setStrokeColorResource(R.color.productivos)
            holder.communityBadge.setImageResource(R.drawable.productivos_badge)
            holder.communityTitle.setTextColor(
                ContextCompat.getColor(
                    holder.binding.root.context,
                    R.color.productivos
                )
            )
        }
        if (currentItem.category == "cultura") {
            holder.communityThumbnail.setStrokeColorResource(R.color.cultura)
            holder.communityBadge.setImageResource(R.drawable.cultura_badge)
            holder.communityTitle.setTextColor(
                ContextCompat.getColor(
                    holder.binding.root.context,
                    R.color.cultura
                )
            )
        }
        if (currentItem.category == "turismo") {
            holder.communityThumbnail.setStrokeColorResource(R.color.turismo)
            holder.communityBadge.setImageResource(R.drawable.turismo_badge)
            holder.communityTitle.setTextColor(
                ContextCompat.getColor(
                    holder.binding.root.context,
                    R.color.turismo
                )
            )
        }
        holder.communityTitle.text = currentItem.name + " " + currentItem.lastname
        holder.communityDescription.text = currentItem.description

    }

    override fun getItemCount(): Int {
        return communityList.size
    }

    class CommunityHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

        val binding = CommunityListItemBinding.bind(itemView)

        val communityThumbnail = binding.communityThumbnail
        val communityBadge = binding.communityBadge
        val communityTitle = binding.communityTitle
        val communityDescription = binding.communityDescription

    }

}