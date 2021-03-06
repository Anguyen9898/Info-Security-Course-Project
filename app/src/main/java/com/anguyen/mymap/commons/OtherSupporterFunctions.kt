package com.anguyen.mymap.commons

import android.content.Context
import android.location.Geocoder
import android.location.Location
import android.view.Gravity
import com.mancj.slideup.SlideUp
import com.mancj.slideup.SlideUpBuilder
import kotlinx.android.synthetic.main.activity_login.*
import java.util.*

fun convertLocationToAddress(from: Context, location: Location): String{

    val geoCoder = Geocoder(from, Locale.getDefault())
    val address = geoCoder.getFromLocation(
        location.latitude,
        location.longitude,
        1   // Here 1 represent max location result to returned, by documents it recommended 1 to 5
    )

    return "${address[0].getAddressLine(0)}," +
            " ${address[0].locality}," +
            " ${address[0].adminArea}" +
            " ${address[0].countryName}"+
            " ${address[0].postalCode}"+
            " ${address[0].featureName}"

}
