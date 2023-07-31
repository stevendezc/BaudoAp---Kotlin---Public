package com.abstractcoder.baudoapp

import android.app.Activity
import android.content.ContentValues
import android.content.Intent
import android.content.SharedPreferences
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.FrameLayout
import android.widget.LinearLayout
import android.widget.ScrollView
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.abstractcoder.baudoapp.databinding.ActivityStoreCheckOutBinding
import com.abstractcoder.baudoapp.fragments.CheckoutContactFragment
import com.abstractcoder.baudoapp.fragments.CheckoutPaymentFragment
import com.abstractcoder.baudoapp.fragments.CheckoutPolicyFragment
import com.abstractcoder.baudoapp.recyclers.PurchaseItemAdapter
import com.abstractcoder.baudoapp.recyclers.PurchaseItemMain
import com.abstractcoder.baudoapp.utils.CheckoutData
import com.abstractcoder.baudoapp.utils.PaymentDialog
import com.abstractcoder.baudoapp.utils.UserImageDialog
import com.google.gson.Gson
import java.text.DecimalFormat
import java.text.DecimalFormatSymbols
import java.util.*
import kotlin.collections.ArrayList

interface OnFragmentInteractionListener {
    fun onDataReceived(checkout_data: CheckoutData)
}

class StoreCheckOutActivity : AppCompatActivity(), SharedPreferences.OnSharedPreferenceChangeListener, OnFragmentInteractionListener {

    private lateinit var binding: ActivityStoreCheckOutBinding

    private lateinit var sharedPreferences: SharedPreferences
    private var itemList: MutableList<PurchaseItemMain> = mutableListOf<PurchaseItemMain>()
    private var generalSubtotal: Long = 0

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

    private fun makeCurrentFragment(fragment: Fragment) {
        supportFragmentManager.beginTransaction().apply {
            replace(R.id.checkoutFragmentWrapper, fragment)
            addToBackStack(null)
            commit()
        }
        binding.checkoutFragmentWrapper.visibility = FrameLayout.VISIBLE
        binding.purchaseItems.visibility = ScrollView.GONE
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
            binding.purchaseButton.setOnClickListener {
                makeCurrentFragment(CheckoutContactFragment())
            }
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
        generalSubtotal = subtotal
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

    override fun onBackPressed() {
    }

    override fun onDataReceived(checkout_data: CheckoutData) {
        println("Activity checkout_data: $checkout_data")
        println("Activity checkout_data type: ${checkout_data.type}")
        checkout_data.subtotal = generalSubtotal
        if (checkout_data.type == "back") {
            binding.checkoutFragmentWrapper.visibility = FrameLayout.GONE
            binding.purchaseItems.visibility = LinearLayout.VISIBLE
        }
        if (checkout_data.type == "policy") {
            val fragment = CheckoutPolicyFragment.newInstance(checkout_data)
            makeCurrentFragment(fragment)
        }
        if (checkout_data.type == "contact") {
            val fragment = CheckoutContactFragment.newInstance(checkout_data)
            makeCurrentFragment(fragment)
        }
        if (checkout_data.type == "payment") {
            // val fragment = CheckoutPaymentFragment.newInstance(checkout_data)
            // makeCurrentFragment(fragment)
            binding.checkoutFragmentWrapper.visibility = FrameLayout.GONE
            binding.purchaseItems.visibility = ScrollView.VISIBLE
            PaymentDialog(checkout_data).show(supportFragmentManager, "user image dialog")
        }
        if (checkout_data.type == "cc_submit") {
            binding.checkoutFragmentWrapper.visibility = FrameLayout.GONE
            binding.purchaseItems.visibility = ScrollView.VISIBLE
            PaymentDialog(checkout_data).show(supportFragmentManager, "user image dialog")
        }
        if (checkout_data.type == "pse_submit") {
            binding.checkoutFragmentWrapper.visibility = FrameLayout.GONE
            binding.purchaseItems.visibility = ScrollView.VISIBLE
            PaymentDialog(checkout_data).show(supportFragmentManager, "user image dialog")
        }
    }
}