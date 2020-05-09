package com.anguyen.mymap.models


data class NotificationDetail(
    var id: String = "",
    var senderId: String = "",
    var receiverId: String = "",
    var status: String = "",
    var category: String = ""

){
    fun toMap(): HashMap<String, Any> {
        return hashMapOf(
            Pair("id", id),
            Pair("senderId", senderId),
            Pair("receiverId", receiverId),
            Pair("status", status),
            Pair("category", category)
        )
    }
}

data class GeneralNotificationDetail(
    var requestId: String,
    var receiveId: String
){
    fun toMap(): HashMap<String, Any> {
        return hashMapOf(Pair("request", requestId), Pair("receive", receiveId))
    }
}