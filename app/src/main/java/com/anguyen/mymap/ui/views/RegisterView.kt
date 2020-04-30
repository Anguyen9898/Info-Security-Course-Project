package com.anguyen.mymap.ui.views

import android.widget.EditText
import com.anguyen.mymap.models.RegisterDetail

interface RegisterView: BaseView{

    fun onRegisterSuccess(registerDetail: RegisterDetail?)

    fun onRegisterFail()

    fun showUsernameError(count: Int)

    fun showEmailError()

    fun showPhoneNumberFormatError()

    fun showPasswordLengthError(count: Int)

    fun showPasswordMatchingError()

    fun onEmptyFieldsError(vararg emptyFields: EditText)

}