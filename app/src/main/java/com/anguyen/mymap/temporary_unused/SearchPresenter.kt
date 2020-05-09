package com.anguyen.mymap.temporary_unused

import android.app.Activity
import android.content.Context
import com.anguyen.mymap.commons.KEY_REQUEST_LOCATION
import com.anguyen.mymap.commons.isNetworkConnected
import com.anguyen.mymap.firebase_managers.FirebaseAuthenticationManager
import com.anguyen.mymap.firebase_managers.FirebaseCloudStoreManager
import com.anguyen.mymap.firebase_managers.FirebaseDataManager
import com.anguyen.mymap.models.NotificationDetail
import com.google.firebase.FirebaseException
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.firestore.FirebaseFirestore
import java.lang.Exception
import kotlin.random.Random

class SearchPresenter(
    private val mContext: Context,
    private val mView: SearchView?
) {

    val authentication = FirebaseAuthenticationManager(
            FirebaseAuth.getInstance(),
            mContext as Activity
        )

    val database = FirebaseDataManager(FirebaseDatabase.getInstance())

    private val fireStore = FirebaseCloudStoreManager(FirebaseFirestore.getInstance())

    fun setUserSearchingList(){

        if(!isNetworkConnected(mContext)!!){
            mView?.internetError()
        }
        try {
            database.setUserSearchingList(authentication.getCurrentUserId()){
                mView?.onSettingUserSearchingList(it)
            }

        }catch (ex: FirebaseException){
            mView?.fireBaseExceptionError(ex.message!!)
        }

    }

    fun checkNotificationStatus(onChecking: (String) -> Unit){
//        database.getUserProfile(authentication.getUserId()){ currentUser ->
//            val notifications = currentUser?.notifications
//            if(notifications != null){
//                if(notifications.senderId == currentUser.id
//                    && notifications.status == STATUS_WAITING_RESPOND){
//
//                    onChecking(notifications.receiverId)
//                }
//            }
//        }
    }

    fun sendRequestHandler(receiverId: String,
                           status: String?){

        try{
            database.getUserData(authentication.getCurrentUserId()){ sender ->
                val notificationId = Random.nextInt().toString()

                val notificationDetail = NotificationDetail(
                    notificationId,
                    sender?.id!!,
                    receiverId,
                    status!!,
                    KEY_REQUEST_LOCATION
                )

                fireStore.setRequestFollowNotificationData(notificationDetail){
                        isSuccessful ->

                    if(isSuccessful){

                        mView?.onSendRequestSuccessfully()

                        database.setNotificationToUserData(
                            authentication.getCurrentUserId(),
                            receiverId,
                            notificationDetail
                        )


                    }else{
                        mView?.onSendRequestFailed()
                    }
                }

            }
        }catch (ex: Exception){
            mView?.fireBaseExceptionError(ex.message!!)
        }

    }

    fun cancelRequestHandler(){

    }

}