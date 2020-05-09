package com.anguyen.mymap.presenter

import android.app.Activity
import android.content.Context
import android.widget.EditText
import com.anguyen.mymap.commons.*
import com.anguyen.mymap.firebase_managers.FirebaseAuthenticationManager
import com.anguyen.mymap.models.LoginDetail
import com.anguyen.mymap.ui.views.BottomDialogLoginView
import com.anguyen.mymap.ui.views.LoginView
import com.google.firebase.FirebaseException
import com.google.firebase.auth.FirebaseAuth

class BottomSheetDialogPresenter (
    var mContext: Context,
    var mBottomDialogLoginView: BottomDialogLoginView?,
    var mLoginView: LoginView?,
    var mLoginDetail: LoginDetail?,
    var mEdtFields: List<EditText>
){

    private val authentication = FirebaseAuthenticationManager(
        FirebaseAuth.getInstance(),
        mContext as Activity
    )

    fun onLoginButtonClicked() {

        when{
            !isNetworkConnected(mContext)!! -> {
                mBottomDialogLoginView?.internetError()
            }

            areAnyFieldsEmpty(mLoginDetail!!.email, mLoginDetail!!.password) -> {

                mEdtFields.forEach{
                    if(it.text.isEmpty()){
                        mBottomDialogLoginView?.onEmptyFieldsError(it)
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
                    if (isSuccessful){
                        mLoginView?.onLoginSuccess()
                    }
                    else{
                        mLoginView?.onLoginFail()
                    }
                }

            }
        }catch (ex: FirebaseException){
            mBottomDialogLoginView?.fireBaseExceptionError(ex.message!!)
        }
    }

    fun onEmailChange(email: String){
        mLoginDetail?.email = email

        if(!isEmailValid(email) and (email.isNotEmpty() or (email == ""))) {
            mBottomDialogLoginView?.showEmailError()
        }
    }

    fun onPasswordChange(password: String){
        mLoginDetail?.password = password
        mBottomDialogLoginView?.showPasswordError(password.length)
    }

}