package com.anguyen.mymap.ui.fragments

import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.location.Location
import android.os.Bundle
import android.provider.Settings
import android.util.Log
import android.view.*
import androidx.fragment.app.Fragment
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
import com.google.android.libraries.places.api.Places
import com.google.android.libraries.places.api.model.Place
import com.google.android.libraries.places.widget.Autocomplete
import com.google.android.libraries.places.widget.AutocompleteActivity
import com.google.android.libraries.places.widget.model.AutocompleteActivityMode
import kotlinx.android.synthetic.main.fragment_map.*

const val CAMERA_ZOOM_INDEX = 19f

class MapFragment : Fragment(), OnMapReadyCallback, MapView {

    private lateinit var mMapPresenter: MapPresenter
    private var supportMapFragment: SupportMapFragment? = null

    private var latLngSearchResult: Place? = null
    private var isSearchingPlace = false

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

        Places.initialize(context!!, getString(R.string.google_maps_key))

        val userType = this.arguments?.getString(KEY_USER_TYPE)
        mMapPresenter = MapPresenter(context!!, this, userType!!)

        toolbar.onItemClick { toolbarItemClickedHandler(it) }
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

    private fun toolbarItemClickedHandler(menuItem: MenuItem){
        when (menuItem.itemId) {
            R.id.search_button -> {
                val intent = Autocomplete.IntentBuilder(
                    AutocompleteActivityMode.FULLSCREEN,
                    listOf(Place.Field.NAME, Place.Field.LAT_LNG)
                ).build(context!!)

                startActivityForResult(intent, SEARCH_PLACE_REQUEST_CODE)
            }
        }
    }

    override fun onResume() {
        getLocationPermissionGranted(context!!){
            if(isSearchingPlace and (latLngSearchResult != null)) {
                moveToThisLocation(latLngSearchResult!!)
            }else{
                //Perform if Permission's granted
                mMapPresenter.getLastKnownLocation()
            }

        }
        super.onResume()
    }

    private fun moveToThisLocation(place: Place){
        mMapPresenter.mMap?.addMarker(
            MarkerOptions().position(place.latLng!!)
                .title("You're at ${place.address}")
        )
        mMapPresenter.mMap?.moveCamera(
            CameraUpdateFactory.newLatLngZoom(
                place.latLng!!,
                19f
            )
        )
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
             SEARCH_PLACE_REQUEST_CODE -> {
                 if (resultCode == Activity.RESULT_OK && data != null) {
                     latLngSearchResult = Autocomplete.getPlaceFromIntent(data)
                     isSearchingPlace = true
                     onResume()

                 } else if (resultCode == AutocompleteActivity.RESULT_ERROR) {
                     val status = Autocomplete.getStatusFromIntent(data!!)

                     Log.i("Get Place", status.statusMessage!!)
                     showToastByString(context!!, status.statusMessage!!)
                 }
             }
        }
    }

    override fun fireBaseExceptionError(message: String) = showToastByString(context!!, message)

    override fun internetError() = showInternetErrorDialog(context!!)

}
