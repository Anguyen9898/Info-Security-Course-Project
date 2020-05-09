package com.anguyen.mymap.presenter

import android.app.Activity
import android.content.Context
import com.anguyen.mymap.firebase_managers.FirebaseAuthenticationManager
import com.anguyen.mymap.ui.views.MainView
import com.google.firebase.auth.FirebaseAuth

class MainPresenter(mContext: Context, private val mView: MainView?) {

    private val authentication =
        FirebaseAuthenticationManager(
            FirebaseAuth.getInstance(),
            mContext as Activity
        )

    fun deleteCurrentGuest(){
        authentication.removeGuest{ isSuccessful ->
            if(isSuccessful){
                mView?.onDeleteGuestSuccessful()
            }
        }
    }
}