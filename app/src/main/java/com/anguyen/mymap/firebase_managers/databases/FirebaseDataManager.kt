package com.anguyen.mymap.firebase_managers.databases


import com.anguyen.mymap.commons.KEY_EMAIL_USER
import com.anguyen.mymap.commons.KEY_FACEBOOK_USER
import com.anguyen.mymap.commons.KEY_GOOGLE_USER
import com.anguyen.mymap.commons.KEY_LOCATION
import com.anguyen.mymap.firebase_managers.authentication.FirebaseAuthManager
import com.anguyen.mymap.models.CoordinateDetail
import com.anguyen.mymap.models.UserDetail
import com.anguyen.mymap.models.UserInformationDetail
import com.google.firebase.auth.AuthResult
import com.google.firebase.database.*

class FirebaseDataManager constructor(private val database: FirebaseDatabase){

    fun createNormalUser(id: String, userName: String, email: String){

        val user = UserDetail(id, userName, email)

        database.reference
            .child(KEY_EMAIL_USER)
            .child(id)
            .setValue(user)

    }

    fun addUserInformation(userType: String, id: String, phoneNumber: String, coordinate: CoordinateDetail?){

        val userInfo = UserInformationDetail(phoneNumber, coordinate)

        database.reference
            .child(userType)
            .child(id)
            .updateChildren(userInfo.toMap())

    }

    fun createFacebookAccountUser(account: AuthResult,
                                  phoneNumber: String,
                                  coordinate: CoordinateDetail?,
                                  auth: FirebaseAuthManager){

        database.reference
            .child(KEY_FACEBOOK_USER)
            .singleValueHandler(dataChangeHandler = { data ->

                if(data.children.all{ it.child("email").value.toString() != account.user!!.email!! }){

                    val user = UserDetail(account.user!!.email!!, account.user!!.displayName!!
                        , account.user!!.email!!)

                    val id = auth.getUserId()

                    database.reference
                        .child(KEY_FACEBOOK_USER)
                        .child(id)
                        .setValue(user)
                        .addOnSuccessListener {
                            addUserInformation(KEY_FACEBOOK_USER, id,  phoneNumber, coordinate)
                        }

                }
            }, cancelHandler = {})


    }

    private fun DatabaseReference.singleValueHandler(
        dataChangeHandler: (data: DataSnapshot) -> Unit,
        cancelHandler: () -> Unit
    ){
        this@singleValueHandler.addListenerForSingleValueEvent(object : ValueEventListener{
            override fun onCancelled(p0: DatabaseError) {
                cancelHandler()
            }

            override fun onDataChange(dataSnapshot: DataSnapshot) {
                dataChangeHandler(dataSnapshot)
            }

        })
    }

    private fun DatabaseReference.valueEventHandler(
        dataChangeHandler: (data: DataSnapshot) -> Unit,
        cancelHandler: () -> Unit
    ){
        this@valueEventHandler.addValueEventListener(object : ValueEventListener{
            override fun onCancelled(p0: DatabaseError) {
                cancelHandler()
            }

            override fun onDataChange(dataSnapshot: DataSnapshot) {
                dataChangeHandler(dataSnapshot)
            }

        })
    }

    private fun stopValueListener(listener : ValueEventListener){
        database.reference.removeEventListener(listener)
    }

    fun createGoogleAccountUser(account: AuthResult,
                                phoneNumber: String,
                                coordinate: CoordinateDetail?,
                                auth: FirebaseAuthManager){

        database.reference
            .child(KEY_GOOGLE_USER)
            .singleValueHandler(dataChangeHandler = { data ->

            if(data.children.all{ it.child("email").value.toString() != account.user!!.email!! }){

                val user = UserDetail(account.user!!.email!!, account.user!!.displayName!!
                    , account.user!!.email!!)

                val id = auth.getUserId()

                database.reference
                    .child(KEY_GOOGLE_USER)
                    .child(id)
                    .setValue(user)
                    .addOnSuccessListener {
                        addUserInformation(KEY_GOOGLE_USER, id,  phoneNumber, coordinate)
                    }

            }
        }, cancelHandler = {})

    }

    fun updateLocationFieldOnDatabase(userId: String, userType: String, coordinate: CoordinateDetail){
        database.reference
            .child(userType)
            .child(userId)
            .child(KEY_LOCATION)
            .updateChildren(hashMapOf(Pair<String, Any?>(KEY_LOCATION, coordinate)))
    }


}