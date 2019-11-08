package com.example.dowaf.model

class Aliment {
    var id: String? = null
    var name: String? = null
    var image: String? = null
    var position: String? = null
    var owner: User? = null
    var booker: User? = null

    constructor(
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
    }

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
}