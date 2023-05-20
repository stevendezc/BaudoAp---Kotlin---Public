package com.abstractcoder.baudoapp.recyclers

import android.content.ActivityNotFoundException
import android.content.ContentValues
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import androidx.core.content.ContextCompat
import androidx.core.content.ContextCompat.startActivity
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
            holder.communityTitle.setTextColor(
                ContextCompat.getColor(
                    holder.binding.root.context,
                    R.color.productivos
                )
            )
        }
        if (currentItem.category == "cultura") {
            holder.communityThumbnail.setStrokeColorResource(R.color.cultura)
            holder.communityTitle.setTextColor(
                ContextCompat.getColor(
                    holder.binding.root.context,
                    R.color.cultura
                )
            )
        }
        if (currentItem.category == "turismo") {
            holder.communityThumbnail.setStrokeColorResource(R.color.turismo)
            holder.communityTitle.setTextColor(
                ContextCompat.getColor(
                    holder.binding.root.context,
                    R.color.turismo
                )
            )
        }
        holder.communityTitle.text = currentItem.name + " " + currentItem.lastname
        holder.communityDescription.text = currentItem.description

        holder.communityFacebook.visibility = if (currentItem.facebook == "") Button.GONE else Button.VISIBLE
        holder.communityFacebook.setOnClickListener {
            openFacebook(holder.itemView.context, currentItem.facebook!!)
        }
        holder.communityInstagram.visibility = if (currentItem.instagram == "") Button.GONE else Button.VISIBLE
        holder.communityInstagram.setOnClickListener {
            openInstagram(holder.itemView.context, currentItem.instagram!!)
        }
        holder.communityWhatsapp.visibility = if (currentItem.whatsapp == "") Button.GONE else Button.VISIBLE
        holder.communityWhatsapp.setOnClickListener {
            openWhatsApp(holder.itemView.context, currentItem.whatsapp!!)
        }

    }

    fun openFacebook(context: Context, link: String) {
        val facebookPageId =  link.substringAfter("id=")
        try {
            val intent = Intent(Intent.ACTION_VIEW, Uri.parse("fb://profile/$facebookPageId"))
            startActivity(context, intent, null)
        } catch (e: Exception) {
            val intent = Intent(Intent.ACTION_VIEW, Uri.parse("https://www.facebook.com/$facebookPageId"))
            startActivity(context, intent, null)
        }
    }

    fun openInstagram(context: Context, link: String) {
        val uri = Uri.parse(link);
        val likeIng = Intent(Intent.ACTION_VIEW, uri);
        likeIng.setPackage("com.instagram.android");
        try {
            startActivity(context, likeIng, null);
        } catch (e: ActivityNotFoundException) {
            startActivity(context,Intent(Intent.ACTION_VIEW,
                Uri.parse(link)), null);
        }
    }

    fun openWhatsApp(context: Context, phoneNumber: String) {
        val countryIndicator = "57"
        val url = "https://wa.me/$countryIndicator$phoneNumber"
        val intent = Intent(Intent.ACTION_VIEW, Uri.parse(url))
        startActivity(context, intent, null)
    }

    override fun getItemCount(): Int {
        return communityList.size
    }

    class CommunityHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

        val binding = CommunityListItemBinding.bind(itemView)

        val communityThumbnail = binding.communityThumbnail
        val communityTitle = binding.communityTitle
        val communityDescription = binding.communityDescription

        val communityFacebook = binding.facebookButton
        val communityInstagram = binding.instagramButton
        val communityWhatsapp = binding.whatsappButton
        val communityTwitter = binding.twitterButton

    }

}