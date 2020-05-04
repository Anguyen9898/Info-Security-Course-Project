package com.anguyen.mymap.ui.views

import com.anguyen.mymap.models.UserSearchListDetail

interface SearchView: BaseView {
    fun setUserSearchingList(user: UserSearchListDetail?)
}