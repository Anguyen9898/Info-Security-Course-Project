package com.anguyen.mymap.ui.activities

import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.viewpager.widget.ViewPager
import com.anguyen.mymap.adapters.viewpager.MainFragmentPagerAdapter
import com.anguyen.mymap.R
import com.anguyen.mymap.commons.*
import com.anguyen.mymap.presenter.MainPresenter
import com.anguyen.mymap.ui.views.MainView
import com.google.android.libraries.places.api.Places
import com.kaopiz.kprogresshud.KProgressHUD
import kotlinx.android.synthetic.*
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity(), MainView,  ViewPager.OnPageChangeListener{

    companion object{
       val ACCEPT_ACTION = "Accept"
       val REFUSE_ACTION = "Reject"
       val SHOW_ACTION = "Show"
    }

    private var selectedFragmentIndexBefore = 0

    private lateinit var mPresenter: MainPresenter

    private lateinit var progressDialog: KProgressHUD

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        initUI()
    }

    private fun initUI() {

        mPresenter = MainPresenter(this, this)

        progressDialog = initProgress(this)

        setViewPager()

        bottom_navigation.onItemSelected {
            view_container.currentItem = when(it.itemId){
                R.id.nav_profile -> 0
                R.id.nav_home -> 1
                else -> 2 // R.id.nav_about
            }
        }

        //Initialize Places Object
        Places.initialize(this, getString(R.string.google_maps_key))

    }

    private fun setViewPager(){
        view_container.adapter = MainFragmentPagerAdapter(
            supportFragmentManager,
            intent.extras!!,
            progressDialog
        )

        view_container.currentItem = 1
        bottom_navigation.selectedItemId = R.id.nav_home

        view_container.addOnPageChangeListener(this)
    }

    override fun onPageScrolled(position: Int, positionOffset: Float, positionOffsetPixels: Int) = Unit

    override fun onPageSelected(position: Int) {
        setBottomNavigationSelected(position)
    }
    override fun onPageScrollStateChanged(state: Int) = Unit

    private fun setBottomNavigationSelected(itemIndex: Int){
        bottom_navigation.selectedItemId = when(itemIndex){
            0 -> R.id.nav_profile
            1 -> R.id.nav_home
            else -> R.id.nav_about // itemIndex = 2
        }
    }

    override fun onStop() {
        if(intent.extras?.getString(KEY_USER_TYPE) == KEY_GUEST_USER){
            mPresenter.deleteCurrentGuest()
        }
        super.onStop()
    }

    override fun onBackPressed() {
        //Set BackPress for fragment
        //val fragment = supportFragmentManager.findFragmentById(R.id.view_container)
        (view_container as? IOnBackPressed)?.onBackPressed()?.not().let {
            super.onBackPressed()
        }
    }

    override fun onDestroy() {
        clearFindViewByIdCache()
        super.onDestroy()
    }

    override fun onDeleteGuestSuccessful() {
        Log.d("Delete Guest User", "Deleted")
    }

}
