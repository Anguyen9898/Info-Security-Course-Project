package com.anguyen.mymap.temporary_unused

import com.anguyen.mymap.models.CoordinateDetail

data class FollowingDetail(
    val id: String = "",
    val username: String = "",
    val location: CoordinateDetail
)

data class FollowerDetail(
    val id: String = "",
    val username: String = ""
)