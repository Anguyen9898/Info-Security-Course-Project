package com.anguyen.mymap.ui.views

import android.widget.EditText

interface BottomDialogLoginView: BaseView {

    fun showEmailError()

    fun showPasswordError(count: Int)

    fun onEmptyFieldsError(vararg emptyFields: EditText)

}