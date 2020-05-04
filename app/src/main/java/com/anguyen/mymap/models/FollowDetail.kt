package com.anguyen.mymap.models

data class FollowingDetail(
    val id: String = "",
    val username: String = "",
    val location: CoordinateDetail
)

data class FollowerDetail(
    val id: String = "",
    val username: String = ""
)