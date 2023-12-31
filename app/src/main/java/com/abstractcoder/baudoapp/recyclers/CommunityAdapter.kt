package com.pereira.baudoapp.recyclers

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
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.core.content.ContextCompat.startActivity
import androidx.recyclerview.widget.RecyclerView
import com.pereira.baudoapp.R
import com.pereira.baudoapp.databinding.CommunityListItemBinding
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
            holder.communityLocation.setTextColor(
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
            holder.communityLocation.setTextColor(
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
            holder.communityLocation.setTextColor(
                ContextCompat.getColor(
                    holder.binding.root.context,
                    R.color.turismo
                )
            )
        }
        holder.communityTitle.text = currentItem.name + " " + currentItem.lastname
        holder.communityDescription.text = currentItem.description
        holder.communityLocation.text = currentItem.location

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

        holder.communityShare.setOnClickListener {
            val intent = Intent()
            val facebookPageId =  currentItem.facebook?.substringAfter("id=")
            intent.action = Intent.ACTION_SEND
            intent.type = "text/plain"
            intent.putExtra(Intent.EXTRA_SUBJECT, "${currentItem.name} ${currentItem.lastname}")
            intent.putExtra(Intent.EXTRA_TEXT, "${currentItem.name} ${currentItem.lastname}" +
                    "\n\n${currentItem.description}" +
                    "\n\nContacto: ${if (currentItem.whatsapp != "") currentItem.whatsapp else "Indefinido"}" +
                    "\nFacebook: ${if (currentItem.facebook != "") "https://www.facebook.com/$facebookPageId" else "Indefinido"}" +
                    "\nInstagram: ${if (currentItem.instagram != "") currentItem.instagram else "Indefinido"}")

            try {
                holder.itemView.context.startActivity(Intent.createChooser(intent, "Share via"))
            } catch (e: ActivityNotFoundException) {
                // Handle the case where the desired app is not installed
                Toast.makeText(holder.itemView.context, "Error compartiendo contenido... intente mas tarde", Toast.LENGTH_SHORT).show()
            }
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
        val communityLocation = binding.communityLocation

        val communityFacebook = binding.facebookButton
        val communityInstagram = binding.instagramButton
        val communityWhatsapp = binding.whatsappButton
        val communityTwitter = binding.twitterButton
        val communityShare = binding.shareButton

    }

}