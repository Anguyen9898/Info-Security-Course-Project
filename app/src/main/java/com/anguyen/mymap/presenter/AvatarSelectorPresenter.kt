package com.anguyen.mymap.presenter

import android.app.Activity
import android.content.Context
import android.net.Uri
import com.anguyen.mymap.commons.*
import com.anguyen.mymap.firebase_managers.FirebaseAuthenticationManager
import com.anguyen.mymap.firebase_managers.FirebaseDataManager
import com.anguyen.mymap.firebase_managers.FirebaseStorageManager
import com.anguyen.mymap.ui.views.AvatarSelectorView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.storage.FirebaseStorage

class AvatarSelectorPresenter constructor(
    var mContext: Context,
    var mView: AvatarSelectorView?
){
    val authentication = FirebaseAuthenticationManager(
        FirebaseAuth.getInstance(),
        mContext as Activity
    )

    private val storageRef = FirebaseStorageManager(
        FirebaseStorage
            .getInstance()
            .getReference(authentication.getCurrentUserId())
    )

    private val database = FirebaseDataManager(FirebaseDatabase.getInstance())

    fun uploadAvatar(uri: Uri){
        storageRef.uploadNewAvatar(authentication.getCurrentUserId(), uri){ isSuccessful ->
            if(isSuccessful){
                updateAvatarUrlOnDatabase()
            }else{
                mView?.onUploadFailed()
            }
        }
    }

    private fun updateAvatarUrlOnDatabase(){
        storageRef.getFileUrl(
            KEY_NEW_AVATAR
        ){ isSuccessful, result ->
            if(isSuccessful){
                database.changeAvatarUrlOnData(authentication.getCurrentUserId(), result){
                    if(it){
                        database.getNewAvatarUrl(authentication.getCurrentUserId()){ url ->
                            mView?.onUploadSuccessful(url)
                        }
                    }else{
                        mView?.onUploadFailed()
                    }
                }
            }else{
                mView?.onUploadFailed()
            }
        }

    }

}