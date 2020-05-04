package com.anguyen.mymap.firebase_managers.authentication

import android.app.Activity
import com.anguyen.mymap.R
import com.anguyen.mymap.commons.GOOGLE_REQUEST_CODE
import com.facebook.CallbackManager
import com.facebook.FacebookCallback
import com.facebook.login.LoginManager
import com.facebook.login.LoginResult
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.firebase.auth.*

class FirebaseAuthManager constructor(
    private val authentication: FirebaseAuth,
    private val activity: Activity
) {

    private val googleOptions = GoogleSignInOptions
        .Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
        .requestIdToken(activity.getString(R.string.default_web_client_id))
        .requestEmail()
        .build()!!

    private val googleSignInClient = GoogleSignIn.getClient(activity, googleOptions)

    private val facebookManager = LoginManager.getInstance()

    fun signInByNormalAccount(email: String, password: String, onResult: (Boolean) -> Unit) {
        authentication.signInWithEmailAndPassword(email, password)
            .addOnCompleteListener{
                onResult(it.isComplete and it.isSuccessful)
            }
    }

    fun sinInWithCredential(credential: AuthCredential,
                            onResult: (Boolean, AuthResult) -> Unit){
        authentication.signInWithCredential(credential).apply {
            addOnCompleteListener {
                onResult(it.isComplete and it.isSuccessful, it.result!!)
            }
        }
    }


    fun signInByGoogleAccount(){
        activity.startActivityForResult(googleSignInClient.signInIntent, GOOGLE_REQUEST_CODE)
    }

    fun signInByFaceBookAccount(mCallbackManager: CallbackManager,
                                facebookCallBack: FacebookCallback<LoginResult>
    ){
        facebookManager.registerCallback(mCallbackManager, facebookCallBack)
    }

    fun anonymousSignIn(onResult: (Boolean) -> Unit){
        authentication.signInAnonymously().addOnCompleteListener {
            onResult(it.isComplete and it.isSuccessful)
        }
    }

    fun removeGuest(onResult: (Boolean) -> Unit) {
        authentication.currentUser
            ?.delete()
            ?.addOnCompleteListener {
                onResult(it.isComplete and it.isSuccessful)
            }
    }

    fun registerNormalAccount(email: String, password: String, username: String, onResult: (Boolean) -> Unit) {

        authentication.createUserWithEmailAndPassword(email, password)
            .addOnCompleteListener {

                if(it.isSuccessful && it.isComplete){
                    authentication.currentUser?.updateProfile(
                        UserProfileChangeRequest
                            .Builder()
                            .setDisplayName(username)
                            .build()
                    )
                    onResult(true)
                }else
                    onResult(false)

            }

    }

    fun isLoginBefore() = authentication.currentUser != null

    fun getUserId() = authentication.currentUser?.uid!!

    //fun getUserName() = authentication.currentUser?.displayName

    fun emailLogout(onResult: () -> Unit) {
        authentication.signOut()
        onResult()
    }

    fun revokeGoogleAccount(onResult: (Boolean)-> Unit){
        googleSignInClient.revokeAccess()
            .addOnCompleteListener {
                onResult(it.isComplete and it.isSuccessful)
            }
    }

    fun facebookLogout(onResult: () -> Unit){
        facebookManager.logOut()
        onResult()
    }

}