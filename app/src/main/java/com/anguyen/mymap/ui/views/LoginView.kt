package com.anguyen.mymap.ui.views

import android.widget.EditText
import com.anguyen.mymap.models.LoginDetail
import com.facebook.FacebookException
import com.facebook.login.LoginResult
import com.google.android.gms.common.api.ApiException
import com.google.firebase.auth.AuthResult


interface LoginView : BaseView{

    fun onApiError(ex: ApiException)

    fun onFacebookAccountLoginSuccess()

    fun onFacebookAccountLoginCancel()

    fun onFacebookAccountLoginError(ex: FacebookException?)

    fun onLoginSuccess()

    fun onAnonymousLoginSuccess()

    fun onLoginFail()

    fun showEmailError()

    fun showPasswordError(count: Int)

    fun onEmptyFieldsError(vararg emptyFields: EditText)

}