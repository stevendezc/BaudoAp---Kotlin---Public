package com.pereira.baudoapp.recyclers

import android.os.Parcel
import android.os.Parcelable
import com.google.firebase.Timestamp

data class StoreItemMain(
    var id: String? = "",
    var title: String? = "",
    var type: String? = "",
    var subtype: String? = "",
    var thumbnail: String? = "",
    var gallery: List<String>? = listOf<String>(),
    var price: String? = "",
    var description: String? = "",
    var creation_date: Timestamp? = null,
    var stock: Int? = 0,
    var stock_regular_xs: Int? = 0,
    var stock_cenido_xs: Int? = 0,
    var stock_regular_s: Int? = 0,
    var stock_cenido_s: Int? = 0,
    var stock_regular_m: Int? = 0,
    var stock_cenido_m: Int? = 0,
    var stock_regular_l: Int? = 0,
    var stock_cenido_l: Int? = 0,
    var stock_regular_xl: Int? = 0,
    var stock_cenido_xl: Int? = 0
) : Parcelable {
    constructor(parcel: Parcel) : this(
        parcel.readString(),
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
        parcel.readInt(),
        parcel.readInt(),
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
        parcel.writeString(subtype)
        parcel.writeString(thumbnail)
        parcel.writeStringList(gallery)
        parcel.writeString(price)
        parcel.writeString(description)
        parcel.writeParcelable(creation_date, flags)
        parcel.writeInt(stock!!)
        parcel.writeInt(stock_regular_xs!!)
        parcel.writeInt(stock_cenido_xs!!)
        parcel.writeInt(stock_regular_s!!)
        parcel.writeInt(stock_cenido_s!!)
        parcel.writeInt(stock_regular_m!!)
        parcel.writeInt(stock_cenido_m!!)
        parcel.writeInt(stock_regular_l!!)
        parcel.writeInt(stock_cenido_l!!)
        parcel.writeInt(stock_regular_xl!!)
        parcel.writeInt(stock_cenido_xl!!)
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