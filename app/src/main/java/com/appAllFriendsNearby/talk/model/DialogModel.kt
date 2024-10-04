package com.appAllFriendsNearby.talk.model


import android.util.Log
import com.appAllFriendsNearby.talk.dataBase.MESSAGE
import com.appAllFriendsNearby.talk.dataBase.RECIPIENT
import com.appAllFriendsNearby.talk.dataBase.SENDER
import com.appAllFriendsNearby.talk.dataBase.TIMESTAMP
import com.appAllFriendsNearby.talk.dataBase.USERS
import com.appAllFriendsNearby.talk.dataBase.USER_CONNECTION
import com.appAllFriendsNearby.talk.dataBase.USER_DATA
import com.appAllFriendsNearby.talk.dataBase.USER_DIALOGS
import com.appAllFriendsNearby.talk.dataBase.USER_ID
import com.appAllFriendsNearby.talk.dataBase.USER_NAME
import com.appAllFriendsNearby.talk.dataBase.USER_PHONE
import com.appAllFriendsNearby.talk.dataBase.USER_PHOTO
import com.appAllFriendsNearby.talk.dataBase.dataClass.CardUserDataClass
import com.appAllFriendsNearby.talk.dataBase.DATABASE_O
import com.appAllFriendsNearby.talk.dataBase.KEY
import com.appAllFriendsNearby.talk.dataBase.USER_ID_O
import com.appAllFriendsNearby.talk.tools.constants.COMPANION_ID
import com.google.firebase.database.ServerValue
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.delay
import javax.inject.Inject

class DialogModel @Inject constructor() {

    ///////////////////Получаем данные по пользователю на основе Id
    suspend fun getUserDataById (idCurrentUser: String) : CardUserDataClass = coroutineScope {
        var cardUserDataClass: CardUserDataClass? = null
        DATABASE_O.child(USERS).child(USER_ID).child(idCurrentUser).child(USER_DATA)
            .get().addOnSuccessListener { it ->
                val userData = it.value as HashMap <*, *>
                cardUserDataClass = CardUserDataClass(
                    idCurrentUser,
                    userData[USER_NAME] as String,
                    userData[USER_PHONE] as String,
                    userData[USER_PHOTO] as String,
                    userData[USER_CONNECTION] as Boolean?
                )
            }.addOnFailureListener {
                Log.e("firebase", "Error getting data", it)
            }

        while (cardUserDataClass == null) {
            delay(100)
        }

        return@coroutineScope cardUserDataClass!!
    }
    ///////////////////////Отправляем сообщение пользователю
    suspend fun sendMessage(message: String, userIdCompanion: String) = coroutineScope {
        val key = DATABASE_O.child(USERS).child(USER_ID).child(USER_ID_O).child(USER_DIALOGS).child(COMPANION_ID).child(userIdCompanion).push().key
        val pathCurrentUserDialog = "$USERS/$USER_ID/$USER_ID_O/$USER_DIALOGS/$COMPANION_ID/$userIdCompanion"
        val pathCompanionUserDialog = "$USERS/$USER_ID/$userIdCompanion/$USER_DIALOGS/$COMPANION_ID/$USER_ID_O"
        val mapMessage = mapOf<String, Any>(
            SENDER to USER_ID_O,
            RECIPIENT to userIdCompanion,
            MESSAGE to message,
            TIMESTAMP to ServerValue.TIMESTAMP,
            KEY to key.toString()
        )
        val mapDialog = mapOf<String, Any>(
            "$pathCurrentUserDialog/$key" to mapMessage,
            "$pathCompanionUserDialog/$key" to mapMessage
        )
        DATABASE_O.updateChildren(mapDialog)
            .addOnSuccessListener {}
            .addOnFailureListener {
            Log.println(Log.ERROR, "sendMessage", it.message.toString())
        }
    }

}