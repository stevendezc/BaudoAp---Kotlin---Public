package com.abstractcoder.baudoapp.utils.wompi

@kotlinx.serialization.Serializable
data class CcTokenRequestData(
    val card_holder: String,
    val cvc: String,
    val exp_month: String,
    val exp_year: String,
    val number: String
)