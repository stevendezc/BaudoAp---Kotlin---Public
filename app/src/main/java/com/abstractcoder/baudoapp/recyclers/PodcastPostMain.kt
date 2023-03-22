package com.abstractcoder.baudoapp.recyclers

import android.net.Uri
import android.os.Parcel
import android.os.Parcelable
import com.google.firebase.Timestamp

data class PodcastPostMain(var thumbnail: Uri, var title: String?, var timestamp: Timestamp?, var description: String?, var media: Uri): Parcelable {
    constructor(parcel: Parcel) : this(
        parcel.readParcelable(Uri::class.java.classLoader)!!,
        parcel.readString(),
        parcel.readParcelable(Timestamp::class.java.classLoader),
        parcel.readString(),
        parcel.readParcelable(Uri::class.java.classLoader)!!
    ) {
    }

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeParcelable(thumbnail, flags)
        parcel.writeString(title)
        parcel.writeParcelable(timestamp, flags)
        parcel.writeString(description)
        parcel.writeParcelable(media, flags)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<PodcastPostMain> {
        override fun createFromParcel(parcel: Parcel): PodcastPostMain {
            return PodcastPostMain(parcel)
        }

        override fun newArray(size: Int): Array<PodcastPostMain?> {
            return arrayOfNulls(size)
        }
    }
}
