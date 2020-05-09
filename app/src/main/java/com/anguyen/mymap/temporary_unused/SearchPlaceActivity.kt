package com.anguyen.mymap.temporary_unused

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.anguyen.mymap.R
import com.anguyen.mymap.commons.showToastByString
import com.anguyen.mymap.ui.activities.MainActivity
import com.google.android.gms.common.api.Status
import com.google.android.libraries.places.api.Places
import com.google.android.libraries.places.api.model.Place
import com.google.android.libraries.places.widget.AutocompleteSupportFragment
import com.google.android.libraries.places.widget.listener.PlaceSelectionListener

class SearchPlaceActivity : AppCompatActivity(), PlaceSelectionListener {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_search_place)

        initPlaceAutoCompleteFragment()
    }

    private fun initPlaceAutoCompleteFragment(){
        Places.initialize(this, getString(R.string.google_maps_key))

        // Initialize the AutocompleteSupportFragment.
        val autocompleteFragment  = supportFragmentManager.findFragmentById(R.id.place_autocomplete)
                as AutocompleteSupportFragment

        autocompleteFragment.apply {
            // Specify the types of place data to return.
            setPlaceFields(arrayListOf(Place.Field.ID, Place.Field.NAME))
            // Set up a PlaceSelectionListener to handle the response.
            setOnPlaceSelectedListener(this@SearchPlaceActivity)
        }
    }

    override fun onPlaceSelected(place: Place) {
        val intent = Intent(this, MainActivity::class.java)
        intent.putExtra("selected_place", place)
    }

    override fun onError(stt: Status) {
        showToastByString(this, stt.statusMessage!!)
    }
}
