package com.anguyen.mymap.models

import android.location.Location

data class UserDetail constructor(
    var id: String = "",
    var username: String = "",
    var email: String = ""
)

data class UserInformationDetail constructor(
    var phoneNumber: String = "",
    var location: CoordinateDetail?
){

    fun toMap(): HashMap<String, Any?> {
        return hashMapOf(Pair("phoneNumber", phoneNumber), Pair("location", location!!))
    }

}

data class UserRespondDetail constructor(
    val id: String = "",
    val username: String = "",
    val email: String = "",
    var phoneNumber: String = "",
    var location: Location? = null
)