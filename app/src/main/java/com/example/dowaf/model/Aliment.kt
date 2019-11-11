package com.example.dowaf.model

import android.os.Parcel
import android.os.Parcelable

class Aliment() : Parcelable {
    var id: String? = null
    var name: String? = null
    var description: String? = null
    var image: String? = null
    var position: String? = null
    var ownerUid: String? = null
    var bookerUid: String? = null

    constructor(parcel: Parcel) : this() {
        id = parcel.readString()
        name = parcel.readString()
        description = parcel.readString()
        image = parcel.readString()
        position = parcel.readString()
        ownerUid = parcel.readString()
        bookerUid = parcel.readString()
    }


    fun toMap(): Map<String, Any?> {
        val result = HashMap<String, Any?>()
        result["name"] = name
        result["description"] = description
        result["image"] = image
        result["position"] = position
        result["ownerUid"] = ownerUid
        result["bookerUid"] = bookerUid

        return result
    }

    fun fromMap(map: Map<String, Any?>) {
        this.name = map["name"].toString()
        this.description = map["description"].toString()
        this.image = map["image"].toString()
        this.position = map["position"].toString()
        this.ownerUid = map["ownerUid"].toString()
        this.bookerUid = map["bookerUid"].toString()
    }

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeString(id)
        parcel.writeString(name)
        parcel.writeString(description)
        parcel.writeString(image)
        parcel.writeString(position)
        parcel.writeString(ownerUid)
        parcel.writeString(bookerUid)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<Aliment> {
        override fun createFromParcel(parcel: Parcel): Aliment {
            return Aliment(parcel)
        }

        override fun newArray(size: Int): Array<Aliment?> {
            return arrayOfNulls(size)
        }
    }
}