package com.anguyen.mymap.presenter

import android.app.Activity
import com.anguyen.mymap.commons.KEY_GENDER
import com.anguyen.mymap.commons.KEY_PHONE
import com.anguyen.mymap.commons.KEY_USER
import com.anguyen.mymap.commons.KEY_USER_NAME
import com.anguyen.mymap.firebase_managers.FirebaseAuthenticationManager
import com.anguyen.mymap.firebase_managers.FirebaseDataManager
import com.anguyen.mymap.models.UserRespondDetail
import com.anguyen.mymap.ui.views.ProfileFragmentView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase

class ProfileFragmentPresenter constructor(
    private val mActivity: Activity?,
    private val mView: ProfileFragmentView?
) {

    private val authentication =
        FirebaseAuthenticationManager(
            FirebaseAuth.getInstance(),
            mActivity!!
        )
    private val database =
        FirebaseDataManager(FirebaseDatabase.getInstance())

    fun setupProfile(){
        database.getUserData(authentication.getCurrentUserId()){ user ->
            if(user != null && mActivity != null){
                mView?.showUserInfo(user)
            }else{
                mView?.showUserInfo(UserRespondDetail())
            }
        }
    }

    fun logout(){
        authentication.firebaseLogout {
            mView?.openLoginUI()
        }
    }

    fun deleteCurrentGuest(){
        authentication.removeGuest{ isSuccessful ->
            if(isSuccessful){
                mView?.openLoginUI()
            }
        }
    }

    fun onRevokeClicked() {
        authentication.revokeGoogleAccount()
    }

    fun onFacebookLogoutButtonClicked() {
        authentication.facebookLogout {
            logout()
        }
    }

    fun updateData(username: String, gender: String, phoneNumber: String){
        val newData: HashMap<String, Any> = hashMapOf(
            Pair(KEY_USER_NAME, username),
            Pair(KEY_GENDER, gender),
            Pair(KEY_PHONE, phoneNumber)
        )

        database.updateProfile(authentication.getCurrentUserId(), newData){ isSuccessful ->
            if(isSuccessful){
                mView?.onProfileUpdateSuccessfully()
            }else{
                mView?.onProfileUpdateFailed()
                mView?.onProfileUpdateFailed()
            }
        }
    }

}