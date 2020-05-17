package com.anguyen.mymap.ui.views

import com.anguyen.mymap.models.UserRespondDetail
import com.anguyen.mymap.ui.views.BaseView

interface ProfileFragmentView : BaseView{

    fun showUserInfo(userRespondDetail: UserRespondDetail)

    fun openLoginUI()

    fun onRevokeSuccess()

    fun onProfileUpdateSuccessfully()

    fun onProfileUpdateFailed()

}