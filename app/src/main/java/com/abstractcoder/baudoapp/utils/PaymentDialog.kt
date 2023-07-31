package com.abstractcoder.baudoapp.utils

import android.app.Dialog
import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.view.Gravity
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.FragmentActivity
import com.abstractcoder.baudoapp.BrowserActivity
import com.abstractcoder.baudoapp.R
import com.abstractcoder.baudoapp.databinding.PaymentDialogBinding
import com.abstractcoder.baudoapp.utils.API.PostsService
import com.abstractcoder.baudoapp.utils.API.PostsServiceImpl
import com.abstractcoder.baudoapp.utils.wompi.*

class PaymentDialog(
    private val paymentData: CheckoutData
    /*private val onSubmitClickListener: () -> Unit,
    private val onBaudoVideoLinkListener: () -> Unit*/
): DialogFragment() {
    private lateinit var binding: PaymentDialogBinding
    private val apiClient = PostsServiceImpl()

    private lateinit var client: PostsService
    private var merchantId = 0
    private var acceptance_token = ""

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        super.onCreate(savedInstanceState)
        binding = PaymentDialogBinding.inflate(layoutInflater)

        client = apiClient.createRetrofit().create(PostsService::class.java)

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

    private suspend fun getMerchantToken() {
        /*val ioScope = CoroutineScope(newSingleThreadContext("merchantThread"))
        val coroutine = ioScope.launch {
            val client = apiClient.createRetrofit().create(PostsService::class.java)
            val acceptanceTokenCall = client.getMerchantToken(WompiKeys.TEST_PUBKEY)
            val acceptanceTokenResponse = acceptanceTokenCall.body()
            if (acceptanceTokenCall.isSuccessful) {
                println("acceptanceTokenResponse: $acceptanceTokenResponse")
                merchantId = acceptanceTokenResponse?.data?.id!!
                println("merchantId: $merchantId")
            }
        }
        runBlocking {
            coroutine.join() // Wait for the coroutine to complete
        }

         */
        //val client = apiClient.createRetrofit().create(PostsService::class.java)
        val acceptanceTokenCall = client.getMerchantToken(WompiKeys.TEST_PUBKEY)
        val acceptanceTokenResponse = acceptanceTokenCall.body()
        if (acceptanceTokenCall.isSuccessful) {
            println("acceptanceTokenResponse: $acceptanceTokenResponse")
            merchantId = acceptanceTokenResponse?.data?.id!!
            acceptance_token = acceptanceTokenResponse.data.presigned_acceptance.acceptance_token
        }
    }

    suspend fun tokenizeCreditCard(activity: FragmentActivity, postRequest: CcTokenRequestData): CcTokenResponse? {
        /*val ioScope = CoroutineScope(Dispatchers.IO)
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

         */
        //val client = apiClient.createRetrofit().create(PostsService::class.java)
        val ccTokenCall = client.tokenizeCreditCard(postRequest)
        val tokenizedResponse = ccTokenCall.body()
        if (ccTokenCall.isSuccessful && tokenizedResponse?.status.equals("CREATED")) {
            println("tokenizedResponse: $tokenizedResponse")
            return tokenizedResponse!!
        } else {
            Toast.makeText(activity, "Error de ejecucion", Toast.LENGTH_SHORT).show()
            return null
        }
    }

    suspend fun getPaymentSourceId(postRequest: PaymentSourceRequestData): PaymentSourceResponse? {
        val paymentSourceCall = client.paymentSource(postRequest)
        val paymentSourceResponse = paymentSourceCall.body()
        if (paymentSourceCall.isSuccessful) {
            println("tokenizedResponse: $paymentSourceResponse")
            return paymentSourceResponse!!
        } else {
            Toast.makeText(activity, "Error de ejecucion", Toast.LENGTH_SHORT).show()
            return null
        }
    }

    fun fillTransactionData(paymentToken: String, paymentType: String): CcTxRequestData {
        return CcTxRequestData(
            acceptance_token,
            10000000,
            "COP",
            CustomerData(
                paymentData.contact_info?.contact_name!!,
                "23179471203",
                paymentType,
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

    private fun generateRandomAlphaNumericString(length: Int): String {
        val charset = ('A'..'Z') + ('a'..'z') + ('0'..'9')
        return (1..length)
            .map { charset.random() }
            .joinToString("")
    }

    private fun initializePayment() {
        binding.loadingText.text = "Realizando Transaccion"

        val pubkey = WompiKeys.PRD_PUBKEY
        val currency = "COP"
        //val amount = (paymentData.subtotal).toString() + "00"
        val amount = (100).toString() + "00"
        val reference = generateRandomAlphaNumericString(32)
        val customerName = paymentData.contact_info?.contact_name?.replace(" ", "+")
        val customerEmail = paymentData.contact_info?.contact_email?.replace("@", "%40")
        val customerPhone = paymentData.contact_info?.contact_phone?.replace("+57","")
        val customerDocumentType = paymentData.contact_info?.contact_doc_type
        val customerDocument = paymentData.contact_info?.contact_doc

        println("reference: $reference")
        println("customerEmail: $customerEmail")
        println("customerName: $customerName")
        println("customerPhone: $customerPhone")

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
        context?.startActivity(browserIntent)

        /*val coroutineExceptionHandler = CoroutineExceptionHandler{_, throwable ->
            throwable.printStackTrace()
        }
        GlobalScope.launch(Dispatchers.IO + coroutineExceptionHandler) {
            getMerchantToken()
            println("Merchant id: $merchantId")
            val token = tokenizeCreditCard(requireActivity(), CcTokenRequestData(
                paymentData.cc_payment_info?.card_holder!!,
                paymentData.cc_payment_info?.card_cvv!!,
                expirationMonth,
                expirationYear,
                paymentData.cc_payment_info?.card_number!!,
            ))
            println("Token: $token")
            val payment_source_id = getPaymentSourceId(
                PaymentSourceRequestData(
                    acceptance_token,
                    paymentData.contact_info?.contact_email!!,
                    token?.data?.public_key!!,
                    "CARD"
                )
            )
            println("payment_source_id: $payment_source_id")
            fillTransactionData(token?.data?.public_key!!, "CARD")
        }

         */

    }
}