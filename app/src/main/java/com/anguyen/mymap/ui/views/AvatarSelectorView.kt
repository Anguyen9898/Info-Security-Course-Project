package com.anguyen.mymap.ui.views

import android.content.Intent
import android.widget.EditText
import com.anguyen.mymap.models.LoginDetail
import com.facebook.FacebookException
import com.facebook.login.LoginResult
import com.google.android.gms.common.api.ApiException
import com.google.firebase.auth.AuthResult


interface AvatarSelectorView : BaseView{
    fun onUploadSuccessful(url: String)
    fun onUploadFailed()
    fun onFileNotFoundException (message: String)
    fun onCancel (message: String)
}