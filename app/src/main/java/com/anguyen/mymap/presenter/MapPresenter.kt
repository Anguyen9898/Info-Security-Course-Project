package com.anguyen.mymap.presenter

import android.app.Activity
import android.content.Context
import android.location.Location
import android.util.Log
import androidx.fragment.app.FragmentManager
import com.anguyen.mymap.R
import com.anguyen.mymap.commons.*
import com.anguyen.mymap.direction_helper.FetchURL
import com.anguyen.mymap.direction_helper.TaskLoadedCallback
import com.anguyen.mymap.firebase_managers.FirebaseAuthenticationManager
import com.anguyen.mymap.firebase_managers.FirebaseDataManager
import com.anguyen.mymap.models.CoordinateDetail
import com.anguyen.mymap.models.alphaBetDetail
import com.anguyen.mymap.ui.fragments.BottomSheetDialogProfile
import com.anguyen.mymap.ui.fragments.CAMERA_ZOOM_INDEX
import com.anguyen.mymap.ui.views.MapView
import com.google.android.gms.location.LocationServices
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.model.*
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
import com.kaopiz.kprogresshud.KProgressHUD

class MapPresenter(
    private val mContext: Context?,
    private val mMapView: MapView?,
    private val mUserType: String,
    private val progressDialog: KProgressHUD,
    private val fragmentManager: FragmentManager

): GoogleMap.OnMyLocationClickListener, GoogleMap.OnMarkerClickListener, TaskLoadedCallback{

    var mMap: GoogleMap? = null

    private val markers = ArrayList<LatLng>()
    private val userIds = ArrayList<String>()

    private var markerIndex = 0

    private lateinit var deviceLocation: LatLng
    private lateinit var destination: LatLng
    private var mPolyline: Polyline? = null

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

                setOnMarkerClickListener(this@MapPresenter)
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

                        deviceLocation = LatLng(location?.latitude!!, location.longitude)

                        if(mUserType != KEY_GUEST_USER) { //Disable this function if user type is guest

                            database.updateLocationFieldOnDatabase(
                                authentication.getCurrentUserId(),

                                CoordinateDetail(
                                    encodedCoordinate(location.latitude.toString()),
                                    encodedCoordinate(location.longitude.toString())
                                )
                            )
                        }

                    }
                }

            }catch (e: SecurityException) {
                Log.e("Exception: %s", e.message!!)
            }

        }

    }

    private fun encodedCoordinate(coordinateIndex: String): String {
        return coordinateIndex.encoding(ENCODING_KEY)
    }

    private fun decodedStringToCoordinate(latitudeStr: String, longitudeStr: String): LatLng {
        return LatLng(
            latitudeStr.convertCharToNumberString().toDouble(),
            longitudeStr.convertCharToNumberString().toDouble()
        )
    }

    private fun String.convertCharToNumberString(): String{
        var converted = ""
        this.decoding(ENCODING_KEY).forEach {
            converted += if(alphaBetDetail.indexOf(it.toString()) >= 0){
                alphaBetDetail.indexOf(it.toString()).toString()
            }else{
                it.toString()
            }
        }

        return  converted
    }

    override fun onMyLocationClick(location: Location) {
        progressDialog.show()
        mMapView?.showCurrentLocationAddress(location)
    }

    fun showUsersLocationMenuOptionClicked(){
        database.getUsersLocationData(authentication.getCurrentUserId()){ userId, location ->
            val latLng = decodedStringToCoordinate(location.latitude, location.longitude)

            mMapView?.showAllUsersLocation(userId, latLng)

            markers.add(latLng)
            userIds.add(userId)
        }
    }

    fun moveCameraToMarkerLocation(){
        if(markers.size <= 0){
            return
        }

        if(markerIndex >= markers.size){
            markerIndex = 0
        }

        mMap?.moveCamera(CameraUpdateFactory.newLatLngZoom(markers[markerIndex], CAMERA_ZOOM_INDEX))
        destination = markers[markerIndex]

        markerIndex++

    }

    fun drawPrimaryLinePath(){
        val url = getLocationUrl(deviceLocation, destination, "driving")
        FetchURL(this).execute(url, "driving")
    }

    private fun getLocationUrl(origin: LatLng, destination: LatLng, directionMode: String): String {
        //Origin of route
        val strOrigin = "origin=${origin.latitude},${origin.longitude}"
        //Destination of route
        val strDes = "destination=${destination.latitude},${destination.longitude}"
        //Mode
        val mode = "mode=$directionMode"
        //Building the parameters to the web service
        val parameter = "$strOrigin&$strDes&$mode"
        //Output format
        val output = "json"

        return "http://maps.googleapis.com/maps/api/directions/$output?$parameter&key=${mContext?.getString(
            R.string.google_maps_key
        )}"
    }

    override fun onTaskDone(vararg values: Any?) {
        if(mPolyline != null){
            mPolyline?.remove()
        }

        mPolyline = mMap?.addPolyline(values[0] as PolylineOptions)
    }

    override fun onMarkerClick(marker: Marker?): Boolean {
        userIds.forEach { userId ->
            if(marker?.title == userId){
                val userProfileDialog = BottomSheetDialogProfile(userId, progressDialog)

                userProfileDialog.show(fragmentManager, "dialog")
            }
        }
        return true
    }

}