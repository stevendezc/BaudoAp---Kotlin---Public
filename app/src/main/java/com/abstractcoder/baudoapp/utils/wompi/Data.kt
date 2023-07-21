package com.abstractcoder.baudoapp.utils.wompi

data class Data(
    val accepted_currencies: List<String>,
    val accepted_payment_methods: List<String>,
    val active: Boolean,
    val contact_name: String,
    val email: String,
    val fraud_groups: List<Any>,
    val fraud_javascript_key: Any,
    val id: Int,
    val legal_id: String,
    val legal_id_type: String,
    val legal_name: String,
    val logo_url: Any,
    val name: String,
    val payment_methods: List<AllowedPaymentMethod>,
    val phone_number: String,
    val presigned_acceptance: PresignedAcceptance,
    val public_key: String
)