package com.anguyen.mymap.ui.activities

import android.app.Activity
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.anguyen.mymap.R
import com.anguyen.mymap.commons.SEARCH_PLACE_REQUEST_CODE
import com.anguyen.mymap.commons.onItemClick
import com.anguyen.mymap.commons.onItemSelected
import com.anguyen.mymap.commons.setup
import com.anguyen.mymap.ui.fragments.MapFragment
import com.anguyen.mymap.ui.fragments.ProfileFragment
import com.google.android.libraries.places.api.Places
import com.google.android.libraries.places.api.model.Place
import com.google.android.libraries.places.widget.Autocomplete
import com.google.android.libraries.places.widget.AutocompleteSupportFragment
import com.google.android.libraries.places.widget.model.AutocompleteActivityMode
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {

    private lateinit var selectedFragment: Fragment

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        initUI()
    }

    private fun initUI() {
        //getPublisher()
        MapFragment().setup(this, R.id.fragment_container)

        //Fragment
        setFragmentData()

        //Initialize Places Object
        Places.initialize(this, getString(R.string.google_maps_key))

        toolbar.onItemClick {
            when (it.itemId) {
                R.id.search_button -> {
                    val intent = Autocomplete.IntentBuilder(AutocompleteActivityMode.FULLSCREEN,
                            listOf(Place.Field.ID, Place.Field.NAME)).build(this)

                    startActivityForResult(intent, SEARCH_PLACE_REQUEST_CODE)
                }
            }
        }
    }

//    private fun getPublisher(){
//        if(mExtras != null){
//            val publisher = mExtras.getString("publisher_id")
//            mEditor.put("profile_id", publisher)
//        }
//
//        HomeFragment().setup(R.id.fragment_container)
//    }

    private fun setFragmentData(){
        bottom_navigation.onItemSelected {
            selectedFragment = when(it.itemId){

                R.id.nav_home ->{ MapFragment() }

                //R.id.nav_notify ->{ ProfileFragment() }

                //R.id.nav_messenger ->{ ProfileFragment() }

                else ->{ ProfileFragment() }

            }

            selectedFragment.setup(this, R.id.fragment_container)

            return@onItemSelected true
        }

    }

}
