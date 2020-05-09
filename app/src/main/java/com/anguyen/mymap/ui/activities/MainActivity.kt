package com.anguyen.mymap.ui.activities

import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.viewpager.widget.ViewPager
import com.anguyen.mymap.adapters.viewpager.MainFragmentPagerAdapter
import com.anguyen.mymap.R
import com.anguyen.mymap.commons.*
import com.anguyen.mymap.presenter.MainPresenter
import com.anguyen.mymap.ui.fragments.MapFragment
import com.anguyen.mymap.ui.fragments.ProfileFragment
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

    private var selectedFragment: Fragment? = null

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

        setFragmentData()
        setViewPager()

        //Initialize Places Object
        Places.initialize(this, getString(R.string.google_maps_key))

    }

    private fun setViewPager(){
        view_container.adapter = MainFragmentPagerAdapter(
            supportFragmentManager,
            intent.extras!!,
            progressDialog
        )
        view_container.addOnPageChangeListener(this)
    }

    override fun onPageScrolled(position: Int, positionOffset: Float, positionOffsetPixels: Int) {
        when(position){
            0 -> bottom_navigation.selectedItemId = R.id.nav_profile
            1 -> bottom_navigation.selectedItemId = R.id.nav_home
            2 ->{
                showDevelopingFeatureWarning(this){ dialog ->
                    backToPrevious()
                    dialog.dismiss()
                }
            }

            3 ->{
                progressDialog.dismiss()
                showDevelopingFeatureWarning(this){ dialog ->
                    backToPrevious()
                    dialog.dismiss()
                }
            }
        }

    }

    override fun onPageSelected(position: Int) = Unit
    override fun onPageScrollStateChanged(state: Int) = Unit

    private fun setFragmentData(){
        bottom_navigation.onItemSelected {

            selectedFragment = when(it.itemId){

                R.id.nav_home -> MapFragment(progressDialog)

//                R.id.nav_notify ->{ NotificationFragment() }
//
//                R.id.nav_search ->{ SearchFragment() }

                R.id.nav_profile -> ProfileFragment(progressDialog)

                else -> null

            }

            if(selectedFragment != null){
                selectedFragment?.setup(this, R.id.fragment_container, intent.extras!!)
            }else{
                showDevelopingFeatureWarning(this){ dialog ->
                    backToPrevious()
                    dialog.dismiss()
                }
            }

            return@onItemSelected true
        }

    }

    override fun onStop() {
        if(intent.extras?.getString(KEY_USER_TYPE) == KEY_GUEST_USER){
            mPresenter.deleteCurrentGuest()
        }
        super.onStop()
    }

    override fun onDestroy() {
        clearFindViewByIdCache()
        super.onDestroy()
    }

    override fun onDeleteGuestSuccessful() {
        Log.d("Delete Guest User", "Deleted")
    }

}
