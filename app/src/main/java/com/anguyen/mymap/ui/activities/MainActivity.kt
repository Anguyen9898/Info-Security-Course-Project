package com.anguyen.mymap.ui.activities

import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import com.anguyen.mymap.R
import com.anguyen.mymap.commons.onItemSelected
import com.anguyen.mymap.commons.setup
import com.anguyen.mymap.presenter.MainPresenter
import com.anguyen.mymap.ui.fragments.MapFragment
import com.anguyen.mymap.ui.fragments.NotificationFragment
import com.anguyen.mymap.ui.fragments.ProfileFragment
import com.anguyen.mymap.ui.fragments.SearchFragment
import com.anguyen.mymap.ui.views.MainView
import com.google.android.libraries.places.api.Places
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity(), MainView {

    private lateinit var selectedFragment: Fragment

    private lateinit var mPresenter: MainPresenter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        initUI()
    }

    private fun initUI() {

        MapFragment().setup(this, R.id.fragment_container, intent.extras!!)

        mPresenter = MainPresenter(this, this)

        //Fragment
        setFragmentData()

        //Initialize Places Object
        Places.initialize(this, getString(R.string.google_maps_key))

    }

    private fun setFragmentData(){
        bottom_navigation.onItemSelected {
            selectedFragment = when(it.itemId){

                R.id.nav_home ->{ MapFragment() }

                R.id.nav_notify ->{ NotificationFragment() }

                R.id.nav_search ->{ SearchFragment() }

                else ->{ ProfileFragment() }

            }

            selectedFragment.setup(this, R.id.fragment_container, intent.extras!!)

            return@onItemSelected true
        }

    }

    override fun onDestroy() {
        super.onDestroy()
        mPresenter.deleteCurrentGuest()
    }

    override fun onDeleteGuestSuccessful() {
        Log.d("Delete Guest User", "Deleted")
    }

}
