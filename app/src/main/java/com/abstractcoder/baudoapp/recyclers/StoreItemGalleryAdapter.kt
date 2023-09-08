package com.pereira.baudoapp.recyclers

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.pereira.baudoapp.R
import com.pereira.baudoapp.databinding.SingleImageListItemBinding
import com.bumptech.glide.Glide

class StoreItemGalleryAdapter(private val imageList: ArrayList<String>) : RecyclerView.Adapter<StoreItemGalleryAdapter.StoreItemGalleryHolder>() {
    var onItemClick : ((StoreItemMain) -> Unit)? = null

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): StoreItemGalleryHolder {
        val itemView = LayoutInflater.from(parent.context).inflate(
            R.layout.single_image_list_item,
            parent, false)
        return StoreItemGalleryHolder(itemView)
    }

    override fun onBindViewHolder(holder: StoreItemGalleryHolder, position: Int) {
        val currentItem = imageList[position]
        Glide.with(holder.itemImage)
            .load(currentItem)
            .into(holder.itemImage)
    }

    override fun getItemCount(): Int {
        return imageList.size
    }

    class StoreItemGalleryHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

        val binding = SingleImageListItemBinding.bind(itemView)

        val itemImage = binding.singleImageListItemMedia

    }
}