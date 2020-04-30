package com.anguyen.mymap.presenter

import android.app.Activity
import android.content.Context
import android.widget.EditText
import com.anguyen.mymap.commons.*
import com.anguyen.mymap.firebase_managers.authentication.FirebaseAuthManager
import com.anguyen.mymap.firebase_managers.databases.FirebaseDataManager
import com.anguyen.mymap.models.CoordinateDetail
import com.anguyen.mymap.models.RegisterDetail
import com.anguyen.mymap.models.UserInformationDetail
import com.anguyen.mymap.ui.views.RegisterView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseAuthException
import com.google.firebase.database.FirebaseDatabase

class RegisterPresenter constructor (
    private val mContext: Context,
    private val mView: RegisterView?,
    private val mRegisterDetail: RegisterDetail?,
    private val mUserDetail: UserInformationDetail?,
    private var mEdtFields: List<EditText>
){

    private val authentication = FirebaseAuthManager(FirebaseAuth.getInstance(), mContext as Activity)
    private val database = FirebaseDataManager(FirebaseDatabase.getInstance())

    fun onRegisterButtonClicked() {

        when{

            !isNetworkConnected(mContext)!! ->  mView?.internetError()

            areAnyFieldsEmpty(
                mRegisterDetail!!.username,
                mRegisterDetail.email,
                mRegisterDetail.password,
                mRegisterDetail.repeatPassword,
                mUserDetail!!.phoneNumber
            ) -> {
                mEdtFields.forEach{
                    if(it.text.isEmpty()){
                        mView?.onEmptyFieldsError(it)
                    }
                }
            }

            else -> register()

        }

    }

    private fun register(){

       if (mRegisterDetail?.isValid()!! && isPhoneNumberValid(mUserDetail!!.phoneNumber)){
           try {
               authentication.registerNormalAccount(
                   mRegisterDetail.email,
                   mRegisterDetail.password,
                   mRegisterDetail.username
               ) { isSuccessful ->

                   if (isSuccessful) {

                       createUser(mRegisterDetail.username, mRegisterDetail.email)
                       addUserInfo(mUserDetail.phoneNumber, mUserDetail.location)

                       mView?.onRegisterSuccess(mRegisterDetail)

                   }else {
                       mView?.onRegisterFail()
                   }
               }

           }catch (ex : FirebaseAuthException){
               mView?.fireBaseExceptionError(ex.message!!)
           }
       }

    }

    private fun createUser(userName: String, email: String){
        val id = authentication.getUserId()
        database.createNormalUser(id, userName, email)
    }

    private fun addUserInfo(phone: String, location: CoordinateDetail?){
        val id = authentication.getUserId()
        database.addUserInformation(KEY_EMAIL_USER, id, phone, location)
    }

    fun onUsernameChange(username: String){
        mRegisterDetail?.username = username
        mView?.showUsernameError(username.length)
    }

    fun onEmailChange(email: String){
        mRegisterDetail?.email = email

        if(!isEmailValid(email)) {
            mView?.showEmailError()
        }
    }

    fun onPhoneNumberChange(phone: String){
        mUserDetail?.phoneNumber = phone
        if(!isPhoneNumberValid(phone)){
            mView?.showPhoneNumberFormatError()
        }
    }

    fun onPasswordChange(password: String){
        mRegisterDetail?.password = password
        mView?.showPasswordLengthError(password.length)
    }

    fun onRepeatPasswordChange(repeatPassword: String){
        mRegisterDetail?.repeatPassword = repeatPassword

        if(!arePasswordsSame(mRegisterDetail!!.password, repeatPassword)) {
            mView?.showPasswordMatchingError()
        }

    }

}