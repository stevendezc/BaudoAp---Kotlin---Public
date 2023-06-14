package com.abstractcoder.baudoapp.utils

import android.app.Dialog
import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.view.Gravity
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.DialogFragment
import com.abstractcoder.baudoapp.R
import com.abstractcoder.baudoapp.StoreCheckOutActivity
import com.abstractcoder.baudoapp.databinding.StoreSingleItemDialogBinding
import com.abstractcoder.baudoapp.recyclers.PurchaseItemMain
import com.bumptech.glide.Glide
import com.google.gson.Gson
import java.text.DecimalFormat
import java.text.DecimalFormatSymbols
import java.util.*

class StoreDialog(
    private val directPurchase: Boolean,
    private val itemThumbnail: String,
    private val itemName: String,
    private val itemPrice: String,
    private val itemQuantity: Int,
    private val itemSize: String
): DialogFragment() {
    private lateinit var binding: StoreSingleItemDialogBinding

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        super.onCreate(savedInstanceState)
        binding = StoreSingleItemDialogBinding.inflate(layoutInflater)

        val builder = AlertDialog.Builder(requireActivity())
        builder.setView(binding.root)

        setup()

        val dialog = builder.create()
        dialog.window!!.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        dialog.window!!.setGravity(Gravity.BOTTOM)
        dialog.window!!.attributes.windowAnimations = R.style.DialogAnimation
        return dialog
    }

    private fun setup() {
        binding.purchaseButton.text = if (directPurchase) "Comprar ahora" else "Agregar al carrito"

        Glide.with(binding.storeItemImageView)
            .load(itemThumbnail)
            .into(binding.storeItemImageView)

        binding.productName.text = itemName
        binding.productPrice.text = "$$itemPrice"
        binding.productQuantity.text = "Cantidad: $itemQuantity"

        val cleanedPriceString = itemPrice.replace(".", "").replace(",", "")
        val total = cleanedPriceString.toInt() * itemQuantity
        val decimalFormatSymbols = DecimalFormatSymbols(Locale.getDefault())
        decimalFormatSymbols.groupingSeparator = '.'
        val decimalFormat = DecimalFormat("#,###", decimalFormatSymbols)
        val formattedTotal = decimalFormat.format(total.toLong())
        binding.productTotal.text = "$$formattedTotal"

        binding.productSize.text = "Talla: $itemSize"

        binding.purchaseButton.setOnClickListener {
            if (directPurchase) {
                val newItem = PurchaseItemMain(itemName, itemThumbnail, itemPrice, itemQuantity, itemSize)
                addItemToShoppingCart(newItem)
                val intent = Intent(activity, StoreCheckOutActivity::class.java)
                dismiss()
                startActivity(intent)
            } else {
                val newItem = PurchaseItemMain(itemName, itemThumbnail, itemPrice, itemQuantity, itemSize)
                addItemToShoppingCart(newItem)
                dismiss()
                Toast.makeText(
                    this.context,
                    "Producto agregado al carrito",
                    Toast.LENGTH_LONG
                ).show()
            }
        }
    }

    private fun addItemToShoppingCart(newItem: PurchaseItemMain) {
        val sharedPreferences = activity?.getSharedPreferences("shopping_cart", Context.MODE_PRIVATE)
        var itemListString = sharedPreferences?.getString("itemList", "") ?: ""
        val itemList = if (itemListString.isNotEmpty()) {
            Gson().fromJson(itemListString, Array<PurchaseItemMain>::class.java).toMutableList()
        } else {
            mutableListOf<PurchaseItemMain>()
        }
        itemList.add(newItem)
        val editor = sharedPreferences?.edit()
        itemListString = Gson().toJson(itemList.toTypedArray())
        editor?.putString("itemList", itemListString)
        editor?.apply()
    }
}