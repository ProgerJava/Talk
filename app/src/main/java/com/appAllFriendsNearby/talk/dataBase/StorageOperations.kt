package com.appAllFriendsNearby.talk.dataBase

import android.net.Uri
import android.util.Log
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.delay


suspend fun addToStorageUserProfilePhoto (urlUserPhoto: Uri): String = coroutineScope {
    var referenceUserPhoto = ""
    val reference = STORAGE_O
        .child(USERS)
        .child(USER_ID_O)
        .child(USER_PHOTO)
    reference.putFile(urlUserPhoto).addOnSuccessListener {
        reference.downloadUrl.addOnCompleteListener {downloadUrl ->
            referenceUserPhoto = downloadUrl.result.toString()
        }.addOnFailureListener {downloadUrl->
            Log.println(Log.ERROR, "addToStorageUserProfilePhoto", downloadUrl.message.toString())
        }
    }.addOnFailureListener { result ->
        Log.println(Log.ERROR, "addToStorageUserProfilePhoto", result.message.toString())
    }
    while (referenceUserPhoto.isEmpty()) {
        delay(100)
    }
    return@coroutineScope referenceUserPhoto
}

/*suspend fun getStorageUserProfilePhoto(userID: String) : String = coroutineScope {
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
}*/
