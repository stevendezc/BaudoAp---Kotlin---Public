package com.abstractcoder.baudoapp

import android.content.ContentValues
import android.content.Intent
import android.content.SharedPreferences
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.abstractcoder.baudoapp.databinding.ActivityStoreCheckOutBinding
import com.abstractcoder.baudoapp.recyclers.PurchaseItemAdapter
import com.abstractcoder.baudoapp.recyclers.PurchaseItemMain
import com.google.gson.Gson
import java.text.DecimalFormat
import java.text.DecimalFormatSymbols
import java.util.*
import kotlin.collections.ArrayList

class StoreCheckOutActivity : AppCompatActivity(), SharedPreferences.OnSharedPreferenceChangeListener {

    private lateinit var binding: ActivityStoreCheckOutBinding

    private lateinit var sharedPreferences: SharedPreferences
    private var itemList: MutableList<PurchaseItemMain> = mutableListOf<PurchaseItemMain>()

    private lateinit var layoutManager: LinearLayoutManager
    private lateinit var purchaseItemAdapter: PurchaseItemAdapter
    private lateinit var purchaseItemRecyclerView: RecyclerView
    private var purchaseItemMainList: ArrayList<PurchaseItemMain> = arrayListOf<PurchaseItemMain>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityStoreCheckOutBinding.inflate(layoutInflater)
        setContentView(binding.root)

        layoutManager = LinearLayoutManager(this.baseContext)

        // Get reference to SharedPreferences
        sharedPreferences = getSharedPreferences("shopping_cart", MODE_PRIVATE)
        // Register the listener
        sharedPreferences.registerOnSharedPreferenceChangeListener(this)

        setUpPurchaseSystem()
        getShoppingCartItems(sharedPreferences, "itemList")
        fillPurchases()

        binding.backButton.setOnClickListener {
            val resultIntent = Intent()
            resultIntent.putExtra("newData", "New Value")
            setResult(2, resultIntent)
            finish()
        }
    }

    override fun onSharedPreferenceChanged(sharedPreferences: SharedPreferences, key: String) {
        if (key == "itemList") {
            // Handle the update of the specific preference key
            getShoppingCartItems(sharedPreferences, key)
            fillPurchases()
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        // Unregister the listener to avoid memory leaks
        sharedPreferences.unregisterOnSharedPreferenceChangeListener(this)
    }

    private fun setUpPurchaseSystem() {
    }

    private fun fillPurchases() {

        val incomingPurchaseList = itemList
        Log.d(ContentValues.TAG, "Init data")
        Log.d(ContentValues.TAG, "incomingPurchaseList: ${incomingPurchaseList.size}")
        purchaseItemMainList = arrayListOf<PurchaseItemMain>()
        for (item in incomingPurchaseList) {
            val storeItem = PurchaseItemMain(
                item.id,
                item.name,
                item.thumbnail,
                item.price,
                item.quantity,
                item.size
            )
            purchaseItemMainList.add(storeItem)
        }

        if (incomingPurchaseList.size == 0) {
            binding.shoppingCartLabel.text = "Tu carrito esta vacio"
            binding.shoppingCartListRecycler.visibility = RecyclerView.GONE
            binding.shoppingCartSubtotal.text = "No tienes items para facturar, regresa a la tienda"
            binding.purchaseButton.text = "Volver a la tienda"
            binding.purchaseButton.setOnClickListener {
                val resultIntent = Intent()
                resultIntent.putExtra("newData", "New Value")
                setResult(2, resultIntent)
                finish()
            }
        } else {
            purchaseItemRecyclerView = binding.shoppingCartListRecycler
            purchaseItemRecyclerView.layoutManager = layoutManager
            purchaseItemAdapter = PurchaseItemAdapter(purchaseItemMainList, sharedPreferences)
            purchaseItemRecyclerView.adapter = purchaseItemAdapter

            binding.shoppingCartSubtotal.text = "Subtotal: $${getSubtotalFromShoppingCartItems(incomingPurchaseList)}"
        }
    }

    private fun getSubtotalFromShoppingCartItems(incomingPurchaseList: MutableList<PurchaseItemMain>): String {
        var subtotal: Long = 0
        for (item in incomingPurchaseList) {
            val cleanedPriceString = item.price?.replace(".", "")?.replace(",", "")
            val total = cleanedPriceString?.toInt()?.times(item.quantity!!)
            if (total != null) {
                subtotal += total.toLong()
            }
        }
        val decimalFormatSymbols = DecimalFormatSymbols(Locale.getDefault())
        decimalFormatSymbols.groupingSeparator = '.'
        val decimalFormat = DecimalFormat("#,###", decimalFormatSymbols)
        return decimalFormat.format(subtotal)
    }

    private fun getShoppingCartItems(sharedPreferences: SharedPreferences, key: String) {
        var itemListString = sharedPreferences?.getString(key, "") ?: ""
        itemList = if (itemListString.isNotEmpty()) {
            Gson().fromJson(itemListString, Array<PurchaseItemMain>::class.java).toMutableList()
        } else {
            mutableListOf<PurchaseItemMain>()
        }
    }
}