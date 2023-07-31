package com.abstractcoder.baudoapp.utils.wompi

data class PaymentSourceRequestData(
    val acceptance_token: String,
    val customer_email: String,
    val token: String,
    val type: String
)