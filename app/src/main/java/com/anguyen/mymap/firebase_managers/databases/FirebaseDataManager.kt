package com.anguyen.mymap.firebase_managers.databases


import com.anguyen.mymap.commons.*
import com.anguyen.mymap.firebase_managers.authentication.FirebaseAuthManager
import com.anguyen.mymap.models.*
import com.google.firebase.auth.AuthResult
import com.google.firebase.database.*

class FirebaseDataManager constructor(private val database: FirebaseDatabase){

    fun createNormalUser(id: String, userName: String, email: String){

        val user = UserDetail(id, userName, email)

        database.reference
            .child(KEY_USER)
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
            .child(KEY_USER)
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


    fun createGoogleUserData(account: AuthResult,
                             coordinate: CoordinateDetail?,
                             id: String){

        database.reference
            .child(KEY_USER)
            .child(id)
            .singleValueHandler(dataChangeHandler = { data ->

                if(data.children.all{ it.child("id").value.toString() != id }){ //Only create data if user login first time

                    //val id = auth.getUserId() Change to string instead of adding auth param

                    val user = UserDetail(id, account.user!!.displayName!!
                        , account.user!!.email!!)

                    database.reference
                        .child(KEY_USER)
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

    fun createFacebookUserData(account: AuthResult,
                               coordinate: CoordinateDetail?,
                               id: String){

        database.reference
            .child(id)
            .singleValueHandler(dataChangeHandler = { data ->

                if(data.children.all{ it.child("id").value.toString() != id }){ //Only create data if user login first time

                    //val id = auth.getUserId()

                    val user = UserDetail(id, account.user!!.displayName!!
                        , account.user!!.email!!)

                    database.reference
                        .child(KEY_USER)
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
            .child(KEY_USER)
            .child(userId)
            .child(KEY_LOCATION)
            .updateChildren(coordinate.toMap())
    }

    fun getUserProfile(userType: String,
                       id: String,
                       onRespond: (UserRespondDetail?) -> Unit){
        //val id = auth.getUserId()

        database.reference
            .child(KEY_USER)
            .child(id)
            .singleValueHandler(
                dataChangeHandler = { data ->
                    val userRespond = data.getValue(UserRespondDetail :: class.java)
                    onRespond(userRespond)
                },
                cancelHandler = {}
            )
    }

    fun checkUserFollowing(currentUser: String, onChecking: (UserSearchListDetail?) -> Unit){
        database.reference
            .child(KEY_USER)
            .child(currentUser)
            .child(KEY_FOLLOWING)
            .valueEventHandler(
                dataChangeHandler = { data ->
                    if(data.exists()){
                        data.children.forEach {
                            val followings = it.getValue(UserSearchListDetail :: class.java)
                            onChecking(followings)
                        }
                    }
                },

                cancelHandler = {}
            )
    }

    fun setUserSearchingList(currentUser: String, onSetting: (UserSearchListDetail?) -> Unit){
        database.reference
            .child(KEY_USER)
            .valueEventHandler(
                dataChangeHandler = { data ->
                    data.children.forEach {
                        if(currentUser != it.child("id").value.toString()) {
                            val userList = it.getValue(UserSearchListDetail::class.java)
                            onSetting(userList)
                        }
                    }
                },
                cancelHandler = {}
            )
    }

}