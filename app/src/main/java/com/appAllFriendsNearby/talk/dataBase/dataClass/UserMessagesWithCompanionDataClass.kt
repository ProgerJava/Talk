package com.appAllFriendsNearby.talk.dataBase.dataClass

data class UserMessagesWithCompanionDataClass(
    val sender: String,
    val recipient: String,
    val message: String,
    val timestamp: Long,
    val key: String
)