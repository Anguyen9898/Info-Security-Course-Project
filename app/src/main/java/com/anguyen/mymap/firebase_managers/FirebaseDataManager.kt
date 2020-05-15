package com.anguyen.mymap.firebase_managers


import com.anguyen.mymap.commons.*
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
        userTypeImgUrl: String,
        id: String,
        avatarUrl: String,
        gender: String,
        phoneNumber: String,
        coordinate: CoordinateDetail?,
        notifications: HashMap<String, NotificationDetail?>
    ){

        val userInfo = UserInformationDetail(
            userType,
            userTypeImgUrl,
            avatarUrl,
            gender,
            phoneNumber,
            coordinate,
            notifications
        )

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
                             notifications: HashMap<String, NotificationDetail?>,
                             userId: String,
                             userTypeImgUrl: String
    ){

        database.reference
            .child(KEY_USER)
            .child(userId)
            .singleValueHandler(dataChangeHandler = { data ->

                if(data.child("id").value.toString() != userId){ //Only create data if user login first time

                    val user = UserDetail(userId, account.user!!.displayName!!
                        , account.user!!.email!!)

                    database.reference
                        .child(KEY_USER)
                        .child(userId)
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
                                userTypeImgUrl,
                                userId,
                                avatarUrl,
                                "",
                                phoneNumber,
                                coordinate,
                                notifications
                            )

                        }

                }
            }, cancelHandler = {})

    }

    fun createFacebookUserData(account: AuthResult,
                               coordinate: CoordinateDetail?,
                               notifications: HashMap<String, NotificationDetail?>,
                               userId: String,
                               userTypeImgUrl: String
    ){
        database.reference
            .child(KEY_USER)
            .singleValueHandler(
                dataChangeHandler = { users ->
                    users.children.forEach {
                        if(account.user!!.email!!.toString()
                            == it.child("email").value.toString()){

                            getUserData(it.key.toString()){ conflictUserData ->
                                database.reference
                                    .child(KEY_USER)
                                    .apply {
                                        child(userId).setValue(conflictUserData)
                                        child(it.key.toString()).removeValue()
                                    }

                            }

                        }else{
                            createIfEmailNotExist(
                                account,
                                coordinate,
                                notifications,
                                userId,
                                userTypeImgUrl
                            )
                        }
                    }
                },
                cancelHandler = {}
            )

    }

    private fun createIfEmailNotExist(account: AuthResult,
                                      coordinate: CoordinateDetail?,
                                      notifications: HashMap<String, NotificationDetail?>,
                                      userId: String,
                                      userTypeImgUrl: String){
        database.reference
            .child(userId)
            .singleValueHandler(dataChangeHandler = { data ->

                if(data.child("id").value.toString() != userId){ //Only create data if user login first time

                    val user = UserDetail(
                        userId,
                        account.user!!.displayName!!.toString(),
                        account.user!!.email!!.toString()
                    )

                    database.reference
                        .child(KEY_USER)
                        .child(userId)
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
                                userTypeImgUrl,
                                userId,
                                avatarUrl,
                                "",
                                phoneNumber,
                                coordinate,
                                notifications
                            )

                        }

                }
            }, cancelHandler = {})

    }

    fun updateLocationFieldOnDatabase(userId: String, coordinate: CoordinateDetail){
        database.reference
            .child(KEY_USER)
            .child(userId)
            .child(KEY_LOCATION)
            .updateChildren(coordinate.toMap())
    }

    fun getUsersLocationData(currentUser: String?, onGetting: (String, CoordinateDetail) -> Unit){
        database.reference
            .child(KEY_USER)
            .singleValueHandler(
                dataChangeHandler = { allUser ->
                   allUser.children.forEach { user ->
                       if(user.key != currentUser){ // Skip if user is current user
                           getUserData(user.key.toString()){
                               onGetting(it?.id!!, it.location!!)
                           }

                       }
                   }
                },

                cancelHandler = {}
            )
    }

    fun getUserData(userId: String,
                    onRespond: (UserRespondDetail?) -> Unit){

        database.reference
            .child(KEY_USER)
            .child(userId)
            .singleValueHandler(
                dataChangeHandler = { data ->
                    val userRespond = data.getValue(UserRespondDetail :: class.java)
                    onRespond(userRespond)
                },
                cancelHandler = {}
            )
    }

    fun getUserType(userId: String, onGetting: (String) -> Unit){
        getUserData(userId){
            if(it != null){
                onGetting(it.userType)
            }
        }
    }

    fun changeAvatarUrlOnData(userId: String, url: Any, onChange: (Boolean) -> Unit){
        database.reference
            .child(KEY_USER)
            .child(userId)
            .updateChildren(hashMapOf(Pair(KEY_AVATAR_URL, url)))
            .addOnCompleteListener {
                onChange(it.isSuccessful and it.isComplete)
            }
    }

    fun getNewAvatarUrl(userId: String, onUpdate: (String) -> Unit){
        database.reference
            .child(KEY_USER)
            .child(userId)
            .child(KEY_AVATAR_URL)
            .singleValueHandler(
            dataChangeHandler = { data ->
                onUpdate(data.value.toString())
            },
            cancelHandler = {}
        )
    }

    fun setUserSearchingList(currentUser: String, onSetting: (UserRespondDetail?) -> Unit){
        database.reference
            .child(KEY_USER)
            .singleValueHandler(
                dataChangeHandler = { data ->
                    data.children.forEach {
                        if(currentUser != it.child("id").value.toString()) {
                            val userList = it.getValue(UserRespondDetail::class.java)
                            onSetting(userList)
                        }
                    }
                },
                cancelHandler = {}
            )
    }

    fun saveTokenToDatabase(currentUser: String, token: Any?){
        database.reference
            .child(KEY_USER)
            .child(currentUser)
            .updateChildren(hashMapOf(Pair("newToken", token)))
    }

    fun setNotificationToUserData(sender: String, receiver: String, notifications: NotificationDetail){
        database.reference.child(KEY_USER).apply {
            child(sender)
                .child(notifications.id)
                .child(KEY_NOTIFICATION)
                .updateChildren(notifications.toMap())

            child(receiver)
                .child(notifications.id)
                .child(KEY_NOTIFICATION)
                .updateChildren(notifications.toMap())
        }
    }

//    fun checkNotificationStatus(currentUser: String,
//                                fireStore: FirebaseCloudStoreManager,
//                                onChecking: (NotificationDetail?) -> Unit
//    ){
//        database.reference
//            .child(KEY_USER)
//            .singleValueHandler(
//                dataChangeHandler = { data ->
//                   data.children.forEach { user ->
//                       if(user.key.toString() != currentUser) { //Only check data users which are different from current
//
//                           user.children.forEach {
//                               val notificationId = it.child(KEY_NOTIFICATION).child("receive").value.toString()
//                               fireStore.getRequestFollowNotificationData(notificationId){
//                                       notification -> onChecking(notification)
//                               }
//                           }
//
//                       }
//                       else{
//                           onChecking(null)
//                       }
//                   }
//                },
//                cancelHandler = {}
//            )
//    }

}