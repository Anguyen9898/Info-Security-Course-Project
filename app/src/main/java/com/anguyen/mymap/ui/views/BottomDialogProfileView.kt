package com.anguyen.mymap.ui.views

import android.widget.EditText
import com.anguyen.mymap.models.UserRespondDetail

interface BottomDialogProfileView: BaseView {

    fun onResult(userData: UserRespondDetail?)

}