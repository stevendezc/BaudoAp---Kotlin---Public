package com.pereira.baudoapp.recyclers

import android.os.Parcel
import android.os.Parcelable

data class NavegantesMain(
    var title: String? = "",
    var price: String? = "",
    var link: String? = "",
    var btn_color: String? = "#FFFFFF",
    var support_text: String? = "",
    var extra_info: String? = ""
): Parcelable {
    constructor(parcel: Parcel) : this(
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
        parcel.writeString(price)
        parcel.writeString(link)
        parcel.writeString(btn_color)
        parcel.writeString(support_text)
        parcel.writeString(extra_info)
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