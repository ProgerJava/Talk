package com.appAllFriendsNearby.talk.view

import com.appAllFriendsNearby.talk.dataBase.dataClass.UserMessagesWithCompanionDataClass

interface OnClickRemoveItem {

    fun addItem (messages: UserMessagesWithCompanionDataClass)
    fun removeItem (messages: UserMessagesWithCompanionDataClass)


}