package com.abstractcoder.baudoapp

import android.content.ContentValues
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.inputmethod.InputMethodManager
import android.widget.FrameLayout
import android.widget.LinearLayout
import android.widget.ScrollView
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.abstractcoder.baudoapp.databinding.ActivityStoreCheckOutBinding
import com.abstractcoder.baudoapp.fragments.CheckoutContactFragment
import com.abstractcoder.baudoapp.fragments.CheckoutPolicyFragment
import com.abstractcoder.baudoapp.recyclers.PurchaseItemAdapter
import com.abstractcoder.baudoapp.recyclers.PurchaseItemMain
import com.abstractcoder.baudoapp.utils.CheckoutData
import com.abstractcoder.baudoapp.utils.EmailMessage
import com.abstractcoder.baudoapp.utils.PaymentDialog
import com.abstractcoder.baudoapp.utils.wompi.TransactionResponse
import com.google.android.gms.tasks.Tasks
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import com.google.gson.Gson
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import java.text.DecimalFormat
import java.text.DecimalFormatSymbols
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList
import kotlin.concurrent.thread

interface OnFragmentInteractionListener {
    fun onDataReceived(checkout_data: CheckoutData)
}

class StoreCheckOutActivity : AppCompatActivity(), SharedPreferences.OnSharedPreferenceChangeListener, OnFragmentInteractionListener {

    private lateinit var binding: ActivityStoreCheckOutBinding
    private val db = FirebaseFirestore.getInstance()

    private lateinit var sharedPreferences: SharedPreferences
    private lateinit var userSharedPreferences: SharedPreferences
    private var itemList: MutableList<PurchaseItemMain> = mutableListOf<PurchaseItemMain>()
    private var generalSubtotal: Long = 0
    private var email: String = ""

    private lateinit var layoutManager: LinearLayoutManager
    private lateinit var purchaseItemAdapter: PurchaseItemAdapter
    private lateinit var purchaseItemRecyclerView: RecyclerView
    private var purchaseItemMainList: ArrayList<PurchaseItemMain> = arrayListOf<PurchaseItemMain>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityStoreCheckOutBinding.inflate(layoutInflater)
        setContentView(binding.root)

        layoutManager = LinearLayoutManager(this.baseContext)

        val userSharedPreferences = getSharedPreferences(getString(R.string.prefs_file), Context.MODE_PRIVATE)
        email = userSharedPreferences.getString("email", "")!!

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
                item.subtype,
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

