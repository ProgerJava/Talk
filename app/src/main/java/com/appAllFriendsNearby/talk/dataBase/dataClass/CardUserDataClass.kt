package com.appAllFriendsNearby.talk.dataBase.dataClass

data class CardUserDataClass(
    val userId: String,
    val userName: String,
    val userEmail: String,
    val userPhoto: String,
    val userNick: String,

    val userConnection: Boolean?
)