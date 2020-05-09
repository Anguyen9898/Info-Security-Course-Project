package com.anguyen.mymap.ui.fragments

import android.content.Context
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

class ProfileFragment(private var progressDialog: KProgressHUD) : Fragment(), ProfileFragmentView, RevokeDialogFragment.RevokeDialogListener {

    private lateinit var mPresenter: ProfileFragmentPresenter
    private lateinit var userType: String

    private var layoutAnimation: SlideUp? = null
    //private lateinit var progressDialog: KProgressHUD

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_profile, container, false)

    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        //progressDialog = initProgress(context!!)

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
        mPresenter.onSettingProfile()

        setupLogoutButton()

        //Button clicked handler
        btn_edit_profile.onClick {
            setEditable(true)
            layoutAnimation?.show()
        }

        btn_save.onClick {
            setEditable(false)
            layoutAnimation?.toggle()
        }

        btn_cancel.onClick {
            setEditable(false)
            layoutAnimation?.toggle()
        }

    }

    private fun setEditable(boolean: Boolean){
        arrayOf(edt_username,edt_email, edt_gender, edt_phone_number).forEach {
            it.isEnabled = boolean
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
            progressDialog.dismiss()
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
                    progressDialog.show()
                    mPresenter.logout()
                }
            }

            KEY_GOOGLE_USER -> {
                btn_facebook_logout.visibility = View.GONE

                btn_logout.visibility = View.VISIBLE
                btn_logout.onClick {
                    progressDialog.show()

                    RevokeDialogFragment(this, progressDialog)
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
                    progressDialog.show()
                    mPresenter.deleteCurrentGuest()
                }
            }

        }

    }

    override fun onRevokeConfirmedListener(isChecked: Boolean) {
        if(isChecked){
            mPresenter.onRevokeClicked()
        }
        mPresenter.logout()
    }

    override fun openLoginUI() {
        progressDialog.dismiss()
        startActivity(Intent(activity, LoginActivity::class.java))
        activity?.finish()
    }

    override fun onRevokeSuccess() {
        showToastByString(context!!, "Revoke Account Successfully")
    }

    override fun fireBaseExceptionError(message: String) {
        progressDialog.dismiss()
        showFirebaseError(context!!, message)
    }

    override fun internetError() {
        progressDialog.dismiss()
        showInternetErrorDialog(context!!)
    }

}
