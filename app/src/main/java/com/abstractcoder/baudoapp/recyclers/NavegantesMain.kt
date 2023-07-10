package com.abstractcoder.baudoapp.recyclers

import android.os.Parcel
import android.os.Parcelable

data class NavegantesMain(
    var title: String? = "",
    var monthly_price: String? = "",
    var yearly_price: String? = "",
    var has_input: Boolean? = false,
    var image: Int? = 0,
    var link_1: String? = "",
    var link_2: String? = "",
    var btn_color: String? = "#FFFFFF",
    var info_1: String? = "",
    var info_2: String? = "",
    var info_3: String? = ""
): Parcelable {
    constructor(parcel: Parcel) : this(
        parcel.readString(),
        parcel.readString(),
        parcel.readString(),
        parcel.readValue(Boolean::class.java.classLoader) as? Boolean,
        parcel.readValue(Int::class.java.classLoader) as? Int,
        parcel.readString(),
        parcel.readString(),
        parcel.readString(),
        parcel.readString(),
        parcel.readString(),
        parcel.readString()
    ) {
    }

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeString(title)
        parcel.writeString(monthly_price)
        parcel.writeString(yearly_price)
        parcel.writeValue(has_input)
        parcel.writeValue(image)
        parcel.writeString(link_1)
        parcel.writeString(link_2)
        parcel.writeString(btn_color)
        parcel.writeString(info_1)
        parcel.writeString(info_2)
        parcel.writeString(info_3)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<NavegantesMain> {
        override fun createFromParcel(parcel: Parcel): NavegantesMain {
            return NavegantesMain(parcel)
        }

        override fun newArray(size: Int): Array<NavegantesMain?> {
            return arrayOfNulls(size)
        }
    }
}
