package com.abstractcoder.baudoapp.utils.wompi

data class CustomerData(
    val full_name: String,
    val legal_id: String,
    val legal_id_type: String,
    val phone_number: String
)

data class PaymentMethod(
    val installments: Int,
    val token: String,
    val type: String
)

data class ShippingAddress(
    val address_line_1: String,
    val address_line_2: String,
    val city: String,
    val country: String,
    val name: String,
    val phone_number: String,
    val postal_code: String,
    val region: String
)

data class CcTxRequestData(
    val acceptance_token: String,
    val amount_in_cents: Int,
    val currency: String,
    val customer_data: CustomerData,
    val customer_email: String,
    val payment_method: PaymentMethod,
    val payment_source_id: Int,
    val redirect_url: String,
    val reference: String,
    val shipping_address: ShippingAddress? = null
)