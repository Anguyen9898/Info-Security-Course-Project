package com.anguyen.mymap.presenter

import android.app.Activity
import android.content.Context
import com.anguyen.mymap.firebase_managers.authentication.FirebaseAuthManager
import com.anguyen.mymap.ui.views.StartView
import com.google.firebase.auth.FirebaseAuth

class StartPresenter(
    mContext: Context,
    private val mView: StartView?
) {

    private val authentication = FirebaseAuthManager(FirebaseAuth.getInstance(), mContext as Activity)

    fun updateUI(){
        if (authentication.isLoginBefore()) {
            mView?.openMainUI()
        } else{
            mView?.openLoginUI()
        }
    }

}