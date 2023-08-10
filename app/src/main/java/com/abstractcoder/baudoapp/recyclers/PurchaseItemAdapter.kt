package com.abstractcoder.baudoapp.recyclers

import android.content.ContentValues
import android.content.SharedPreferences
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.abstractcoder.baudoapp.R
import com.abstractcoder.baudoapp.databinding.ShoppingCartListItemBinding
import com.bumptech.glide.Glide
import com.google.gson.Gson
import java.text.DecimalFormat
import java.text.DecimalFormatSymbols
import java.util.*
import kotlin.collections.ArrayList

class PurchaseItemAdapter(private val storeList: ArrayList<PurchaseItemMain>, private val sharedPreferences: SharedPreferences) : RecyclerView.Adapter<PurchaseItemAdapter.PurchaseItemHolder>() {
    var onItemClick : ((PurchaseItemMain) -> Unit)? = null

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PurchaseItemHolder {
        val itemView = LayoutInflater.from(parent.context).inflate(
            R.layout.shopping_cart_list_item,
            parent, false)
        return PurchaseItemHolder(itemView)
    }

    override fun onBindViewHolder(holder: PurchaseItemHolder, position: Int) {
        val currentItem = storeList[position]
        if (currentItem.thumbnail != null) {
            val imageUrl = currentItem.thumbnail
            Log.d(ContentValues.TAG, "Tumbnail => ${imageUrl}")
            Glide.with(holder.purchaseItemMedia)
                .load(imageUrl)
                .into(holder.purchaseItemMedia)
        }
        holder.purchaseItemName.text = currentItem.name
        holder.purchaseItemPrice.text = "$${currentItem.price}"
        holder.purchaseItemQuantity.text = "Cantidad: ${currentItem.quantity}"

        val cleanedPriceString = currentItem.price?.replace(".", "")?.replace(",", "")
        val total = cleanedPriceString?.toInt()?.times(currentItem.quantity!!)
        val decimalFormatSymbols = DecimalFormatSymbols(Locale.getDefault())
        decimalFormatSymbols.groupingSeparator = '.'
        val decimalFormat = DecimalFormat("#,###", decimalFormatSymbols)
        val formattedTotal = decimalFormat.format(total?.toLong())
        holder.purchaseItemTotal.text = "$$formattedTotal"
        if (currentItem.size != "") {
            holder.purchaseItemSize.text = "Talla: ${currentItem.size}"
        }


        holder.purchaseItemDelete.setOnClickListener {
            storeList.remove(currentItem)
            val editor = sharedPreferences?.edit()
            val itemListString = Gson().toJson(storeList.toTypedArray())
            editor?.putString("itemList", itemListString)
            editor?.apply()
        }
    }

    override fun getItemCount(): Int {
        return storeList.size
    }

    class PurchaseItemHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

        val binding = ShoppingCartListItemBinding.bind(itemView)

        val purchaseItemMedia = binding.shoppingCartListItemMedia
        val purchaseItemName = binding.shoppingCartListItemName
        val purchaseItemPrice = binding.shoppingCartListItemPrice
        val purchaseItemQuantity = binding.shoppingCartListItemQuantity
        val purchaseItemTotal = binding.shoppingCartListItemTotal
        val purchaseItemSize = binding.shoppingCartListItemSize

        val purchaseItemDelete = binding.shoppingCartListItemDelete

    }
}