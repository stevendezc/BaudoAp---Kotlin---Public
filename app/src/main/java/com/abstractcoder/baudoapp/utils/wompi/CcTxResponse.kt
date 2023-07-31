package com.abstractcoder.baudoapp.utils.wompi

data class PaymentMethodX(
    val phone_number: Long,
    val type: String
)

data class ShippingAddressX(
    val address_line_1: String,
    val city: String,
    val country: String,
    val phone_number: Long,
    val region: String
)

data class CcData(
    val amount_in_cents: Int,
    val created_at: String,
    val currency: String,
    val customer_email: String,
    val id: String,
    val payment_link_id: Any,
    val payment_method: PaymentMethodX,
    val payment_method_type: String,
    val redirect_url: String,
    val reference: String,
    val shipping_address: ShippingAddressX,
    val status: String
)

data class CcTxResponse(
    val `data`: CcData
)