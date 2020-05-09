package com.anguyen.mymap.temporary_unused.search

interface OnSearchItemsClickListener {

    fun onSendRequestClickHandler(receiverId: String, status: String?)

    fun onWaitingClickHandler(status: String?)

    fun onFollowingClickHandler(status: String?)

}