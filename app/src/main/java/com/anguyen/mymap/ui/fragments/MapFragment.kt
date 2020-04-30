package com.anguyen.mymap.ui.fragments

import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.location.Location
import android.os.Bundle
import android.provider.Settings
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast

import com.anguyen.mymap.R
import com.anguyen.mymap.commons.*
import com.anguyen.mymap.presenter.MapPresenter
import com.anguyen.mymap.ui.views.MapView
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import com.google.android.libraries.places.widget.Autocomplete

const val CAMERA_ZOOM_INDEX = 19f

class MapFragment : Fragment(), OnMapReadyCallback, MapView {

    private lateinit var mMapPresenter: MapPresenter
    private var supportMapFragment: SupportMapFragment? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_map, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        //Initialize UI
        initUI()
    }

    /**
     *  This method's to initialize all UI's contents
     */
    private fun initUI(){
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        initMapFragment()

        val userType = activity?.intent?.getStringExtra(KEY_USER_TYPE)
        mMapPresenter = MapPresenter(context!!, this, userType!!)

    }

    private fun initMapFragment(){
        supportMapFragment = childFragmentManager
            .findFragmentById(R.id.map) as? SupportMapFragment

        if(supportMapFragment == null){
            supportMapFragment = SupportMapFragment.newInstance()
            fragmentManager?.beginTransaction()?.replace(R.id.map, supportMapFragment!!)
        }

        supportMapFragment?.getMapAsync(this)
    }

    override fun onMapReady(googleMap: GoogleMap) {
        mMapPresenter.mMap = googleMap
        mMapPresenter.updateMapUI()
    }

    override fun onResume() {
        getLocationPermissionGranted(context!!){
            //Perform if Permission's granted
            mMapPresenter.getLastKnownLocation()
        }
        super.onResume()
    }

    override fun showGPSSettingUI() {
        startActivity(Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS))
    }

    /**
     * Set the map's camera position to the current location of the device.
     */
    override fun showLocationOnMap(location: Location?) {

        if(location != null){
            val latLng = LatLng(location.latitude, location.longitude)

            mMapPresenter.mMap?.moveCamera(
                CameraUpdateFactory
                    .newLatLngZoom(latLng, CAMERA_ZOOM_INDEX)
            )

        }else{
            Toast.makeText(context!!, "Can't Connect", Toast.LENGTH_LONG).show()
        }
    }

    override fun showLocationAddress(location: Location?) {
        Toast.makeText(
            context!!,
            "You're at ${generateLocationToAddress(context!!, location!!)}",
            Toast.LENGTH_LONG
        ).show()
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)

        if(requestCode == PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION){
            // If request is cancelled, the result arrays are empty.
            if (grantResults.isNotEmpty() and (grantResults[0] == PackageManager.PERMISSION_GRANTED)) {
                mMapPresenter.getLastKnownLocation()
            }
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        when(requestCode){
            //Get Selected Place result from Main Activity
             SEARCH_PLACE_REQUEST_CODE ->{
                if(resultCode == Activity.RESULT_OK && data != null){

                    val place =  Autocomplete.getPlaceFromIntent(data)

                    mMapPresenter.mMap?.addMarker(MarkerOptions().position(place.latLng!!)
                        .title("You're at ${place.address}"))
                    mMapPresenter.mMap?.moveCamera(CameraUpdateFactory.newLatLngZoom(place.latLng!!, 19f))

                }
            }
        }
    }

    override fun fireBaseExceptionError(message: String) = showToastByString(context!!, message)

    override fun internetError() = internetErrorDialog(context!!)

}
