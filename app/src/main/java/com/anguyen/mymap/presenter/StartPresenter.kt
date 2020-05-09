package com.anguyen.mymap.presenter

import android.app.Activity
import android.content.Context
import com.anguyen.mymap.firebase_managers.FirebaseAuthenticationManager
import com.anguyen.mymap.firebase_managers.FirebaseDataManager
import com.anguyen.mymap.ui.views.StartView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase

class StartPresenter(
    mContext: Context,
    private val mView: StartView?
) {

    private val authentication =
        FirebaseAuthenticationManager(
            FirebaseAuth.getInstance(),
            mContext as Activity
        )

    private val database = FirebaseDataManager(FirebaseDatabase.getInstance())

    fun updateUI(){
        if (authentication.isLoginBefore()) {
            database.getUserType(authentication.getCurrentUserId()){ userType ->
                mView?.openMainUI(userType)
            }
        } else{
            mView?.openLoginUI()
        }
    }

}