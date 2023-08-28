package com.abstractcoder.baudoapp.recyclers

import android.os.Parcel
import android.os.Parcelable

data class ValidationResultMain(
    var item_id: String?,
    var item_in_stock: Boolean?,
    var item_subtype: String?,
    var item_size: String?
): Parcelable {
    constructor(parcel: Parcel) : this(
        parcel.readString(),
        parcel.readValue(Boolean::class.java.classLoader) as? Boolean,
        parcel.readString(),
        parcel.readString()
    ) {
    }

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeString(item_id)
        parcel.writeValue(item_in_stock)
        parcel.writeString(item_subtype)
        parcel.writeString(item_size)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<ValidationResultMain> {
        override fun createFromParcel(parcel: Parcel): ValidationResultMain {
            return ValidationResultMain(parcel)
        }

        override fun newArray(size: Int): Array<ValidationResultMain?> {
            return arrayOfNulls(size)
        }
    }
}