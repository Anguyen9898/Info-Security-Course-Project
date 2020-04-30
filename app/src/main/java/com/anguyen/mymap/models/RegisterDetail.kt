package com.anguyen.mymap.models

import com.anguyen.mymap.commons.arePasswordsSame
import com.anguyen.mymap.commons.isEmailValid
import com.anguyen.mymap.commons.isUsernameValid

data class RegisterDetail (var username: String = "",
                           var email: String = "",
                           var password: String = "",
                           var repeatPassword: String = ""){

    fun isValid() = isUsernameValid(username)
            && isEmailValid(email)
            && arePasswordsSame(password, repeatPassword)

}