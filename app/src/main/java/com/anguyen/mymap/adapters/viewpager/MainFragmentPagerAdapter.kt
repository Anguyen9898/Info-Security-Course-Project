package com.anguyen.mymap.adapters.viewpager

import android.os.Bundle
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentStatePagerAdapter
import com.anguyen.mymap.ui.fragments.AboutFragment
import com.anguyen.mymap.ui.fragments.MapFragment
import com.anguyen.mymap.ui.fragments.ProfileFragment
import com.kaopiz.kprogresshud.KProgressHUD

class MainFragmentPagerAdapter(
    supportFragment: FragmentManager,
    private val argument: Bundle,
    private var progressDialog: KProgressHUD
)
    : FragmentStatePagerAdapter(supportFragment, BEHAVIOR_RESUME_ONLY_CURRENT_FRAGMENT){

    override fun getItem(position: Int): Fragment {
        val fragment = newInstance(position)!!
        fragment.arguments = argument
        return fragment
    }

    private fun newInstance(i: Int): Fragment?{
        return when(i){
            0 -> ProfileFragment(progressDialog)
            1 -> MapFragment(progressDialog)
            else -> AboutFragment(progressDialog) // i = 2
        }
    }

    override fun getCount() = 3

}