package com.pereira.baudoapp.recyclers

import android.net.Uri
import android.os.Parcel
import android.os.Parcelable
import com.google.firebase.Timestamp

data class PodcastPostMain(var id: String?, var thumbnail: Uri?, var background: Uri?, var title: String?, var creation_date: Timestamp?, var description: String?, var media: Uri?, var status: String?): Parcelable {
    constructor(parcel: Parcel) : this(
        parcel.readString(),
        parcel.readParcelable(Uri::class.java.classLoader),
        parcel.readParcelable(Uri::class.java.classLoader),
        parcel.readString(),
        parcel.readParcelable(Timestamp::class.java.classLoader),
        parcel.readString(),
        parcel.readParcelable(Uri::class.java.classLoader),
        parcel.readString()
    ) {
    }

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeString(id)
        parcel.writeParcelable(thumbnail, flags)
        parcel.writeParcelable(background, flags)
        parcel.writeString(title)
        parcel.writeParcelable(creation_date, flags)
        parcel.writeString(description)
        parcel.writeParcelable(media, flags)
        parcel.writeString(status)
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