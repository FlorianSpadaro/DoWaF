package com.example.dowaf.model

class Category() {
    var id: String? = null
    var name: String? = null
    var imageUrl: String? = null

    constructor(
        id: String? = null,
        name: String? = null,
        imageUrl: String? = null
    ) : this() {
        this.id = id
        this.name = name
    }

    fun fromMap(map: Map<String, Any?>) {
        this.name = map["name"].toString()
        this.imageUrl = map["imageUrl"].toString()
    }
}