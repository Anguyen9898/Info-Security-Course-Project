package com.anguyen.mymap.models

import com.anguyen.mymap.commons.*

data class UserDetail constructor(
    var id: String = "",
    var username: String = "",
    var email: String = ""
)

data class UserInformationDetail constructor(
    var userType: String = "",
    var userTypeImgUrl: String = "",
    var avatarUrl: String = "",
    var gender: String = "",
    var phoneNumber: String = "",
    var location: CoordinateDetail? = null,
    var notifications: HashMap<String, NotificationDetail?> = hashMapOf()
){
    fun isValid() = !areAnyFieldsEmpty(gender, phoneNumber) && isPhoneNumberValid(phoneNumber)

    fun toMap(): HashMap<String, Any?> {
        return hashMapOf(
            Pair("avatarUrl", avatarUrl),
            Pair(KEY_USER_TYPE, userType),
            Pair(KEY_USER_TYPE_IMG_URL, userTypeImgUrl),
            Pair("gender", gender),
            Pair("phoneNumber", phoneNumber),
            Pair("location", location!!),
            Pair("notifications", notifications)
        )
    }

}

data class UserRespondDetail constructor(
    var id: String = "",
    var avatarUrl: String = "",
    var userType: String = "",
    var userTypeImgUrl: String = "",
    var username: String = "",
    var gender: String = "",
    var email: String = "",
    var phoneNumber: String = "",
    var location: CoordinateDetail? = CoordinateDetail(),
    var notifications: HashMap<String, NotificationDetail?> = hashMapOf()
)

//data class UserSearchListDetail constructor(
//    var avatarUrl: String = "",
//    var id: String = "",
//    var username: String = ""
//)