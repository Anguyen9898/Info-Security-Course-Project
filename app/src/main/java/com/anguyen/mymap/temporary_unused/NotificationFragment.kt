package com.anguyen.mymap.temporary_unused

import android.os.Bundle
import android.view.Gravity
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup

import com.anguyen.mymap.R
import com.anguyen.mymap.adapters.notification.NotificationAdapter
import com.mancj.slideup.SlideUp
import com.mancj.slideup.SlideUpBuilder
import kotlinx.android.synthetic.main.notify_item.*


class NotificationFragment : Fragment() {

    private var layoutAnimation: SlideUp? = null

    private val mNotifications = ArrayList<String>()
    private lateinit var mAdapter: NotificationAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_notification, container, false)
    }

    private fun initAnimationObj(): SlideUp? {
        return SlideUpBuilder(layout_button)
            .withStartState(SlideUp.State.HIDDEN)
            .withHideSoftInputWhenDisplayed(true)
            .withStartGravity(Gravity.TOP)
            .build()
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initUI()
    }

    private fun initUI(){
        layoutAnimation = initAnimationObj()
    }



}
