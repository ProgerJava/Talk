package com.appAllFriendsNearby.talk.dataBase

import android.content.SharedPreferences
import android.util.Log
import com.appAllFriendsNearby.talk.dataBase.dataClass.UserMessagesWithCompanionDataClass
import com.appAllFriendsNearby.talk.tools.constants.COMPANION_ID
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.launch

///////////////////Записываем данные на нового пользователя в БД
suspend fun writeNewUserToDB(sharedPreferences: SharedPreferences) = coroutineScope {
    /////////////////Запись имени, фото и телефона
    val user = mapOf<Any, Any>(
        USER_NAME to sharedPreferences.getString(USER_NAME, "").toString(),
        USER_PHOTO to sharedPreferences.getString(USER_PHOTO, "").toString(),
        USER_PHONE to sharedPreferences.getString(USER_PHONE, "").toString()
    )
    DATABASE_O
        .child(USERS)
        .child(USER_ID)
        .child(sharedPreferences.getString(USER_ID, "").toString())
        .child(USER_DATA)
        .setValue(user)
        .addOnSuccessListener {
            Log.println(Log.INFO, "writeNewUserToDB", "success")
        }
        .addOnFailureListener {
            Log.println(Log.ERROR, "writeNewUserToDB", it.message.toString())
        }
}

////////////////////////Устанавливаем статус подключения пользователя
suspend fun setUserConnection (statusConnection: Boolean) = coroutineScope {
    val connection = mapOf<String, Any>(USER_CONNECTION to statusConnection)
    DATABASE_O
        .child(USERS)
        .child(USER_ID)
        .child(USER_ID_O)
        .child(USER_DATA)
        .updateChildren(connection)
        .addOnSuccessListener {
            Log.println(Log.INFO, "setUserConnection", "success")
        }
        .addOnFailureListener {
            Log.println(Log.ERROR, "setUserConnection", it.message.toString())
        }
}
/////////////////////Удаляем сообщения
suspend fun removeSelectMessages (deletionList: List<UserMessagesWithCompanionDataClass>) = coroutineScope {
    val coroutineScope = CoroutineScope(Dispatchers.Main)
    var companionId = ""
    for (i in deletionList.indices) {
        val deletionObject = deletionList[i]
        companionId = if (deletionObject.sender == USER_ID_O) {
            deletionObject.recipient
        }else {
            deletionObject.sender
        }
        coroutineScope.launch {
            DATABASE_O
                .child(USERS)
                .child(USER_ID)
                .child(USER_ID_O)
                .child(USER_DIALOGS)
                .child(COMPANION_ID)
                .child(companionId)
                .child(deletionObject.key)
                .removeValue()
                .addOnSuccessListener {
                    Log.println(Log.INFO, "removeSelectMessages", "success")
                }
                .addOnFailureListener {
                    Log.println(Log.ERROR, "removeSelectMessages", it.message.toString())
                }
        }
    }
}