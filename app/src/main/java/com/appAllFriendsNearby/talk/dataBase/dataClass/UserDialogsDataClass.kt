package com.appAllFriendsNearby.talk.dataBase.dataClass

data class UserDialogsDataClass(
    val userCompanion: String,
    val userSender: String,
    val userName: String,
    val userPhoto: String,
    val userLastMessage: String?,
    val userConnection: Boolean?,
    val timestamp: Long
)