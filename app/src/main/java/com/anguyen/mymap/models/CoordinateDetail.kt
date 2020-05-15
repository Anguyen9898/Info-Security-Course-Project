package com.anguyen.mymap.models

import com.anguyen.mymap.commons.KEY_LATITUDE
import com.anguyen.mymap.commons.KEY_LONGITUDE

data class CoordinateDetail(
    var latitude: String = "",
    var longitude: String = ""
){
    fun toMap(): HashMap<String, Any?> {
        return hashMapOf(Pair(KEY_LATITUDE, latitude), Pair(KEY_LONGITUDE, longitude))
    }
}