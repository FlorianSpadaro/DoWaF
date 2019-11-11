package com.example.dowaf.model

import android.os.Parcel
import android.os.Parcelable

class Aliment() : Parcelable {
    var id: String? = null
    var name: String? = null
    var image: String? = null
    var position: String? = null
    var owner: User? = null
    var booker: User? = null

    constructor(parcel: Parcel) : this() {
        id = parcel.readString()
        name = parcel.readString()
        image = parcel.readString()
        position = parcel.readString()
    }

    /*constructor(
        id: String? = null,
        name: String? = null,
        image: String? = null,
        position: String? = null,
        owner: User? = null,
        booker: User? = null
    ){
        this.id = id
        this.name = name
        this.image = image
        this.position = position
        this.owner = owner
        this.booker = booker
    }*/

    fun toMap() : Map<String, Any?> {
        val result = HashMap<String, Any?>()
        result.put("name", name)
        result.put("image", image)
        result.put("position", position)
        result.put("owner", owner)
        result.put("booker", booker)

        return result
    }

    fun fromMap(map: Map<String, Any?>) {
        this.name = map["name"].toString()
        this.image = map["image"].toString()
        this.position = map["position"].toString()
        //this.owner = map["owner"].toString()
        //this.booker = map["name"].toString()
    }

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeString(id)
        parcel.writeString(name)
        parcel.writeString(image)
        parcel.writeString(position)
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