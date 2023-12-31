package com.pereira.baudoapp.recyclers

import android.net.Uri
import android.os.Parcel
import android.os.Parcelable

data class CommunityMain(var thumbnail: Uri?, var name: String?, var lastname: String?, var description: String?, var location: String?, var category: String?, var whatsapp: String?, var instagram: String?, var facebook: String?): Parcelable {
    constructor(parcel: Parcel) : this(
        parcel.readParcelable(Uri::class.java.classLoader),
        parcel.readString(),
        parcel.readString(),
        parcel.readString(),
        parcel.readString(),
        parcel.readString(),
        parcel.readString(),
        parcel.readString(),
        parcel.readString()
    ) {
    }

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeParcelable(thumbnail, flags)
        parcel.writeString(name)
        parcel.writeString(lastname)
        parcel.writeString(description)
        parcel.writeString(location)
        parcel.writeString(category)
        parcel.writeString(whatsapp)
        parcel.writeString(instagram)
        parcel.writeString(facebook)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<CommunityMain> {
        override fun createFromParcel(parcel: Parcel): CommunityMain {
            return CommunityMain(parcel)
        }

        override fun newArray(size: Int): Array<CommunityMain?> {
            return arrayOfNulls(size)
        }
    }
}