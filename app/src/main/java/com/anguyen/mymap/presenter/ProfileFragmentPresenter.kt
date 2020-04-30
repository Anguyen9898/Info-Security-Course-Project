package com.anguyen.mymap.presenter

import android.app.Activity
import android.content.Context
import com.anguyen.mymap.firebase_managers.authentication.FirebaseAuthManager
import com.anguyen.mymap.ui.views.ProfileFragmentView
import com.google.firebase.auth.FirebaseAuth

class ProfileFragmentPresenter constructor(
    private val mContext: Context,
    private var mView: ProfileFragmentView) {

    private val authentication = FirebaseAuthManager(FirebaseAuth.getInstance(), mContext as Activity)

    fun onLogoutClicked() = authentication.logout { mView.openLoginUI() }

    fun onRevokeClicked() {
        authentication.revokeGoogleAccount {
            mView.onRevokeSuccess()
        }
    }

}