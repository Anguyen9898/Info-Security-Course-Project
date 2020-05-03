package com.anguyen.mymap.presenter

import android.app.Activity
import android.content.Context
import com.anguyen.mymap.firebase_managers.authentication.FirebaseAuthManager
import com.anguyen.mymap.firebase_managers.databases.FirebaseDataManager
import com.anguyen.mymap.ui.views.ProfileFragmentView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase

class ProfileFragmentPresenter constructor(
    private val mContext: Context,
    private val mView: ProfileFragmentView?,
    private val userType: String) {

    private val authentication = FirebaseAuthManager(FirebaseAuth.getInstance(), mContext as Activity)
    private val database = FirebaseDataManager(FirebaseDatabase.getInstance())

    fun onSettingProfile(){
        database.getUserProfile(userType, authentication){
            mView?.showUserInfo(it!!)
        }
    }

    fun logout() = authentication.emailLogout { mView?.openLoginUI() }

    fun onRevokeClicked() {
        authentication.revokeGoogleAccount { isSuccessful ->
            if(isSuccessful){
                mView?.onRevokeSuccess()
            }
        }
    }

    fun onFacebookLogoutButtonClicked() = authentication.facebookLogout { mView?.openLoginUI() }

}