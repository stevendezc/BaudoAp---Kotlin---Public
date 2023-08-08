package com.abstractcoder.baudoapp.utils

import android.app.Activity
import android.app.Dialog
import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.view.Gravity
import android.widget.Button
import android.widget.ImageView
import android.widget.ProgressBar
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.FragmentActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.abstractcoder.baudoapp.BrowserActivity
import com.abstractcoder.baudoapp.OnFragmentInteractionListener
import com.abstractcoder.baudoapp.R
import com.abstractcoder.baudoapp.databinding.PaymentDialogBinding
import com.abstractcoder.baudoapp.recyclers.*
import com.abstractcoder.baudoapp.utils.API.PostsService
import com.abstractcoder.baudoapp.utils.API.PostsServiceImpl
import com.abstractcoder.baudoapp.utils.wompi.*
import com.google.android.gms.tasks.Tasks
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlin.concurrent.thread

class PaymentDialog(
    private val paymentData: CheckoutData,
    private val isPayment: Boolean,
    private var purchaseItems: ArrayList<PurchaseItemMain>? = arrayListOf<PurchaseItemMain>()
): DialogFragment() {
    private lateinit var binding: PaymentDialogBinding
    private val apiClient = PostsServiceImpl()
    private val db = FirebaseFirestore.getInstance()

    private lateinit var layoutManager: LinearLayoutManager
    private lateinit var validationAdapter: ValidationResultAdapter
    private lateinit var validationRecyclerView: RecyclerView
    private var validationMainList: ArrayList<ValidationResultMain> = arrayListOf<ValidationResultMain>()

    private var listener: OnFragmentInteractionListener? = null
    private lateinit var client: PostsService
    val REQUEST_CODE_ACTIVITY = 1

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        super.onCreate(savedInstanceState)
        isCancelable = false
        binding = PaymentDialogBinding.inflate(layoutInflater)
        println("paymentData in dialog: ${paymentData}")

        val builder = AlertDialog.Builder(requireActivity())
        builder.setView(binding.root)
        layoutManager = LinearLayoutManager(context)

        if (isPayment) {
            println("paymentData contact initializePayment: ${paymentData.contact_info}")
            initializePayment()
        } else {
            println("paymentData contact validateItems: ${paymentData.contact_info}")
            validateItems()
        }

        val dialog = builder.create()
        dialog.window!!.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        dialog.window!!.setGravity(Gravity.TOP)
        dialog.window!!.attributes.windowAnimations = R.style.DialogAnimation
        dialog.setCanceledOnTouchOutside(false)
        return dialog
    }

    private fun generateRandomAlphaNumericString(length: Int): String {
        val charset = ('A'..'Z') + ('a'..'z') + ('0'..'9')
        return (1..length)
            .map { charset.random() }
            .joinToString("")
    }

    private fun validateItems() {
        binding.loadingText.text = "Validando Items"
        validationMainList = arrayListOf<ValidationResultMain>()
        println("purchaseItems: $purchaseItems")
        val validItems = purchaseItems?.map { item ->
            println("item.size: ${item.size}")
            println("item.quantity: ${item.quantity}")
            db.collection("productos").document(item.id!!).get()
                .addOnCompleteListener {
                    val currentStock = it.result.get("stock_${item.size?.lowercase()}")
                    println("currentStock: $currentStock")
                    if ((currentStock as Long) - item.quantity!! < 0) {
                        println("No hay stock para ${item.name}")
                        validationMainList.add(ValidationResultMain(
                            item.id,
                            false,
                            item.size
                        ))
                    } else {
                        println("Hay suficiente stock para ${item.name}")
                        validationMainList.add(
                            ValidationResultMain(
                            item.id,
                            true,
                            item.size
                        ))
                    }
                    println("validationMainList.size: $validationMainList")
                    println("validationMainList.size: ${validationMainList.size}")
                    println("purchaseItems?.size: ${purchaseItems?.size}")
                    if (validationMainList.size == purchaseItems?.size) {
                        binding.loadingText.visibility = TextView.GONE
                        binding.loadingSpinner.visibility = ProgressBar.GONE
                        validationRecyclerView = binding.validationRecycler
                        validationRecyclerView.layoutManager = layoutManager
                        validationRecyclerView.setHasFixedSize(true)
                        validationAdapter = ValidationResultAdapter(requireContext(), validationMainList, purchaseItems!!)
                        validationRecyclerView.adapter = validationAdapter
                        validationRecyclerView.visibility = RecyclerView.VISIBLE
                        binding.paymentSubmitButton.visibility = Button.VISIBLE
                        val stockItems = validationMainList.filter { it.item_in_stock == true }
                        println("stockItems: $stockItems")
                        if (stockItems.size != purchaseItems?.size) {
                            val filterIds = stockItems.map { it.item_id }
                            val filterSizes = stockItems.map { it.item_size }
                            println("filterIds: $filterIds")
                            val validItems = purchaseItems?.filter { it.id in filterIds }?.filter { it.size in filterSizes }
                            //val validItems = purchaseItems.filter { invalidItems.contains(it) }
                            println("validItems: $validItems")
                            println("validItems size: ${validItems?.size}")
                            binding.paymentSubmitButton.text = "Regresar al Carrito"
                            binding.loadingText.text = "Hay algunos articulos que ya no se encuentran en stock y deben ser retirados del carrito"
                            binding.loadingText.visibility = TextView.VISIBLE
                            binding.paymentSubmitButton.setOnClickListener {
                                listener?.onDataReceived(CheckoutData(type = "valid_items", validItems = validItems!!, contact_info = paymentData.contact_info))
                                dismiss()
                            }
                        } else {
                            binding.paymentSubmitButton.text = "Pagar articulos"
                            binding.paymentSubmitButton.setOnClickListener {
                                listener?.onDataReceived(CheckoutData(type = "payment", contact_info = paymentData.contact_info))
                                dismiss()
                            }
                        }
                    }
                }
        }
        Tasks.whenAllComplete(validItems)
    }

    private fun initializePayment() {
        binding.loadingText.text = "Realizando Transaccion"

        val pubkey = WompiKeys.TEST_PUBKEY
        val currency = "COP"
        val amount = (paymentData.subtotal).toString() + "00"
        val reference = generateRandomAlphaNumericString(32)
        val customerName = paymentData.contact_info?.contact_name?.replace(" ", "+")
        val customerEmail = paymentData.contact_info?.contact_email?.replace("@", "%40")
        val customerPhone = paymentData.contact_info?.contact_phone?.replace("+57","")
        val customerDocumentType = paymentData.contact_info?.contact_doc_type
        val customerDocument = paymentData.contact_info?.contact_doc

        var wompi_url = """
            https://checkout.wompi.co/p/?
            public-key=$pubkey&
            currency=$currency&
            amount-in-cents=$amount&
            reference=$reference&
            redirect-url=https%3A%2F%2Fbaudoap.com&
            customer-data%3Aemail=$customerEmail&
            customer-data%3Afull-name=$customerName&
            customer-data%3Aphone-number=$customerPhone&
            customer-data%3Alegal-id=$customerDocument&
            customer-data%3Alegal-id-type=$customerDocumentType
        """.trimIndent()

        var browserIntent = Intent(context, BrowserActivity::class.java).apply {
            putExtra("incoming_url", wompi_url)
        }
        startActivityForResult(browserIntent, REQUEST_CODE_ACTIVITY)
    }

    private fun getTransaction(payment_id: String) {
        client = apiClient.createRetrofit().create(PostsService::class.java)
        thread {
            val call = client.getTransaction(payment_id)
            val transaction = call.execute().body()
            println("Transaction: $transaction")
            println("Data: ${transaction?.data}")
            println("Status: ${transaction?.data?.status}")
            if (transaction != null) {
                if (transaction.data.status != "DECLINED") {
                    requireActivity().runOnUiThread {
                        binding.loadingSpinner.visibility = ProgressBar.GONE
                        binding.txDone.visibility = ImageView.VISIBLE
                        binding.loadingText.text = "Transaccion exitosa"
                        binding.paymentSubmitButton.visibility = Button.VISIBLE
                        binding.paymentSubmitButton.setOnClickListener {
                            listener?.onDataReceived(CheckoutData(type = "payment_approved", transactionResponse = transaction, contact_info = paymentData.contact_info))
                            dismiss()
                        }
                    }
                } else {
                    requireActivity().runOnUiThread {
                        binding.loadingSpinner.visibility = ProgressBar.GONE
                        binding.txError.visibility = ImageView.VISIBLE
                        binding.loadingText.text = "Transaccion no aprobada"
                        binding.paymentSubmitButton.visibility = Button.VISIBLE
                        binding.paymentSubmitButton.setOnClickListener {
                            listener?.onDataReceived(CheckoutData(type = "payment_declined", transactionResponse = transaction))
                            dismiss()
                        }
                    }
                }
            }
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == REQUEST_CODE_ACTIVITY && resultCode == Activity.RESULT_OK) {
            val payment_id = data?.getStringExtra("payment_id")
            if (payment_id != null) {
                println("payment_id: $payment_id")
                getTransaction(payment_id)
            }
        } else {
            binding.loadingSpinner.visibility = ProgressBar.GONE
            binding.txError.visibility = ImageView.VISIBLE
            binding.loadingText.text = "Pago cancelado"
            binding.paymentSubmitButton.visibility = Button.VISIBLE
            binding.paymentSubmitButton.setOnClickListener {
                dismiss()
            }
        }
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        if (context is OnFragmentInteractionListener) {
            listener = context
        }
    }

    override fun onDetach() {
        super.onDetach()
        listener = null
    }
}