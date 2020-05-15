package com.anguyen.mymap.firebase_managers

import android.net.Uri
import com.anguyen.mymap.commons.KEY_NEW_AVATAR
import com.google.firebase.storage.StorageReference

class FirebaseStorageManager constructor(private val storageRef: StorageReference){

    fun uploadNewAvatar(userId: String, uri: Uri, onResult: (Boolean) -> Unit) {

        storageRef
            .child(KEY_NEW_AVATAR)
            .putFile(uri)
            .addOnCompleteListener{
                onResult(it.isComplete and it.isSuccessful)
            }

    }

    fun getFileUrl(fileName: String, onResult: (Boolean, String) -> Unit){
        storageRef
            .child(fileName)
            .downloadUrl
            .addOnCompleteListener {
                onResult(it.isSuccessful and it.isComplete, it.result.toString())
            }
    }

}