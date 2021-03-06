package com.anguyen.mymap.ui.fragments

import android.content.Intent
import android.os.Bundle
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.anguyen.mymap.R
import com.anguyen.mymap.commons.*
import com.anguyen.mymap.models.UserRespondDetail
import com.anguyen.mymap.presenter.ProfileFragmentPresenter
import com.anguyen.mymap.ui.activities.LoginActivity
import com.anguyen.mymap.ui.views.ProfileFragmentView
import com.bumptech.glide.Glide
import com.kaopiz.kprogresshud.KProgressHUD
import com.mancj.slideup.SlideUp
import com.mancj.slideup.SlideUpBuilder
import kotlinx.android.synthetic.main.fragment_profile.*

class ProfileFragment(
    private var progressDialog: KProgressHUD? = null

) : Fragment(), ProfileFragmentView, IOnBackPressed, RevokeDialogFragment.RevokeDialogListener {

    private lateinit var mPresenter: ProfileFragmentPresenter
    private lateinit var userType: String

    private var layoutAnimation: SlideUp? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_profile, container, false)

    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        progressDialog?.show()
        initUI()
    }

    private fun initAnimationObj(): SlideUp? {
        return SlideUpBuilder(hidden_layout)
            .withStartState(SlideUp.State.HIDDEN)
            .withStartGravity(Gravity.BOTTOM)
            .build()
    }

    private fun initUI(){

        //Set Animation Object
        layoutAnimation = initAnimationObj()

        //Set profile's editable status
        //setEditable(false)

        userType = this.arguments?.getString(KEY_USER_TYPE)!!

        //Initialize presenter
        mPresenter = ProfileFragmentPresenter(activity!!, this)
        mPresenter.setupProfile()

        setupLogoutButton()

        //Button clicked handler
        btn_edit_profile.onClick {
            if(userType == KEY_EMAIL_USER){
                setEditable(true)
                layoutAnimation?.show()
            }else{
                errorDialog(context!!, getString(R.string.error_title), "This feature is not support your account's type")
            }
        }

        btn_save.onClick {
            progressDialog?.show()

            setEditable(false)
            layoutAnimation?.toggle()

            mPresenter.updateData(
                edt_username.text.toString(),
                edt_gender.text.toString(),
                edt_phone_number.text.toString()
            )
        }

        edt_gender.onClick {
            BottomDialogGenderChooserFragment(edt_gender).show(fragmentManager!!, "dialog")
        }

        btn_cancel.onClick {
            setEditable(false)
            layoutAnimation?.toggle()
        }

    }

    private fun setEditable(boolean: Boolean){
        if(boolean){
            arrayOf(edt_username, edt_gender, edt_phone_number).forEach {
                it.isEnabled = boolean
            }
        }else{
            arrayOf(edt_username, edt_gender, edt_email, edt_phone_number).forEach {
                it.isEnabled = boolean
            }
        }
    }

    override fun showUserInfo(userRespondDetail: UserRespondDetail) {
        if(isAdded){
            if(isFieldEmpty(userRespondDetail.avatarUrl)
                and isFieldEmpty(userRespondDetail.userTypeImgUrl)
            ){ //Setup guest user's image
                Glide.with(this).load(DEFAULT_AVATAR_URL).into(img_profile)
                Glide.with(this).load(GUEST_USER_TYPE_URL).into(img_user_type)

            }else{

                Glide.with(this).load(userRespondDetail.avatarUrl).into(img_profile)
                Glide.with(this).load(userRespondDetail.userTypeImgUrl).into(img_user_type)

            }

            txt_username.text = userRespondDetail.username
            txt_id.text = userRespondDetail.id

            edt_username.setText(userRespondDetail.username)
            edt_gender.setText(userRespondDetail.gender)
            edt_email.setText(userRespondDetail.email)
            edt_phone_number.setText(userRespondDetail.phoneNumber)

            setTextIfAnyEditTextEmpty()

            setEditable(false)
            progressDialog?.dismiss()
        }
    }

    private fun setTextIfAnyEditTextEmpty(){
        arrayOf(txt_username, txt_id, edt_username, edt_email, edt_gender, edt_phone_number).forEach {
            if(isFieldEmpty(it.text.toString())){
                it.text = KEY_EMPTY_DATA
            }
        }
    }

    private fun setupLogoutButton(){

        when (userType) {

            KEY_EMAIL_USER -> {
                btn_facebook_logout.visibility = View.GONE

                btn_logout.visibility = View.VISIBLE
                btn_logout.onClick {
                    progressDialog?.show()
                    mPresenter.logout()
                }
            }

            KEY_GOOGLE_USER -> {
                btn_facebook_logout.visibility = View.GONE

                btn_logout.visibility = View.VISIBLE
                btn_logout.onClick {
                    progressDialog?.show()

                    RevokeDialogFragment(this, progressDialog!!)
                        .show(childFragmentManager, "dialog")
                }
            }

            KEY_FACEBOOK_USER -> {
                btn_facebook_logout.visibility = View.VISIBLE
                btn_facebook_logout.onClick { mPresenter.onFacebookLogoutButtonClicked() }

                btn_logout.visibility = View.GONE
            }

            KEY_GUEST_USER ->{
                btn_facebook_logout.visibility = View.GONE

                btn_logout.visibility = View.VISIBLE
                btn_logout.onClick {
                    progressDialog?.show()
                    mPresenter.deleteCurrentGuest()
                }
            }

        }

    }

    override fun onRevokeConfirmedListener(isChecked: Boolean) {
        progressDialog?.dismiss()
        if(isChecked){
            mPresenter.onRevokeClicked()
        }
        mPresenter.logout()
    }

    override fun openLoginUI() {
        progressDialog?.dismiss()
        startActivity(Intent(activity, LoginActivity::class.java))
        activity?.finish()
    }

    override fun fireBaseExceptionError(message: String) {
        progressDialog?.dismiss()
        showFirebaseError(context!!, message)
    }

    override fun onProfileUpdateSuccessfully() {
        mPresenter.setupProfile()
        progressDialog?.dismiss()
        showToastByString(context!!, "Update Successfully!")
    }

    override fun onProfileUpdateFailed() {
        progressDialog?.dismiss()
        showToastByString(context!!, getString(R.string.general_failed_message))
    }

    override fun internetError() {
        progressDialog?.dismiss()
        showInternetErrorDialog(context!!)
    }

    override fun onBackPressed(): Boolean {
        return if(layoutAnimation?.isVisible!!){
            layoutAnimation?.toggle()
            true
        }else {
            false
        }
    }

}
