package com.pereira.baudoapp.utils

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
import com.pereira.baudoapp.R
import com.pereira.baudoapp.StoreCheckOutActivity
import com.pereira.baudoapp.databinding.StoreSingleItemDialogBinding
import com.pereira.baudoapp.recyclers.PurchaseItemMain
import com.bumptech.glide.Glide
import com.google.gson.Gson
import java.text.DecimalFormat
import java.text.DecimalFormatSymbols
import java.util.*

interface DialogListener {
    fun onDialogSubmit(success: Boolean)
}

class StoreDialog(
    private val directPurchase: Boolean,
    private val itemId: String,
    private val itemThumbnail: String,
    private val itemName: String,
    private val itemPrice: String,
    private val itemQuantity: Int,
    private val subtype: String,
    private val itemSize: String
): DialogFragment() {
    private lateinit var binding: StoreSingleItemDialogBinding
    private lateinit var listener: DialogListener

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

    override fun onAttach(context: Context) {
        super.onAttach(context)
        try {
            // Assign the reference of the hosting activity to the listener variable
            listener = context as DialogListener
        } catch (e: ClassCastException) {
            throw ClassCastException("$context must implement DialogListener")
        }
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
                val newItem = PurchaseItemMain(itemId, itemName, itemThumbnail, itemPrice, itemQuantity, subtype, itemSize)
                addItemToShoppingCart(newItem)
                val intent = Intent(activity, StoreCheckOutActivity::class.java)
                dismiss()
                startActivity(intent)
            } else {
                val newItem = PurchaseItemMain(itemId, itemName, itemThumbnail, itemPrice, itemQuantity, subtype, itemSize)
                addItemToShoppingCart(newItem)
                listener.onDialogSubmit(true)
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
        println("newItem: $newItem")
        val sharedPreferences = activity?.getSharedPreferences("shopping_cart", Context.MODE_PRIVATE)
        var itemListString = sharedPreferences?.getString("itemList", "") ?: ""
        var itemList = if (itemListString.isNotEmpty()) {
            Gson().fromJson(itemListString, Array<PurchaseItemMain>::class.java).toMutableList()
        } else {
            mutableListOf<PurchaseItemMain>()
        }

        val shoppingCartItems = getShoppingCartItems()
        println("itemId: $itemId")
        println("itemSize: $itemSize")
        val similarPurchase = shoppingCartItems.filter { it.id == itemId && it.subtype == subtype && it.size == itemSize }
        println("similarPurchase: $similarPurchase")
        println("itemList 0: $itemList")
        itemList.add(newItem)
        println("itemList 2: $itemList")

        val reducedItems = itemList.groupBy { it.id to it.size }
            .map { (key, group) ->
                val (id, size) = key
                val totalQuantity = group.sumBy { it.quantity!! }
                PurchaseItemMain(
                    id,
                    group.first().name,
                    group.first().thumbnail,
                    group.first().price,
                    totalQuantity,
                    size)
            }
        println("reducedItems: $reducedItems")
        itemList = reducedItems.toMutableList()
        println("itemList 3: $itemList")
        val editor = sharedPreferences?.edit()
        itemListString = Gson().toJson(itemList.toTypedArray())
        editor?.putString("itemList", itemListString)
        editor?.apply()
    }

    private fun getShoppingCartItems(): MutableList<PurchaseItemMain> {
        val sharedPreferences = activity?.getSharedPreferences("shopping_cart", Context.MODE_PRIVATE)
        var itemListString = sharedPreferences?.getString("itemList", "") ?: ""
        val itemList = if (itemListString.isNotEmpty()) {
            Gson().fromJson(itemListString, Array<PurchaseItemMain>::class.java).toMutableList()
        } else {
            mutableListOf<PurchaseItemMain>()
        }
        return itemList
    }
}