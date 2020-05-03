package com.anguyen.mymap.presenter

import android.app.Activity
import android.content.Context
import android.widget.EditText
import com.anguyen.mymap.commons.areAnyFieldsEmpty
import com.anguyen.mymap.commons.isEmailValid
import com.anguyen.mymap.commons.isNetworkConnected
import com.anguyen.mymap.firebase_managers.authentication.FirebaseAuthManager
import com.anguyen.mymap.firebase_managers.databases.FirebaseDataManager
import com.anguyen.mymap.models.CoordinateDetail
import com.anguyen.mymap.models.LoginDetail
import com.anguyen.mymap.ui.views.LoginView
import com.facebook.CallbackManager
import com.facebook.FacebookCallback
import com.facebook.FacebookException
import com.facebook.login.LoginResult
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.firebase.FirebaseException
import com.google.firebase.auth.FacebookAuthProvider
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.GoogleAuthProvider
import com.google.firebase.database.FirebaseDatabase

class LoginPresenter constructor(
    var mContext: Context,
    var mView: LoginView? = null,
    var mLoginDetail: LoginDetail? = null,
    var mEdtFields: List<EditText>
){
    val authentication = FirebaseAuthManager(
        FirebaseAuth.getInstance(),
        mContext as Activity
    )
    private val database = FirebaseDataManager(FirebaseDatabase.getInstance())

    fun onLoginByGoogleButtonClicked(){

        if(!isNetworkConnected(mContext)!!){
            mView?.internetError()
        }else{
            authentication.signInByGoogleAccount()
        }

    }

    fun googleAccLoginCompleting(account: GoogleSignInAccount?){

        val credential = GoogleAuthProvider.getCredential(account!!.idToken, null)

        authentication.sinInWithCredential(
            credential = credential,
            onResult = { isSuccessful, result ->
                if(isSuccessful){
                    mView?.onLoginSuccess()

                    database.createGoogleAccountUser(
                        result,
                        CoordinateDetail(0.0, 0.0),
                        authentication
                    )
                }else{
                    mView?.onLoginFail()
                }
            }
        )

    }

    fun onLoginByFacebookButtonClicked(mCallbackManager: CallbackManager){
        if(!isNetworkConnected(mContext)!!){
            mView?.internetError()
        }else{
            authentication.signInByFaceBookAccount(mCallbackManager, getFacebookCallBack)
        }
    }

    private val getFacebookCallBack = object : FacebookCallback<LoginResult> {

        override fun onSuccess(result: LoginResult?) {

            val credential = FacebookAuthProvider.getCredential(result!!.accessToken.token.toString())

            authentication.sinInWithCredential(
                credential = credential,
                onResult = { isSuccessful, authResult ->

                    mView?.onLoginSuccess()

                    if(isSuccessful){
                        database.createFacebookAccountUser(
                            authResult,
                            CoordinateDetail(0.0, 0.0),
                            authentication
                        )
                    }else{
                        mView?.onLoginFail()
                    }

                }
            )

        }

        override fun onCancel() {
            mView?.onFacebookAccountLoginCancel()
        }

        override fun onError(error: FacebookException?) {
            mView?.onFacebookAccountLoginError(error)
        }

    }

    fun onLoginButtonClicked() {

        when{
            !isNetworkConnected(mContext)!! ->  mView?.internetError()

            areAnyFieldsEmpty(mLoginDetail!!.email, mLoginDetail!!.password) -> {
                mEdtFields.forEach{
                    if(it.text.isEmpty()){
                        mView?.onEmptyFieldsError(it)
                    }
                }
            }

            else -> loginByNormalEmail()
        }

    }

    private fun loginByNormalEmail(){
        try {

            if(mLoginDetail?.isValid()!!){

                authentication.signInByNormalAccount(mLoginDetail!!.email,
                    mLoginDetail!!.password){ isSuccessful ->
                    if (isSuccessful)
                        mView?.onLoginSuccess()
                    else
                        mView?.onLoginFail()
                }

            }
        }catch (ex: FirebaseException){
            mView?.fireBaseExceptionError(ex.message!!)
        }
    }

    fun onAnonymousLoginButtonClicked(){
        if(!isNetworkConnected(mContext)!!){
            mView?.internetError()
        }else {
            try {
                authentication.anonymousSignIn(){  isSuccessful ->
                    if (isSuccessful)
                        mView?.onAnonymousLoginSuccess()
                }
            }catch (ex: FirebaseException){
                mView?.fireBaseExceptionError(ex.message!!)
            }
        }
    }

    fun onEmailChange(email: String){
        mLoginDetail?.email = email

        if(!isEmailValid(email) and (email.isNotEmpty() or (email == ""))) {
            mView?.showEmailError()
        }
    }

    fun onPasswordChange(password: String){
        mLoginDetail?.password = password
        mView?.showPasswordError(password.length)
    }

}