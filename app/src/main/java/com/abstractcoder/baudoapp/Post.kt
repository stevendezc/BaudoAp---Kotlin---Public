package com.abstractcoder.baudoapp

import android.os.Parcel
import android.os.Parcelable
import com.google.firebase.Timestamp

data class Commentary(
    var id: String? = "",
    val post: String? = "",
    val author: String? = "",
    val text: String? = "",
    val timestamp: Timestamp? = null,
    val replies: List<Commentary>? = null
)

data class Reaction(
    val post: String? = "",
    val timestamp: Timestamp? = null,
    var type: String? = ""
)

data class PostData(
    var id: String? = "",
    val author: String? = "",
    val category: String? = "",
    val commentaries: List<String>? = null,
    val description: String? = "",
    val main_media: String? = "",
    val likes: Int? = 0,
    val dislikes: Int? = 0,
    val indifferents: Int? = 0,
    val thumbnail: String? = "",
    val timestamp: Timestamp? = null,
    val title: String? = "",
    val type: String? = ""
) : Parcelable {
    constructor(parcel: Parcel) : this(
        parcel.readString(),
        parcel.readString(),
        parcel.readString(),
        parcel.createStringArrayList(),
        parcel.readString(),
        parcel.readString(),
        parcel.readValue(Int::class.java.classLoader) as? Int,
        parcel.readValue(Int::class.java.classLoader) as? Int,
        parcel.readValue(Int::class.java.classLoader) as? Int,
        parcel.readString(),
        parcel.readParcelable(Timestamp::class.java.classLoader),
        parcel.readString(),
        parcel.readString()
    ) {
    }

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeString(id)
        parcel.writeString(author)
        parcel.writeString(category)
        parcel.writeStringList(commentaries)
        parcel.writeString(description)
        parcel.writeString(main_media)
        parcel.writeValue(likes)
        parcel.writeValue(dislikes)
        parcel.writeValue(indifferents)
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
