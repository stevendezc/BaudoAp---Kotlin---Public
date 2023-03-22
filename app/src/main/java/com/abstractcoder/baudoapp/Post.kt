package com.abstractcoder.baudoapp

import android.os.Parcel
import android.os.Parcelable
import com.google.firebase.Timestamp

data class Commentary(
    val id: String,
    val author: String,
    val text: String,
    val timestamp: Timestamp,
    val replies: List<Commentary>
)

data class Reaction(
    val id: String,
    val author: String,
    val timestamp: Timestamp,
    val type: String
)

data class PostData(
    val id: String,
    val author: String,
    val category: String,
    val commentaries: List<Commentary>,
    val description: String,
    val main_media: String,
    val reactions: List<Reaction>,
    val thumbnail: String,
    val timestamp: Timestamp,
    val title: String,
    val type: String
) : Parcelable {
    constructor(parcel: Parcel) : this(
        parcel.readString()!!,
        parcel.readString()!!,
        parcel.readString()!!,
        TODO("commentaries"),
        parcel.readString()!!,
        parcel.readString()!!,
        TODO("reactions"),
        parcel.readString()!!,
        parcel.readParcelable(Timestamp::class.java.classLoader)!!,
        parcel.readString()!!,
        parcel.readString()!!
    ) {
    }

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeString(id)
        parcel.writeString(author)
        parcel.writeString(category)
        parcel.writeString(description)
        parcel.writeString(main_media)
        parcel.writeString(thumbnail)
        parcel.writeParcelable(timestamp, flags)
        parcel.writeString(title)
        parcel.writeString(type)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<PostData> {
        override fun createFromParcel(parcel: Parcel): PostData {
            return PostData(parcel)
        }

        override fun newArray(size: Int): Array<PostData?> {
            return arrayOfNulls(size)
        }
    }

}

data class Post(val id: String, val data: PostData)
