package com.abstractcoder.baudoapp.recyclers

import android.net.Uri
import android.os.Parcel
import android.os.Parcelable

data class ImagePostMain(var id: String?, var thumbnail: Uri, var main_media: Uri, var title: String?, var author: String?, val location: String?, var description: String?, var commentaries: List<String>): Parcelable {
    constructor(parcel: Parcel) : this(
        parcel.readString(),
        parcel.readParcelable(Uri::class.java.classLoader)!!,
        parcel.readParcelable(Uri::class.java.classLoader)!!,
        parcel.readString(),
        parcel.readString(),
        parcel.readString(),
        parcel.readString(),
        parcel.createStringArrayList()!!
    ) {
    }

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeString(id)
        parcel.writeParcelable(thumbnail, flags)
        parcel.writeParcelable(main_media, flags)
        parcel.writeString(title)
        parcel.writeString(author)
        parcel.writeString(location)
        parcel.writeString(description)
        parcel.writeStringList(commentaries)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<ImagePostMain> {
        override fun createFromParcel(parcel: Parcel): ImagePostMain {
            return ImagePostMain(parcel)
        }

        override fun newArray(size: Int): Array<ImagePostMain?> {
            return arrayOfNulls(size)
        }
    }
}