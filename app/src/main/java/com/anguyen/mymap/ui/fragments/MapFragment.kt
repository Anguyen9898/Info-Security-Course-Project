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
import com.kaopiz.kprogresshud.KProgressHUD
import com.mancj.slideup.SlideUp
import com.mancj.slideup.SlideUpBuilder
import kotlinx.android.synthetic.main.fragment_map.*

const val CAMERA_ZOOM_INDEX = 19f

class MapFragment(
    private var progressDialog: KProgressHUD? = null

) : Fragment(), OnMapReadyCallback, MapView {

    private lateinit var mMapPresenter: MapPresenter
    private var supportMapFragment: SupportMapFragment? = null

    private var layoutAnimation: SlideUp? = null

    private var userType: String? = ""

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_map, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        //progressDialog? = initProgress(context!!)
        progressDialog?.show()
        //Initialize UI
        initUI()
    }

    /**
     *  This method's to initialize all UI's contents
     */
    private fun initUI(){
        layoutAnimation = initAnimationObj()

        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        initMapFragment()

        Places.initialize(context!!, getString(R.string.google_maps_key))

        userType = this.arguments?.getString(KEY_USER_TYPE)

        mMapPresenter = MapPresenter(
            context!!,
            this,
            userType!!,
            progressDialog!!,
            fragmentManager!!
        )

        toolbar.onItemClick { toolbarItemClickedHandler(it) }

        btn_move_to_marker.onClick {
            mMapPresenter.moveCameraToMarkerLocation()
            mMapPresenter.drawPrimaryLinePath()
        }
    }

    private fun initAnimationObj(): SlideUp? {
        return SlideUpBuilder(btn_move_to_marker)
            .withStartState(SlideUp.State.HIDDEN)
            .withStartGravity(Gravity.BOTTOM)
            .build()
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

            R.id.show_users_location -> {
                if(!isGuest()){
                    mMapPresenter.showUsersLocationMenuOptionClicked()
                }else{
                    errorDialog(context!!, getString(R.string.error_title), getString(R.string.error_guest))
                }

            }

            R.id.clear -> {
                mMapPresenter.mMap?.clear()

                if(layoutAnimation?.isVisible!!){
                    layoutAnimation?.toggle()
                }
            }

            R.id.exit_button -> activity?.finish()

        }
    }

    private fun isGuest(): Boolean{
        return userType == KEY_GUEST_USER
    }

    override fun onResume() {
        progressDialog?.dismiss()

        getLocationPermissionGranted(context!!){
            //Perform if Permission's granted
            mMapPresenter.getLastKnownLocation()
        }
        super.onResume()
    }

    override fun showGPSSettingUI() {
        progressDialog?.dismiss()
        startActivity(Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS))
    }

    /**
     * Set the map's camera position to the current location of the device.
     */
    override fun showCurrentLocationOnMap(location: Location?) {
        progressDialog?.dismiss()
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

    override fun showCurrentLocationAddress(location: Location?) {
        progressDialog?.dismiss()
        Toast.makeText(
            context!!,
            "You're at ${convertLocationToAddress(context!!, location!!)}",
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

                     val latLng = Autocomplete.getPlaceFromIntent(data)

                     if(latLng != null){
                         showToastByString(context!!, latLng.address!!)
                     }else{
                         showToastByString(context!!, getString(R.string.general_failed_message))
                     }


                 } else if (resultCode == AutocompleteActivity.RESULT_ERROR) {

                     val status = Autocomplete.getStatusFromIntent(data!!)

                     Log.i("Get Place", status.statusMessage!!)
                     showToastByString(context!!, status.statusMessage!!)

                 }else{
                     showToastByString(context!!, getString(R.string.general_failed_message))
                 }
             }
        }
    }

    override fun showAllUsersLocation(userId: String, latLng: LatLng?) {
        mMapPresenter.mMap?.addMarker(MarkerOptions().position(latLng!!).title(userId))
        layoutAnimation?.show()
    }

    override fun fireBaseExceptionError(message: String) {
        progressDialog?.dismiss()
        showFirebaseError(context!!, message)
    }

    override fun internetError() {
        progressDialog?.dismiss()
        showInternetErrorDialog(context!!)
    }

}