    private fun getCurrentDateTime(): String {
        val dateFormat = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault())

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            val timeZone = TimeZone.getTimeZone("America/Bogota") // Colombia's time zone
            dateFormat.timeZone = timeZone
        }

        return dateFormat.format(Date())
    }

    private fun addPrefixAndSuffixToProducts(inputList: List<String>, prefix: String, suffix: String): String {
        val modifiedList = inputList.map { "$prefix$it$suffix" }
        return modifiedList.joinToString("")
    }

    private fun addPurchaseReceipt(checkout_data: CheckoutData) {
        var transactionResponse = checkout_data.transactionResponse!!
        var contactInfo = checkout_data.contact_info!!
        var stringifiedProductList = arrayListOf<String>()
        val context = this
        val item_list = itemList
        println("item_list: $item_list")
        GlobalScope.launch(Dispatchers.IO) {
            println("itemList: $item_list")
            for (item in item_list) {
                println("item id: ${item.id!!}")
                val documentSnapshot = db.collection("productos").document(item.id!!).get().await()
                println("documentSnapshot: $documentSnapshot")
                println("exists: ${documentSnapshot.exists()}")
                if (documentSnapshot.exists()) {
                    var itemCurrentSize =
                        documentSnapshot.get("stock_${item.size?.lowercase()}") as Long
                    println("itemCurrentSize: $itemCurrentSize")
                    val updatedQuantity = itemCurrentSize - item.quantity!!
                    println("updatedQuantity: $updatedQuantity")
                    if (updatedQuantity < 0) {
                        stringifiedProductList.add("${item.quantity}X ${item.name} ${item.size} Agotado")
                    } else {
                        stringifiedProductList.add("${item.quantity}X ${item.name} ${item.size}")
                        decreaseItemStock(item, updatedQuantity, transactionResponse.data.reference)
                    }
                }
            }
            println("stringifiedProductList: $stringifiedProductList")
            val stringifiedProducts = stringifiedProductList.joinToString("; ")
            //val stringifiedProducts = itemList.joinToString("; ") { item -> "${item.quantity}X ${item.name} ${item.size}" }
            println("stringifiedProducts: $stringifiedProducts")
            db.collection("compras").document(transactionResponse.data.reference).set(
                hashMapOf(
                    "transaction_id" to transactionResponse.data.id,
                    "total_amount" to (transactionResponse.data.amount_in_cents / 100),
                    "payment_method" to transactionResponse.data.payment_method_type,
                    "transaction_date" to transactionResponse.data.created_at,
                    "transaction_status" to transactionResponse.data.status,
                    "products" to stringifiedProducts,
                    "buyer_name" to contactInfo.contact_name,
                    "buyer_address" to "${contactInfo.contact_address}, ${contactInfo.contact_city}",
                    "buyer_phone" to contactInfo.contact_phone,
                    "buyer_email" to contactInfo.contact_email
                )
            ).addOnSuccessListener {
                Toast.makeText(context, "Pago exitoso", Toast.LENGTH_SHORT).show()
            }

            val currentDate = getCurrentDateTime()
            val documentName = "${email}-$currentDate"
            val receivers = listOf<String>(email)
            val productsListItems = addPrefixAndSuffixToProducts(stringifiedProductList, "<li>", "</li>")
            val emailMessage = EmailMessage(
                subject = "Recibo de compra ${transactionResponse.data.id}",
                html = "<h2>Recibo de compra ${transactionResponse.data.id}</h2><br>" +
                        "<p>Estimado ${contactInfo.contact_name} la siguiente lista detalla los articulos que fueron solicitados a su nombre:</p><br>" +
                        "<ul>" +
                        productsListItems +
                        "</ul><br>" +
                        "<p>Por un valor total de <b>${(transactionResponse.data.amount_in_cents / 100)}</b></p><br><br>" +
                        "<p>Quedamos atentos ante cualquier inquietud</p><br>" +
                        "<img src='https://baudoap.com/wp-content/uploads/2017/10/logo.png'></img>"
            )
            db.collection("mail").document(documentName).set(
                mapOf(
                    "to" to receivers,
                    "message" to emailMessage
                )
            ).addOnSuccessListener {
                Toast.makeText(context, "Email de recibo enviado", Toast.LENGTH_SHORT).show()
            }.addOnFailureListener {
                Toast.makeText(context, "El email de recibo no se pudo enviar correctamente", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun decreaseItemStock(item: PurchaseItemMain, updatedQuantity: Long, transactionReference: String) {
        if (item.size != "") {
            db.collection("productos").document(item.id!!).update(
                "stock_${item.subtype?.lowercase()}_${item.size?.lowercase()}", updatedQuantity
            )
                .addOnSuccessListener {
                    println("Item ${item.id} ${item.name} actualizado")
                    db.collection("users").document(email).update(
                        "compras", FieldValue.arrayUnion(transactionReference)
                    )
                }
                .addOnFailureListener { e ->
                    println("Item ${item.id} ${item.name} no pudo ser actualizado")
                }
        } else {
            db.collection("productos").document(item.id!!).update(
                "stock", updatedQuantity
            )
                .addOnSuccessListener {
                    println("Item ${item.id} ${item.name} actualizado")
                    db.collection("users").document(email).update(
                        "compras", FieldValue.arrayUnion(transactionReference)
                    )
                }
                .addOnFailureListener { e ->
                    println("Item ${item.id} ${item.name} no pudo ser actualizado")
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
        if (checkout_data.type == "validation") {
            println("contact info on validation: ${checkout_data.contact_info}")
            var purchaseItems = arrayListOf<PurchaseItemMain>()
            purchaseItems.addAll(itemList)
            binding.checkoutFragmentWrapper.visibility = FrameLayout.GONE
            binding.purchaseItems.visibility = ScrollView.VISIBLE
            PaymentDialog(checkout_data, false, purchaseItems).show(supportFragmentManager, "user image dialog")
        }
        if (checkout_data.type == "valid_items") {
            println("contact info post validation: ${checkout_data.contact_info}")
            println("VALID ITEMS: ${checkout_data.validItems}")
            val sharedPreferences = getSharedPreferences("shopping_cart", Context.MODE_PRIVATE)
            val editor = sharedPreferences.edit()
            editor?.putString("itemList", Gson().toJson(checkout_data.validItems.toTypedArray()))
            editor?.apply()
            itemList = mutableListOf<PurchaseItemMain>()
            itemList.addAll(checkout_data.validItems)
            println("ITEM LIST: $itemList")
            Toast.makeText(this, "Items actualizados", Toast.LENGTH_SHORT).show()
            fillPurchases()
        }
        if (checkout_data.type == "payment") {
            binding.checkoutFragmentWrapper.visibility = FrameLayout.GONE
            binding.purchaseItems.visibility = ScrollView.VISIBLE
            println("contact info pre payment: ${checkout_data.contact_info}")
            PaymentDialog(checkout_data, true).show(supportFragmentManager, "user image dialog")
        }
        if (checkout_data.type == "payment_approved") {
            println("Transaction response on payment_approved: ${checkout_data.transactionResponse}")
            binding.checkoutFragmentWrapper.visibility = FrameLayout.GONE
            binding.purchaseItems.visibility = ScrollView.VISIBLE
            addPurchaseReceipt(checkout_data)
            val sharedPreferences = getSharedPreferences("shopping_cart", Context.MODE_PRIVATE)
            val editor = sharedPreferences.edit()
            editor.remove("itemList")
            editor.apply()
            itemList = mutableListOf<PurchaseItemMain>()
            fillPurchases()
        }
        if (checkout_data.type == "payment_declined") {
            println("Transaction response on payment_declined: ${checkout_data.transactionResponse}")
            binding.checkoutFragmentWrapper.visibility = FrameLayout.GONE
            binding.purchaseItems.visibility = ScrollView.VISIBLE
        }
    }
}