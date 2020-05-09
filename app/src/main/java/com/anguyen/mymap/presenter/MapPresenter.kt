package com.anguyen.mymap.presenter

import android.app.Activity
import android.content.Context
import android.location.Location
import android.util.Log
import com.anguyen.mymap.commons.*
import com.anguyen.mymap.firebase_managers.FirebaseAuthenticationManager
import com.anguyen.mymap.firebase_managers.FirebaseDataManager
import com.anguyen.mymap.models.CoordinateDetail
import com.anguyen.mymap.ui.views.MapView
import com.google.android.gms.location.LocationServices
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.model.LatLng
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
import com.kaopiz.kprogresshud.KProgressHUD

class MapPresenter(
    private val mContext: Context?,
    private val mMapView: MapView?,
    private val mUserType: String,
    private val stopProgress: Unit
): GoogleMap.OnMyLocationClickListener{

    var mMap: GoogleMap? = null

    private val mFusedLocationProviderClient = LocationServices
        .getFusedLocationProviderClient(mContext!!)

    private val authentication =
        FirebaseAuthenticationManager(
            FirebaseAuth.getInstance(),
            mContext as Activity
        )
    private val database =
        FirebaseDataManager(FirebaseDatabase.getInstance())

    fun updateMapUI() {
        if (mMap == null) {
            return
        }

        try{
            if(isLocationPermissionGranted(mContext!!)){
                setMapSetting(true)
            }else{
                setMapSetting(false)
            }
        }catch (e: SecurityException){
            Log.e("Exception: %s", e.message!!)
        }
    }

    private fun setMapSetting(isSet: Boolean){
        mMap?.apply {
            isMyLocationEnabled = isSet

            uiSettings?.isMyLocationButtonEnabled = isSet

            if(isSet){ // isSet == true
                mapType = GoogleMap.MAP_TYPE_NORMAL

                clear() //clear old markers

                setOnMyLocationClickListener(this@MapPresenter)
            }

        }
    }

    fun getLastKnownLocation(){

        if(!isGPSEnabled(mContext!!)){
            showLocationErrorDialog(mContext) { mMapView?.showGPSSettingUI() }
        }else{

            try{
                mFusedLocationProviderClient.lastLocation.addOnCompleteListener {
                    if(it.isSuccessful) {

                        val location = it.result

                        mMapView?.showCurrentLocationOnMap(location)

                        if(mUserType != KEY_GUEST_USER) { //Disable this function if user type is guest
                            database.updateLocationFieldOnDatabase(
                                authentication.getCurrentUserId(),
                                CoordinateDetail(location!!.latitude, location.longitude)
                            )
                        }

                    }
                }

            }catch (e: SecurityException) {
                Log.e("Exception: %s", e.message!!)
            }

        }

    }

    override fun onMyLocationClick(location: Location) {
        stopProgress
        mMapView?.showCurrentLocationAddress(location)
    }

    fun showUsersLocationMenuOptionClicked(){
        database.getUsersLocationData(authentication.getCurrentUserId()){
            mMapView?.showAllUsersLocation(LatLng(it.latitude, it.longitude))
        }
    }

}