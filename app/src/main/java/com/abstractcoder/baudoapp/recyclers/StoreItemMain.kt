package com.abstractcoder.baudoapp.recyclers

import android.net.Uri
import android.os.Parcel
import android.os.Parcelable

data class StoreItemMain(
    var id: String?,
    var name: String?,
    var image: Uri,
    var price: Double?,
    var description: String?,
    var sizes: List<String>
) : Parcelable {
    constructor(parcel: Parcel) : this(
        parcel.readString(),
        parcel.readString(),
        parcel.readParcelable(Uri::class.java.classLoader)!!,
        parcel.readValue(Double::class.java.classLoader) as? Double,
        parcel.readString(),
        parcel.createStringArrayList()!!
    ) {
    }

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeString(id)
        parcel.writeString(name)
        parcel.writeParcelable(image, flags)
        parcel.writeValue(price)
        parcel.writeString(description)
        parcel.writeStringList(sizes)
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
