package com.abstractcoder.baudoapp

import android.content.ContentValues
import android.content.Context
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

class StoreCheckOutActivity : AppCompatActivity() {

    private lateinit var binding: ActivityStoreCheckOutBinding

    private lateinit var layoutManager: LinearLayoutManager
    private lateinit var purchaseItemAdapter: PurchaseItemAdapter
    private lateinit var purchaseItemRecyclerView: RecyclerView
    private var purchaseItemMainList: ArrayList<PurchaseItemMain> = arrayListOf<PurchaseItemMain>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityStoreCheckOutBinding.inflate(layoutInflater)
        setContentView(binding.root)

        layoutManager = LinearLayoutManager(this.baseContext)

        fillPurchases()

        binding.backButton.setOnClickListener {
            finish()
        }
    }

    private fun fillPurchases() {

        val incomingPurchaseList = getShoppingCartItems()
        Log.d(ContentValues.TAG, "Init data")
        Log.d(ContentValues.TAG, "incomingPurchaseList: ${incomingPurchaseList.size}")
        purchaseItemMainList = arrayListOf<PurchaseItemMain>()
        for (post in incomingPurchaseList) {
            println("Purchase Item:")
            println(post)
            val storeItem = PurchaseItemMain(
                post.name,
                post.thumbnail,
                post.price,
                post.quantity,
                post.size
            )
            purchaseItemMainList.add(storeItem)
        }

        purchaseItemRecyclerView = binding.shoppingCartListRecycler
        purchaseItemRecyclerView.layoutManager = layoutManager
        purchaseItemAdapter = PurchaseItemAdapter(purchaseItemMainList)
        purchaseItemRecyclerView.adapter = purchaseItemAdapter

        binding.shoppingCartSubtotal.text = "Subtotal: $${getSubtotalFromShoppingCartItems()}"
    }

    private fun getSubtotalFromShoppingCartItems(): String {
        val incomingPurchaseList = getShoppingCartItems()
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

    private fun getShoppingCartItems(): MutableList<PurchaseItemMain> {
        val sharedPreferences = getSharedPreferences("shopping_cart", Context.MODE_PRIVATE)
        var itemListString = sharedPreferences?.getString("itemList", "") ?: ""
        val itemList = if (itemListString.isNotEmpty()) {
            Gson().fromJson(itemListString, Array<PurchaseItemMain>::class.java).toMutableList()
        } else {
            mutableListOf<PurchaseItemMain>()
        }
        println("Store item list on shared preferences")
        println(itemList)
        return itemList
    }
}