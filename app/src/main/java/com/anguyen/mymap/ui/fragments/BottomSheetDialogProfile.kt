package com.anguyen.mymap.ui.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.DialogFragment
import com.anguyen.mymap.R
import com.anguyen.mymap.commons.KEY_EMPTY_DATA
import com.anguyen.mymap.commons.isFieldEmpty
import com.anguyen.mymap.commons.showFirebaseError
import com.anguyen.mymap.commons.showInternetErrorDialog
import com.anguyen.mymap.models.UserRespondDetail
import com.anguyen.mymap.presenter.BottomProfileDialogPresenter
import com.anguyen.mymap.ui.views.BottomDialogProfileView
import com.bumptech.glide.Glide
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.kaopiz.kprogresshud.KProgressHUD
import kotlinx.android.synthetic.main.fragment_bottom_dialog_profile.*
import kotlinx.android.synthetic.main.fragment_profile.*
import kotlinx.android.synthetic.main.fragment_profile.edt_email
import kotlinx.android.synthetic.main.fragment_profile.txt_username
import kotlinx.android.synthetic.main.layout_email_login_bottom_dialog.*

class BottomSheetDialogProfile(
    private val userId: String,
    private val progressDialog: KProgressHUD?
) : BottomSheetDialogFragment(), BottomDialogProfileView {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setStyle(DialogFragment.STYLE_NORMAL, R.style.BottomSheetDialog)
    }

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
        Glide.with(context!!).load(userData?.userTypeImgUrl).into(dialog_img_user_type)
        Glide.with(context!!).load(userData?.avatarUrl).into(dialog_img_profile)

        dialog_txt_username.text = userData?.username
        dialog_edt_gender.setText(userData?.gender)
        dialog_edt_phone_number.setText(userData?.phoneNumber)
        dialog_edt_email.setText(userData?.email)

        setTextIfAnyEditTextEmpty()
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
