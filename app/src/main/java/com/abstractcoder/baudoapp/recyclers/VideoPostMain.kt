package com.abstractcoder.baudoapp.recyclers

import android.net.Uri
import android.os.Parcel
import android.os.Parcelable

data class VideoPostMain(var video: Uri, var thumbnail: Uri, var title: String, var description: String, var category: String): Parcelable {
    constructor(parcel: Parcel) : this(
        parcel.readParcelable(Uri::class.java.classLoader)!!,
        parcel.readParcelable(Uri::class.java.classLoader)!!,
        parcel.readString()!!,
        parcel.readString()!!,
        parcel.readString()!!
    ) {
    }

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeParcelable(video, flags)
        parcel.writeParcelable(thumbnail, flags)
        parcel.writeString(title)
        parcel.writeString(description)
        parcel.writeString(category)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<VideoPostMain> {
        override fun createFromParcel(parcel: Parcel): VideoPostMain {
            return VideoPostMain(parcel)
        }

        override fun newArray(size: Int): Array<VideoPostMain?> {
            return arrayOfNulls(size)
        }
    }
}
