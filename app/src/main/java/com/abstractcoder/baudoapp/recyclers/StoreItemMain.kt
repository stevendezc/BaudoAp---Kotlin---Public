package com.abstractcoder.baudoapp.recyclers

import android.os.Parcel
import android.os.Parcelable
import com.google.firebase.Timestamp

data class StoreItemMain(
    var id: String? = "",
    var title: String? = "",
    var type: String? = "",
    var thumbnail: String? = "",
    var gallery: List<String>? = listOf<String>(),
    var price: String? = "",
    var description: String? = "",
    var creation_date: Timestamp? = null,
    var stock_xs: Int = 0,
    var stock_s: Int = 0,
    var stock_m: Int = 0,
    var stock_l: Int = 0,
    var stock_xl: Int = 0
) : Parcelable {
    constructor(parcel: Parcel) : this(
        parcel.readString(),
        parcel.readString(),
        parcel.readString(),
        parcel.readString(),
        parcel.createStringArrayList(),
        parcel.readString(),
        parcel.readString(),
        parcel.readParcelable(Timestamp::class.java.classLoader),
        parcel.readInt(),
        parcel.readInt(),
        parcel.readInt(),
        parcel.readInt(),
        parcel.readInt()
    ) {
    }

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeString(id)
        parcel.writeString(title)
        parcel.writeString(type)
        parcel.writeString(thumbnail)
        parcel.writeStringList(gallery)
        parcel.writeString(price)
        parcel.writeString(description)
        parcel.writeParcelable(creation_date, flags)
        parcel.writeInt(stock_xs)
        parcel.writeInt(stock_s)
        parcel.writeInt(stock_m)
        parcel.writeInt(stock_l)
        parcel.writeInt(stock_xl)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<StoreItemMain> {
        override fun createFromParcel(parcel: Parcel): StoreItemMain {
            return StoreItemMain(parcel)
        }

        override fun newArray(size: Int): Array<StoreItemMain?> {
            return arrayOfNulls(size)
        }
    }
}