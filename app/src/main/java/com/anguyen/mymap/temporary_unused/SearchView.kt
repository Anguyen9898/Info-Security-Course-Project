package com.anguyen.mymap.temporary_unused

import com.anguyen.mymap.models.UserRespondDetail
import com.anguyen.mymap.ui.views.BaseView

interface SearchView: BaseView {

    fun onSettingUserSearchingList(user: UserRespondDetail?)

    fun onSendRequestSuccessfully()
    fun onSendRequestFailed()

}