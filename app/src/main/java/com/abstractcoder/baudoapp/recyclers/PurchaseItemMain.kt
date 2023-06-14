package com.abstractcoder.baudoapp.recyclers

import android.os.Parcel
import android.os.Parcelable

data class PurchaseItemMain(
    val name: String? = "",
    val thumbnail: String? = "",
    val price: String? = "",
    val quantity: Int? = 0,
    val size: String? = "",
): Parcelable {
    constructor(parcel: Parcel) : this(
        parcel.readString(),
        parcel.readString(),
        parcel.readString(),
        parcel.readValue(Int::class.java.classLoader) as? Int,
        parcel.readString()
    ) {
    }

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeString(name)
        parcel.writeString(thumbnail)
        parcel.writeString(price)
        parcel.writeValue(quantity)
        parcel.writeString(size)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<PurchaseItemMain> {
        override fun createFromParcel(parcel: Parcel): PurchaseItemMain {
            return PurchaseItemMain(parcel)
        }

        override fun newArray(size: Int): Array<PurchaseItemMain?> {
            return arrayOfNulls(size)
        }
    }
}
