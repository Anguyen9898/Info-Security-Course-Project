package com.anguyen.mymap.models

import com.anguyen.mymap.commons.KEY_LATITUDE
import com.anguyen.mymap.commons.KEY_LONGITUDE

data class CoordinateDetail(
    var latitude: Double = 0.0,
    var longitude: Double = 0.0
){
    fun toMap(): HashMap<String, Any?> {
        return hashMapOf(Pair(KEY_LATITUDE, latitude), Pair(KEY_LONGITUDE, longitude))
    }
}