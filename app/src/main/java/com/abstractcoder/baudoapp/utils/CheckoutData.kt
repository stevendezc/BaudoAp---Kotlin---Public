package com.abstractcoder.baudoapp.utils

import android.net.Uri
import android.os.Parcel
import android.os.Parcelable

data class ContactInfo(
    val contact_email: String? = "",
    val contact_name: String? = "",
    val contact_address: String? = "",
    val contact_apartment: String? = "",
    val contact_city: String? = "",
    val contact_phone: String? = ""
)

data class PsePaymentInfo(
    val banking_entity: String? = "",
    val person: String? = "",
    val doc_type: String? = "",
    val doc: String? = "",
)

data class CcPaymentInfo(
    val card_holder: String? = "",
    val card_number: String? = "",
    val card_expiration: String? = "",
    val card_cvv: String? = ""
)

data class CheckoutData(
    var type: String? = "",
    var contact_info: ContactInfo? = null,
    var pse_payment_info: PsePaymentInfo? = null,
    var cc_payment_info: CcPaymentInfo? = null
): Parcelable {
    constructor(parcel: Parcel) : this(
        parcel.readString(),
        parcel.readParcelable(ContactInfo::class.java.classLoader),
        parcel.readParcelable(PsePaymentInfo::class.java.classLoader),
        parcel.readParcelable(CcPaymentInfo::class.java.classLoader),
    ) {
    }

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeString(type)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<CheckoutData> {
        override fun createFromParcel(parcel: Parcel): CheckoutData {
            return CheckoutData(parcel)
        }

        override fun newArray(size: Int): Array<CheckoutData?> {
            return arrayOfNulls(size)
        }
    }
}