package com.anguyen.mymap.models

import com.anguyen.mymap.commons.isEmailValid
import com.anguyen.mymap.commons.isPasswordValid

data class LoginDetail constructor(var email: String = "",
                                   var password: String = ""){

    fun isValid() = isEmailValid(email) && isPasswordValid(password)

}