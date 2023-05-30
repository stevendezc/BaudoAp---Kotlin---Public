package com.abstractcoder.baudoapp.recyclers

import android.os.Parcel
import android.os.Parcelable
import com.google.firebase.Timestamp

data class StoreItemMain(
    var id: String? = "",
    var title: String? = "",
    var thumbnail: String? = "",
    var price: String? = "",
    var description: String? = "",
    var creation_date: Timestamp? = null,
    var sizes: List<Long>? = null
) : Parcelable {
    constructor(parcel: Parcel) : this(
        parcel.readString(),
        parcel.readString(),
        parcel.readString(),
        parcel.readString(),
        parcel.readString(),
        parcel.readParcelable(Timestamp::class.java.classLoader),
        emptyList()
    ) {
    }

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeString(id)
        parcel.writeString(title)
        parcel.writeString(thumbnail)
        parcel.writeString(price)
        parcel.writeString(description)
        parcel.writeParcelable(creation_date, flags)
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