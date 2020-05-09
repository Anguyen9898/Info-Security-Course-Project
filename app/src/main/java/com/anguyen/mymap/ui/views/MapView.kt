package com.anguyen.mymap.ui.views

import android.location.Location
import com.google.android.gms.maps.model.LatLng

interface MapView: BaseView {

    fun showCurrentLocationOnMap(location: Location?)

    fun showCurrentLocationAddress(location: Location?)

    fun showGPSSettingUI()

    fun showAllUsersLocation(latLng: LatLng?)

    //fun showLocationOnMap(location: Location?)

}