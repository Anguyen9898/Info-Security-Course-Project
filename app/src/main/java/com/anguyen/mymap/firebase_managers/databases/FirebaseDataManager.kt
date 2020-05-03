package com.anguyen.mymap.firebase_managers.databases


import com.anguyen.mymap.commons.*
import com.anguyen.mymap.firebase_managers.authentication.FirebaseAuthManager
import com.anguyen.mymap.models.CoordinateDetail
import com.anguyen.mymap.models.UserDetail
import com.anguyen.mymap.models.UserInformationDetail
import com.anguyen.mymap.models.UserRespondDetail
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

    fun addUserInformation(
        userType: String,
        id: String,
        avatarUrl: String,
        gender: String,
        phoneNumber: String,
        coordinate: CoordinateDetail?
    ){

        val userInfo = UserInformationDetail(userType, avatarUrl, gender, phoneNumber, coordinate)

        database.reference
            .child(userType)
            .child(id)
            .updateChildren(userInfo.toMap())

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


    fun createGoogleAccountUser(account: AuthResult,
                                coordinate: CoordinateDetail?,
                                auth: FirebaseAuthManager){

        database.reference
            .child(KEY_GOOGLE_USER)
            .singleValueHandler(dataChangeHandler = { data ->

                if(data.children.all{ it.child("id").value.toString() != auth.getUserId() }){

                    val id = auth.getUserId()

                    val user = UserDetail(id, account.user!!.displayName!!
                        , account.user!!.email!!)

                    database.reference
                        .child(KEY_GOOGLE_USER)
                        .child(id)
                        .setValue(user)
                        .addOnSuccessListener {

                            val avatarUrl = if(isFieldEmpty(account.user?.photoUrl.toString())){
                                DEFAULT_AVATAR_URL
                            } else{
                                account.user?.photoUrl.toString()
                            }

                            val phoneNumber = if(account.user?.phoneNumber == null){
                                "" //return empty string
                            } else{
                                account.user?.phoneNumber.toString()
                            }

                            addUserInformation(
                                KEY_GOOGLE_USER,
                                id,
                                avatarUrl,
                                "",
                                phoneNumber,
                                coordinate
                            )

                        }

                }
            }, cancelHandler = {})

    }

    fun createFacebookAccountUser(account: AuthResult,
                                  coordinate: CoordinateDetail?,
                                  auth: FirebaseAuthManager){

        database.reference
            .child(KEY_FACEBOOK_USER)
            .singleValueHandler(dataChangeHandler = { data ->

                if(data.children.all{ it.child("id").value.toString() != auth.getUserId() }){

                    val id = auth.getUserId()

                    val user = UserDetail(id, account.user!!.displayName!!
                        , account.user!!.email!!)

                    database.reference
                        .child(KEY_FACEBOOK_USER)
                        .child(id)
                        .setValue(user)
                        .addOnSuccessListener {

                            val avatarUrl = if(isFieldEmpty(account.user?.photoUrl.toString())){
                                DEFAULT_AVATAR_URL
                            } else{
                                account.user?.photoUrl.toString()
                            }

                            val phoneNumber = if(account.user?.phoneNumber == null){
                                "" //return empty string
                            } else{
                                account.user?.phoneNumber.toString()
                            }

                            addUserInformation(
                                KEY_FACEBOOK_USER,
                                id,
                                avatarUrl,
                                "",
                                phoneNumber,
                                coordinate
                            )

                        }

                }
            }, cancelHandler = {})


    }

    fun updateLocationFieldOnDatabase(userId: String, userType: String, coordinate: CoordinateDetail){
        database.reference
            .child(userType)
            .child(userId)
            .child(KEY_LOCATION)
            .updateChildren(coordinate.toMap())
    }

    fun getUserProfile(userType: String,
                       auth: FirebaseAuthManager,
                       onRespond: (UserRespondDetail?) -> Unit){
        val id = auth.getUserId()

        database.reference
            .child(userType)
            .child(id)
            .singleValueHandler(
                dataChangeHandler = { data ->
                    val userRespond = data.getValue(UserRespondDetail::class.java)
                    onRespond(userRespond)
                },
                cancelHandler = {}
            )
    }

//    private fun stopValueListener(listener : ValueEventListener){
//        database.reference.removeEventListener(listener)
//    }

}