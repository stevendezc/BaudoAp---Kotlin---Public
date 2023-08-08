package com.abstractcoder.baudoapp.utils

import android.os.Parcel
import android.os.Parcelable
import com.abstractcoder.baudoapp.recyclers.PurchaseItemMain
import com.abstractcoder.baudoapp.utils.wompi.TransactionResponse

data class ContactInfo(
    val contact_email: String? = "",
    val contact_name: String? = "",
    val contact_doc_type: String? = "",
    val contact_doc: String? = "",
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
) :Parcelable {
    constructor(parcel: Parcel) : this(
        parcel.readString(),
        parcel.readString(),
        parcel.readString(),
        parcel.readString()
    ) {
    }

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeString(banking_entity)
        parcel.writeString(person)
        parcel.writeString(doc_type)
        parcel.writeString(doc)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<PsePaymentInfo> {
        override fun createFromParcel(parcel: Parcel): PsePaymentInfo {
            return PsePaymentInfo(parcel)
        }

        override fun newArray(size: Int): Array<PsePaymentInfo?> {
            return arrayOfNulls(size)
        }
    }
}

data class CcPaymentInfo(
    val card_holder: String? = "",
    val card_number: String? = "",
    val card_expiration: String? = "",
    val card_cvv: String? = ""
): Parcelable {
    constructor(parcel: Parcel) : this(
        parcel.readString(),
        parcel.readString(),
        parcel.readString(),
        parcel.readString()
    ) {
    }

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeString(card_holder)
        parcel.writeString(card_number)
        parcel.writeString(card_expiration)
        parcel.writeString(card_cvv)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<CcPaymentInfo> {
        override fun createFromParcel(parcel: Parcel): CcPaymentInfo {
            return CcPaymentInfo(parcel)
        }

        override fun newArray(size: Int): Array<CcPaymentInfo?> {
            return arrayOfNulls(size)
        }
    }
}

data class CheckoutData(
    var type: String? = "",
    var subtotal: Long? = 0,
    var contact_info: ContactInfo? = null,
    var validItems: List<PurchaseItemMain> = listOf<PurchaseItemMain>(),
    var pse_payment_info: PsePaymentInfo? = null,
    var cc_payment_info: CcPaymentInfo? = null,
    var transactionResponse: TransactionResponse? = null
): Parcelable {
    constructor(parcel: Parcel) : this(
        parcel.readString(),
        parcel.readValue(Long::class.java.classLoader) as? Long,
        parcel.readParcelable(ContactInfo::class.java.classLoader) ?: ContactInfo(),
        listOf<PurchaseItemMain>().apply {
                                                parcel.readList(this, PurchaseItemMain::class.java.classLoader)
        },
        parcel.readParcelable(PsePaymentInfo::class.java.classLoader) ?: PsePaymentInfo(),
        parcel.readParcelable(CcPaymentInfo::class.java.classLoader) ?: CcPaymentInfo()
    ) {
    }

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeString(type)
        parcel.writeValue(subtotal)
        parcel.writeList(validItems)
        parcel.writeParcelable(pse_payment_info, flags)
        parcel.writeParcelable(cc_payment_info, flags)
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