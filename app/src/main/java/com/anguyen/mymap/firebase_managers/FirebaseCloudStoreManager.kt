package com.anguyen.mymap.firebase_managers

import com.anguyen.mymap.commons.KEY_NOTIFICATION
import com.anguyen.mymap.commons.KEY_REQUEST_LOCATION
import com.anguyen.mymap.commons.KEY_USER
import com.anguyen.mymap.models.NotificationDetail
import com.anguyen.mymap.models.UserDetail
import com.anguyen.mymap.models.UserInformationDetail
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.FirebaseFirestoreException
import com.google.firebase.firestore.QuerySnapshot
import com.google.firebase.firestore.SetOptions
import kotlin.random.Random


class FirebaseCloudStoreManager(private val fireStore: FirebaseFirestore) {

    fun createUserFireStoreData(userId: String, userDetail: UserInformationDetail){
        fireStore
            .collection(KEY_USER)
            .document(userId)
            .set(userDetail)
    }

    fun setRequestFollowNotificationData(notificationDetail: NotificationDetail,
                                         onResult: (Boolean) -> Unit){
        fireStore
            .collection(KEY_NOTIFICATION)
            .document(notificationDetail.id)
            .set(notificationDetail, SetOptions.merge())
            .addOnCompleteListener {
                onResult(it.isSuccessful and it.isComplete)
            }

    }

    fun getRequestFollowNotificationData(notificationId: String,
                                         onResult: (NotificationDetail?) -> Unit){
        fireStore
            .collection(KEY_NOTIFICATION)
            .document(notificationId)
            .get()
            .addOnCompleteListener {
                if(it.isSuccessful){
                    onResult(it.result?.toObject(NotificationDetail::class.java))
                }
            }
    }

}