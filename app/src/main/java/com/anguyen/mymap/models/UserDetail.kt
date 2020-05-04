package com.anguyen.mymap.models

import com.anguyen.mymap.commons.*

data class UserDetail constructor(
    var id: String = "",
    var username: String = "",
    var email: String = ""
)

data class UserInformationDetail constructor(
    var userType: String = "",
    var avatarUrl: String = "",
    var gender: String = "",
    var phoneNumber: String = "",
    var location: CoordinateDetail?
){
    fun isValid() = !areAnyFieldsEmpty(gender, phoneNumber) && isPhoneNumberValid(phoneNumber)

    private fun setUserTypeImgUrl(): String{
        return (when(userType){
            KEY_EMAIL_USER -> EMAIL_USER_AVATAR_URL
            KEY_GOOGLE_USER -> GOOGLE_USER_AVATAR_URL
            KEY_FACEBOOK_USER -> FACEBOOK_USER_AVATAR_URL
            KEY_GUEST_USER -> GUEST_USER_AVATAR_URL
            else -> ""
        })
    }

    fun toMap(): HashMap<String, Any?> {
        return hashMapOf(
            Pair("avatarUrl", avatarUrl),
            Pair(KEY_USER_TYPE, userType),
            Pair("userTypeImgUrl", setUserTypeImgUrl()),
            Pair("gender", gender),
            Pair("phoneNumber", phoneNumber),
            Pair("location", location!!)
        )
    }

}

data class UserRespondDetail constructor(
    var id: String = "",
    var avatarUrl: String = "",
    var userTypeImgUrl: String = "",
    var username: String = "",
    var gender: String = "",
    var email: String = "",
    var phoneNumber: String = ""
)

data class UserSearchListDetail constructor(
    var avatarUrl: String = "",
    var id: String = "",
    var username: String = ""
)