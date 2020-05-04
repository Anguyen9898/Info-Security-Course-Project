package com.anguyen.mymap.presenter

import android.app.Activity
import android.content.Context
import com.anguyen.mymap.commons.isNetworkConnected
import com.anguyen.mymap.firebase_managers.authentication.FirebaseAuthManager
import com.anguyen.mymap.firebase_managers.databases.FirebaseDataManager
import com.anguyen.mymap.ui.views.SearchView
import com.google.firebase.FirebaseException
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase

class SearchPresenter(
    private val mContext: Context,
    private val mView: SearchView?
) {

    private val authentication = FirebaseAuthManager(FirebaseAuth.getInstance(), mContext as Activity)
    private val database = FirebaseDataManager(FirebaseDatabase.getInstance())

    fun setUserSearchingList(){

        if(!isNetworkConnected(mContext)!!){
            mView?.internetError()
        }
        try {
            database.setUserSearchingList(authentication.getUserId()) {
                mView?.setUserSearchingList(it)
            }
        }catch (ex: FirebaseException){
            mView?.fireBaseExceptionError(ex.message!!)
        }

    }

}