package com.pereira.baudoapp.utils.wompi

data class TransactionResponse(
    val data: Data,
    val meta: Meta
)

class Data(
    val id: String,
    val created_at: String,
    val amount_in_cents: Int,
    val reference: String,
    val customer_email: String,
    val currency: String,
    val payment_method_type: String,
    val payment_method: PaymentMethod,
    val redirect_url: String,
    val status: String,
    val status_message: String,
    val merchant: Merchant,
    val taxes: Array<String>
)

class PaymentMethod(
    val type: String,
    val extra: Extra,
    val installments: Int
)
class Extra(
    val name: String,
    val brand: String,
    val last_four: String,
    val is_three_ds: Boolean,
    val three_ds_auth: ThreeDsAuth,
    val external_identifier: String,
    val processor_response_code: String,
)
class ThreeDsAuth(
    val three_ds_auth: InnerThreeDsAuth,
)
class InnerThreeDsAuth(
    val current_step: String,
    val current_step_status: String,
)

class Merchant(
    val name: String,
    val legal_name: String,
    val contact_name: String,
    val phone_number: String,
    val logo_url: String,
    val legal_id_type: String,
    val email: String,
    val legal_id: String
)

class Meta