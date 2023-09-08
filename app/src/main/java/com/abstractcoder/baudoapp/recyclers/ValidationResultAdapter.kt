package com.pereira.baudoapp.recyclers

import android.content.Context
import android.content.res.ColorStateList
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.pereira.baudoapp.R
import com.pereira.baudoapp.databinding.ValidationListItemBinding


class ValidationResultAdapter(private val context: Context, private val validationResults: ArrayList<ValidationResultMain>, private val purchaseItems: ArrayList<PurchaseItemMain>): RecyclerView.Adapter<ValidationResultAdapter.ValidationHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ValidationHolder {
        val itemView = LayoutInflater.from(parent.context).inflate(
            R.layout.validation_list_item,
            parent, false)
        return ValidationHolder(itemView)
    }

    override fun onBindViewHolder(holder: ValidationResultAdapter.ValidationHolder, position: Int) {
        val currentItem = purchaseItems[position]
        val currentItemValidation = validationResults[position]
        if (currentItem != null) {
            holder.itemName.text = "${currentItem.name} ${currentItem.size} X${currentItem.quantity}"
            if (currentItemValidation.item_in_stock == true) {
                holder.itemStatusIcon.setImageResource(R.drawable.podcast_check)
                holder.itemStatusIcon.imageTintList = ColorStateList.valueOf(ContextCompat.getColor(context, R.color.white))
                holder.itemStatus.text = "En Stock"
            } else {
                holder.itemStatusIcon.setImageResource(R.drawable.warning)
                holder.itemStatusIcon.imageTintList = ColorStateList.valueOf(ContextCompat.getColor(context, R.color.white))
                //holder.itemStatusIcon.
                holder.itemStatus.text = "Fuera de Stock"
            }
        }
    }

    override fun getItemCount(): Int {
        return validationResults.size
    }

    class ValidationHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

        val binding = ValidationListItemBinding.bind(itemView)
        val itemName = binding.itemName
        val itemStatusIcon = binding.itemStatusIcon
        val itemStatus = binding.itemStatus

    }
}