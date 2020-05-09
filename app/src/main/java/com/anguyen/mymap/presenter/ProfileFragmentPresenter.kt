package com.anguyen.mymap.presenter

import android.app.Activity
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

    fun onSettingProfile(){
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
        authentication.revokeGoogleAccount { isSuccessful ->
            if(isSuccessful){
                mView?.onRevokeSuccess()
            }
        }
    }

    fun onFacebookLogoutButtonClicked() {
        authentication.facebookLogout {
            logout()
        }
    }

}