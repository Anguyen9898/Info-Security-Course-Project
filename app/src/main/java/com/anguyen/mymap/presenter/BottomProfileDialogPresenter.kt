package com.anguyen.mymap.presenter

import android.app.Activity
import android.content.Context
import android.widget.EditText
import com.anguyen.mymap.commons.*
import com.anguyen.mymap.firebase_managers.FirebaseAuthenticationManager
import com.anguyen.mymap.firebase_managers.FirebaseDataManager
import com.anguyen.mymap.models.LoginDetail
import com.anguyen.mymap.ui.views.BottomDialogLoginView
import com.anguyen.mymap.ui.views.BottomDialogProfileView
import com.anguyen.mymap.ui.views.LoginView
import com.google.firebase.FirebaseException
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase

class BottomProfileDialogPresenter (
    private val mView: BottomDialogProfileView?
){
    private val database = FirebaseDataManager(FirebaseDatabase.getInstance())

    fun setupProfile(id: String){
        database.getUserData(id){
            mView?.onResult(it)
        }
    }
}