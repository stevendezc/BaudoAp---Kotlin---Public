package com.abstractcoder.baudoapp.utils

import android.app.Dialog
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.view.Gravity
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.FragmentActivity
import com.abstractcoder.baudoapp.R
import com.abstractcoder.baudoapp.databinding.PaymentDialogBinding
import com.abstractcoder.baudoapp.utils.API.PostsService
import com.abstractcoder.baudoapp.utils.API.PostsServiceImpl
import com.abstractcoder.baudoapp.utils.wompi.*
import kotlinx.coroutines.*
import retrofit2.awaitResponse

class PaymentDialog(
    private val paymentData: CheckoutData
    /*private val onSubmitClickListener: () -> Unit,
    private val onBaudoVideoLinkListener: () -> Unit*/
): DialogFragment() {
    private lateinit var binding: PaymentDialogBinding
    private val apiClient = PostsServiceImpl()

    private var merchantId = 0

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        super.onCreate(savedInstanceState)
        binding = PaymentDialogBinding.inflate(layoutInflater)

        val builder = AlertDialog.Builder(requireActivity())
        builder.setView(binding.root)

        initializePayment()

        val dialog = builder.create()
        dialog.window!!.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        dialog.window!!.setGravity(Gravity.TOP)
        dialog.window!!.attributes.windowAnimations = R.style.DialogAnimation
        dialog.setCanceledOnTouchOutside(false)
        return dialog
    }

    fun fillTransactionData(paymentToken: String, paymentType: String): CcTxRequestData {
        return CcTxRequestData(
            "",
            10000000,
            "COP",
            CustomerData(
                paymentData.contact_info?.contact_name!!,
                "23179471203",
                "CC",
                paymentData.contact_info?.contact_phone!!
            ),
            paymentData.contact_info?.contact_email!!,
            PaymentMethod(
                1,
                paymentToken,
                paymentType
            ),
            1234,
            "https://baudoap.com/",
            "akjdha8qy3e98quhd8q"
        )
    }

    private fun getMerchantToken() {
        val ioScope = CoroutineScope(newSingleThreadContext("merchantThread"))
        val coroutine = ioScope.launch {
            val client = apiClient.createRetrofit().create(PostsService::class.java)
            val acceptanceTokenCall = client.getMerchantToken(WompiKeys.TEST_PUBKEY).execute()
            val acceptanceTokenResponse = acceptanceTokenCall.body()
            if (acceptanceTokenCall.isSuccessful) {
                println("acceptanceTokenResponse: $acceptanceTokenResponse")
                merchantId = acceptanceTokenResponse?.data?.id!!
                println("merchantId: $merchantId")
            }
        }
        /*
        runBlocking {
            coroutine.join() // Wait for the coroutine to complete
        }

         */
    }

    fun tokenizeCreditCard(activity: FragmentActivity, postRequest: CcTokenRequestData) {
        val ioScope = CoroutineScope(Dispatchers.IO)
        val coroutine = ioScope.launch {
            val client = apiClient.createRetrofit().create(PostsService::class.java)
            val ccTokenCall = client.tokenizeCreditCard(postRequest).execute()
            val tokenizedResponse = ccTokenCall.body()
            if (ccTokenCall.isSuccessful && tokenizedResponse?.status.equals("CREATED")) {
                println("tokenizedResponse: $tokenizedResponse")
            } else {
                Toast.makeText(activity, "Error de ejecucion", Toast.LENGTH_SHORT).show()
            }
        }
        runBlocking {
            coroutine.join() // Wait for the coroutine to complete
        }
    }

    private fun initializePayment() {
        binding.loadingText.text = "Realizando Transaccion"

        val expirationMonth = paymentData.cc_payment_info?.card_expiration!!.take(2)
        val expirationYear = paymentData.cc_payment_info?.card_expiration!!.takeLast(2)
        /*val posts = tokenizeCreditCard(requireActivity(), CcTokenRequestData(
            paymentData.cc_payment_info?.card_holder!!,
            paymentData.cc_payment_info?.card_cvv!!,
            expirationMonth,
            expirationYear,
            paymentData.cc_payment_info?.card_number!!,
        ))

         */
    }
}