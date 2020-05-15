package com.anguyen.mymap.presenter

import android.app.Activity
import android.content.Context
import android.widget.EditText
import com.anguyen.mymap.commons.*
import com.anguyen.mymap.firebase_managers.FirebaseAuthenticationManager
import com.anguyen.mymap.firebase_managers.FirebaseDataManager
import com.anguyen.mymap.models.CoordinateDetail
import com.anguyen.mymap.models.LoginDetail
import com.anguyen.mymap.models.NotificationDetail
import com.anguyen.mymap.ui.views.LoginView
import com.facebook.CallbackManager
import com.facebook.FacebookCallback
import com.facebook.FacebookException
import com.facebook.login.LoginResult
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.firebase.FirebaseException
import com.google.firebase.auth.FacebookAuthProvider
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseAuthUserCollisionException
import com.google.firebase.auth.GoogleAuthProvider
import com.google.firebase.database.FirebaseDatabase

class LoginPresenter constructor(
    var mContext: Context,
    var mView: LoginView?
){
    val authentication = FirebaseAuthenticationManager(
        FirebaseAuth.getInstance(),
        mContext as Activity
    )
    private val database =
        FirebaseDataManager(FirebaseDatabase.getInstance())

    fun onLoginByGoogleButtonClicked(){

        if(!isNetworkConnected(mContext)!!){

            mView?.internetError()

        }else{
            authentication.signInByGoogleAccount{
                mView?.openGoogleSignInDialog(it)
            }
        }

    }

    fun googleAccLoginCompleting(account: GoogleSignInAccount?){

        val credential = GoogleAuthProvider.getCredential(account!!.idToken, null)

        authentication.sinInWithCredential(

            credential = credential,

            onSuccess = { authResult ->

                mView?.onLoginSuccess()

                database.createGoogleUserData(
                    authResult,
                    CoordinateDetail(),
                    hashMapOf(Pair(KEY_NOTIFICATION, NotificationDetail())),
                    authentication.getCurrentUserId(),
                    GOOGLE_USER_TYPE_URL
                )

            },

            onError = { googleException ->
                mView?.fireBaseExceptionError(googleException.message!!)
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

                onSuccess = { authResult ->

                    mView?.onLoginSuccess()
                    database.createFacebookUserData(
                        authResult,
                        CoordinateDetail(),
                        hashMapOf(Pair(KEY_NOTIFICATION, NotificationDetail())),
                        authentication.getCurrentUserId(),
                        FACEBOOK_USER_TYPE_URL
                    )

                },

                onError = { facebookException ->
                    authentication.facebookLogout{
                        mView?.onFacebookAccountLoginCancel()
                    }
                    mView?.fireBaseExceptionError(facebookException.message!!)
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

}