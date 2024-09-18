package com.appAllFriendsNearby.talk.dataBase

import android.content.SharedPreferences
import android.util.Log
import androidx.core.net.toUri
import kotlinx.coroutines.coroutineScope


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