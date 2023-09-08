package com.pereira.baudoapp.recyclers

import android.os.Parcel
import android.os.Parcelable

data class SavedPostMain(var id: String?, var thumbnail: String?, var title: String?, var liked: Boolean?): Parcelable {
    constructor(parcel: Parcel) : this(
        parcel.readString(),
        parcel.readString(),
        parcel.readString(),
        parcel.readValue(Boolean::class.java.classLoader) as? Boolean
    ) {
    }

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeString(id)
        parcel.writeString(thumbnail)
        parcel.writeString(title)
        parcel.writeValue(liked)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<SavedPostMain> {
        override fun createFromParcel(parcel: Parcel): SavedPostMain {
            return SavedPostMain(parcel)
        }

        override fun newArray(size: Int): Array<SavedPostMain?> {
            return arrayOfNulls(size)
        }
    }
}
