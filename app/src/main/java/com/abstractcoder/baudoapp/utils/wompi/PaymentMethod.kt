package com.abstractcoder.baudoapp.utils.wompi

data class AllowedPaymentMethod(
    val name: String,
    val payment_processors: List<PaymentProcessor>
)