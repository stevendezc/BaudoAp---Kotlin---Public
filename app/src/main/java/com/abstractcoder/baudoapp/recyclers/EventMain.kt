package com.abstractcoder.baudoapp.recyclers

import android.os.Parcel
import android.os.Parcelable
import com.google.firebase.Timestamp

data class EventMain(
    var id: String? = "",
    var date: Timestamp? = null,
    var description: String? = "",
    var event_url: String? = "",
    var month: Int? = 0,
    var subject: String? = "",
    var title: String? = "",
    var year: Int? = 0
): Parcelable {
    constructor(parcel: Parcel) : this(
        parcel.readString(),
        parcel.readParcelable(Timestamp::class.java.classLoader),
        parcel.readString(),
        parcel.readString(),
        parcel.readValue(Int::class.java.classLoader) as? Int,
        parcel.readString(),
        parcel.readString(),
        parcel.readValue(Int::class.java.classLoader) as? Int
    ) {
    }

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeString(id)
        parcel.writeParcelable(date, flags)
        parcel.writeString(description)
        parcel.writeString(event_url)
        parcel.writeValue(month)
        parcel.writeString(subject)
        parcel.writeString(title)
        parcel.writeValue(year)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<EventMain> {
        override fun createFromParcel(parcel: Parcel): EventMain {
            return EventMain(parcel)
        }

        override fun newArray(size: Int): Array<EventMain?> {
            return arrayOfNulls(size)
        }
    }
}
