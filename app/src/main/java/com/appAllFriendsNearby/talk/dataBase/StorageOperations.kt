package com.appAllFriendsNearby.talk.dataBase

import android.content.SharedPreferences
import android.util.Log
import androidx.core.net.toUri
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.delay


suspend fun addToStorageUserProfilePhoto (sharedPreferences: SharedPreferences) = coroutineScope {
    val file = sharedPreferences.getString(USER_PHOTO, "").toString().toUri()
    val reference = currentUser?.uid?.let {
        storage
            .child(USERS)
            .child(it)
            .child(USER_PHOTO)
        }
    reference?.putFile(file)?.addOnCompleteListener {
        Log.println(Log.INFO, "addToStorageUserProfilePhoto", it.result.toString())
    }?.addOnFailureListener {
        Log.println(Log.ERROR, "addToStorageUserProfilePhoto", it.message.toString())
    }
}

suspend fun getStorageUserProfilePhoto(userID: String) : String = coroutineScope {
    var result = ""
    storage.child(USERS).child(userID).child(USER_PHOTO)
        .downloadUrl
        .addOnSuccessListener {it ->
            result = it.toString()
        }.addOnFailureListener {
            Log.println(Log.ERROR, "getStorageUserProfilePhoto", it.message.toString())
        }
    while (result.isEmpty()) {
        delay(100)
    }
    return@coroutineScope result
}
