package com.pereira.baudoapp

import android.os.Parcel
import android.os.Parcelable
import com.google.firebase.Timestamp

data class Commentary(
    var id: String? = "",
    val post: String? = "",
    val author: String? = "",
    val author_email: String? = "",
    val text: String? = "",
    val timestamp: Timestamp? = null,
    val replies: List<Commentary>? = null
)

data class PodcastInfo(
    val post: String? = "",
    var finished: Boolean? = false
) : Parcelable {
    constructor(parcel: Parcel) : this(
        parcel.readString(),
        parcel.readValue(Boolean::class.java.classLoader) as? Boolean
    ) {
    }

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeString(post)
        parcel.writeValue(finished)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<PodcastInfo> {
        override fun createFromParcel(parcel: Parcel): PodcastInfo {
            return PodcastInfo(parcel)
        }

        override fun newArray(size: Int): Array<PodcastInfo?> {
            return arrayOfNulls(size)
        }
    }
}

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
    val thumbnail2: String? = "",
    val creation_date: Timestamp? = null,
    val title: String? = "",
    val location: String? = "",
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
        parcel.readString(),
        parcel.readParcelable(Timestamp::class.java.classLoader),
        parcel.readString(),
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
        parcel.writeString(thumbnail2)
        parcel.writeParcelable(creation_date, flags)
        parcel.writeString(title)
        parcel.writeString(location)
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
