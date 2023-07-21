package com.abstractcoder.baudoapp.utils.wompi

@kotlinx.serialization.Serializable
data class CcTokenResponse(
    val `data`: Data,
    val status: String
)