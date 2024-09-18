package com.appAllFriendsNearby.talk.dataBase

import android.content.SharedPreferences
import android.util.Log
import kotlinx.coroutines.coroutineScope

///////////////////Записываем данные на нового пользователя в БД
suspend fun writeNewUserToDB(sharedPreferences: SharedPreferences) = coroutineScope {
    /////////////////Запись имени и телефона
    val user = mapOf<Any, Any>(
        USER_NAME to sharedPreferences.getString(USER_NAME, "").toString(),
        USER_PHOTO to sharedPreferences.getString(USER_PHOTO, "").toString(),
        USER_PHONE to sharedPreferences.getString(USER_PHONE, "").toString()
    )
    database
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
//////////////////////////Проверяем, есть ли пользователь в БД
suspend fun checkExistsUserData() = coroutineScope  {
    var flag = false
    currentUser?.let {
        database.child(USERS).child(USER_ID).child(it.uid).child(USER_DATA).child(USER_NAME)
            .get().addOnSuccessListener { result -> ///////Если нет имени
                if (result.value == null) {
                    flag = true
                }
            }
    }
    return@coroutineScope flag
}