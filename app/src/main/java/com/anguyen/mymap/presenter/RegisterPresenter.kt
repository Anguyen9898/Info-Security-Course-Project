package com.anguyen.mymap.presenter

import android.app.Activity
import android.content.Context
import android.widget.EditText
import com.anguyen.mymap.commons.*
import com.anguyen.mymap.firebase_managers.FirebaseAuthenticationManager
import com.anguyen.mymap.firebase_managers.FirebaseCloudStoreManager
import com.anguyen.mymap.firebase_managers.FirebaseDataManager
import com.anguyen.mymap.models.CoordinateDetail
import com.anguyen.mymap.models.NotificationDetail
import com.anguyen.mymap.models.RegisterDetail
import com.anguyen.mymap.models.UserInformationDetail
import com.anguyen.mymap.ui.views.RegisterView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseAuthException
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.firestore.FirebaseFirestore

class RegisterPresenter constructor (
    private val mContext: Context,
    private val mView: RegisterView?,
    private val mRegisterDetail: RegisterDetail?,
    private val mUserDetail: UserInformationDetail?,
    private var mEdtFields: List<EditText>
){

    private val authentication =
        FirebaseAuthenticationManager(
            FirebaseAuth.getInstance(),
            mContext as Activity
        )
    private val database =
        FirebaseDataManager(FirebaseDatabase.getInstance())

    private val fireStore = FirebaseCloudStoreManager(FirebaseFirestore.getInstance())

    fun onRegisterButtonClicked() {

        when{

            !isNetworkConnected(mContext)!! -> {
                mView?.internetError()
            }

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

       if (mRegisterDetail?.isValid()!! && mUserDetail?.isValid()!!){
           try {
               authentication.registerNormalAccount(
                   mRegisterDetail.email,
                   mRegisterDetail.password,
                   mRegisterDetail.username
               ) { isSuccessful ->

                   if (isSuccessful) {

                       createUser(mRegisterDetail.username, mRegisterDetail.email)

                       addUserInfo(
                           mUserDetail.avatarUrl,
                           mUserDetail.userTypeImgUrl,
                           mUserDetail.gender,
                           mUserDetail.phoneNumber,
                           mUserDetail.location,
                           mUserDetail.notifications
                       )

                       fireStore.createUserFireStoreData(authentication.getCurrentUserId(), mUserDetail)

                       mView?.onRegisterSuccess(mRegisterDetail)

                   }else {
                       mView?.onRegisterFail()
                   }
               }

           }catch (ex : FirebaseAuthException){
               mView?.fireBaseExceptionError(ex.message!!)
           }
       }else{
           mView?.onRegisterFail()
       }

    }

    private fun createUser(userName: String, email: String){
        val id = authentication.getCurrentUserId()
        database.createNormalUser(id, userName, email)
    }

    private fun addUserInfo(imgUrl: String,
                            userTypeImgUrl: String,
                            gender: String,
                            phone: String,
                            location: CoordinateDetail?,
                            notifications: HashMap<String, NotificationDetail?>
    ){
        val id = authentication.getCurrentUserId()
        database.addUserInformation(
            KEY_EMAIL_USER,
            userTypeImgUrl,
            id,
            imgUrl,
            gender,
            phone,
            location,
            notifications
        )
    }

    fun onUsernameChange(username: String){
        mRegisterDetail?.username = username
        mView?.showUsernameError(username.length)
    }

    fun onGenderChange(gender: String){
        mUserDetail?.gender = gender
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