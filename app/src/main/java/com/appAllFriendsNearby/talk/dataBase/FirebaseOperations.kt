package com.appAllFriendsNearby.talk.dataBase

import android.content.SharedPreferences
import android.util.Log
import com.appAllFriendsNearby.talk.dataBase.dataClass.UserMessagesWithCompanionDataClass
import com.appAllFriendsNearby.talk.tools.constants.COMPANION_ID
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

///////////////////Записываем данные на нового пользователя в БД
suspend fun writeNewUserToDB(sharedPreferences: SharedPreferences) = coroutineScope {
    /////////////////Запись имени, фото и телефона
    val user = mapOf<Any, Any>(
        USER_NAME to sharedPreferences.getString(USER_NAME, "").toString(),
        USER_PHOTO to sharedPreferences.getString(USER_PHOTO, "").toString(),
        USER_EMAIL to sharedPreferences.getString(USER_EMAIL, "").toString(),
        USER_NICK to sharedPreferences.getString(USER_NICK, "").toString()
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
suspend fun removeSelectMessages (deletionList: List<UserMessagesWithCompanionDataClass>) : Boolean = coroutineScope {
    var flag = false
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
        if (i == deletionList.size-1) {
            flag = true
        }
    }
    while (!flag) {
        delay(100)
    }
    return@coroutineScope flag
}


////////////////Если флаг false - ник уникален
suspend fun checkExistsUserNick (nick: String): Boolean = coroutineScope {
    var flag: Boolean? = null
    DATABASE_O.child(USERS).child(USER_ID)
        .get().addOnSuccessListener { it -> ///////Если нет имени
            if (it.childrenCount.toInt() != 0) {
                (it.value as HashMap<*, *>).forEach { (_, value) ->
                    val result = (value as HashMap<*, *>)
                    flag = if (result.containsKey(USER_NICK)) {
                        val nickFromDb = result[USER_NICK] as String
                        (nickFromDb == nick)
                    }else {
                        false
                    }
                }
                println()
            } else {
                flag = false
            }
        }
    while (flag == null) {
        delay(100)
    }
    return@coroutineScope flag!!
}