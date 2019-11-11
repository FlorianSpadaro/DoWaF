package com.example.dowaf.model

class User {
    var id: String? = null
    var name: String? = null
    var firstName: String? = null
    var mail: String? = null
    var address: String? = null
    var phone: String? = null

    constructor(
        id: String? = null,
        name: String? = null,
        firstName: String? = null,
        phone: String? = null,
        mail: String? = null,
        address: String? = null
    ) {
        this.id = id
        this.name = name
        this.firstName = firstName
        this.address = address
        this.phone = phone
        this.mail = mail
    }

    fun toMap() : Map<String, Any?> {
        val result = HashMap<String, Any?>()
        result.put("name", name)
        result.put("firstName", firstName)
        result.put("address", address)
        result.put("phone", phone)
        result.put("mail", mail)

        return result
    }

    fun fromMap(map: Map<String, Any?>) {
        this.name = map["name"].toString()
        this.firstName = map["firstName"].toString()
        this.address = map["address"].toString()
        this.phone = map["phone"].toString()
        this.mail = map["mail"].toString()
    }
}