package com.example.dowaf.model

import android.os.Parcel
import android.os.Parcelable

class Category() : Parcelable {
    var id: String? = null
    var name: String? = null
    var imageUrl: String? = null

    constructor(parcel: Parcel) : this() {
        id = parcel.readString()
        name = parcel.readString()
        imageUrl = parcel.readString()
    }

    fun fromMap(map: Map<String, Any?>) {
        this.name = map["name"].toString()
        this.imageUrl = map["imageUrl"].toString()
    }

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeString(id)
        parcel.writeString(name)
        parcel.writeString(imageUrl)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<Category> {
        override fun createFromParcel(parcel: Parcel): Category {
            return Category(parcel)
        }

        override fun newArray(size: Int): Array<Category?> {
            return arrayOfNulls(size)
        }
    }
}