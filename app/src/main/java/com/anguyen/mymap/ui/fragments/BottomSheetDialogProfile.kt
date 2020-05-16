package com.anguyen.mymap.ui.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.anguyen.mymap.R
import com.anguyen.mymap.commons.*
import com.anguyen.mymap.models.UserRespondDetail
import com.anguyen.mymap.presenter.BottomProfileDialogPresenter
import com.anguyen.mymap.ui.views.BottomDialogProfileView
import com.bumptech.glide.Glide
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.kaopiz.kprogresshud.KProgressHUD
import kotlinx.android.synthetic.main.fragment_bottom_dialog_profile.*

class BottomSheetDialogProfile(
    private val userId: String,
    private val progressDialog: KProgressHUD?,
    private val distance: Float
) : BottomSheetDialogFragment(), BottomDialogProfileView {


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_bottom_dialog_profile, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val presenter = BottomProfileDialogPresenter(this)

        presenter.setupProfile(userId)
    }

    override fun onResult(userData: UserRespondDetail?) {
        setView(userData)
    }

    private fun setView(userData: UserRespondDetail?){
        Glide.with(context!!).load(userData?.avatarUrl).into(dialog_img_profile)

        dialog_txt_username.text = userData?.username
        dialog_edt_gender.setText(userData?.gender)
        dialog_edt_phone_number.setText(userData?.phoneNumber)
        dialog_edt_email.setText(userData?.email)

        setupDistance()
        setupTimeTravel()

        setTextIfAnyEditTextEmpty()
    }

    private fun setupTimeTravel(){
        val time = ((distance/1000)/averageVelocity).toInt()
        var strTime = "$time min"

        if(time > 60){
            strTime = convertTime(time)
        }

        txt_timer.text = strTime
    }

    private fun convertTime(time: Int): String{
        return "${time/60}h ${time%60}min"
    }

    private fun convertToHour(minute: Float) = minute/60

    private fun setupDistance(){
        val distanceFormat = "%.2f"

        if(distance < 1000f){
            txt_distance.text = distanceFormat.format(distance)
            txt_distance_unit.text = getString(R.string.meter_unit)
        }

        if(distance > 1000f){
            txt_distance.text = distanceFormat.format(distance/1000)
            txt_distance_unit.text = getString(R.string.km_unit)
        }

    }

    private fun setTextIfAnyEditTextEmpty(){
        arrayOf(dialog_txt_username, dialog_edt_gender, dialog_edt_phone_number, dialog_edt_email).forEach {
            if(isFieldEmpty(it.text.toString())){
                it.text = KEY_EMPTY_DATA
            }
        }
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
