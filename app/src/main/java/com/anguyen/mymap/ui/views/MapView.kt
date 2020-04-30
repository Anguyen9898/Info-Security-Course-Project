package com.anguyen.mymap.ui.views

import android.location.Location

interface MapView: BaseView {

    fun showLocationOnMap(location: Location?)

    fun showLocationAddress(location: Location?)

    fun showGPSSettingUI()

    //fun showLocationOnMap(location: Location?)

}