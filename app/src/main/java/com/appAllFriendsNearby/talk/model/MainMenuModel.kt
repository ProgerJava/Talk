package com.appAllFriendsNearby.talk.model

import android.util.Log
import com.appAllFriendsNearby.talk.dataBase.USERS
import com.appAllFriendsNearby.talk.dataBase.USER_DATA
import com.appAllFriendsNearby.talk.dataBase.USER_ID
import com.appAllFriendsNearby.talk.dataBase.USER_NAME
import com.appAllFriendsNearby.talk.dataBase.USER_PHONE
import com.appAllFriendsNearby.talk.dataBase.USER_PHOTO
import com.appAllFriendsNearby.talk.dataBase.currentUser
import com.appAllFriendsNearby.talk.dataBase.dataClass.CardUserDataClass
import com.appAllFriendsNearby.talk.dataBase.database
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.delay
import javax.inject.Inject

class MainMenuModel @Inject constructor() {

    suspend fun getAllUsers() : MutableList <CardUserDataClass> = coroutineScope {
        var listWithUser: MutableList<CardUserDataClass>? = null
        var countOfUser = 0
        database.child(USERS).get().addOnSuccessListener {
            listWithUser = mutableListOf()
            it.children.forEach { snapshot ->
                ///////////////Количество всех пользователей, не включая меня
                countOfUser = snapshot.childrenCount.toInt()-1
                snapshot.children.forEach {action ->
                    val userId = action.key
                    if (userId != currentUser?.uid) {
                        val resultValue = action.value as HashMap<*, *>
                        val key = resultValue.keys.first()
                        val userData = resultValue[key] as HashMap<*, *>
                        listWithUser!!.add(
                            CardUserDataClass(
                                userId as String,
                                userData[USER_NAME] as String,
                                userData[USER_PHONE] as String,
                                userData[USER_PHOTO] as String
                            )
                        )
                    }
                }
            }
        }.addOnFailureListener {
            Log.e("firebase", "Error getting data", it)
        }
        while (listWithUser == null || listWithUser?.size != countOfUser) {
            delay(100)
        }
        listWithUser!!.shuffle()
        return@coroutineScope listWithUser!!
    }
    suspend fun getCurrentUserData () : CardUserDataClass = coroutineScope {
        val userId = currentUser?.uid.toString()
        var cardUserDataClass: CardUserDataClass? = null
        database.child(USERS).child(USER_ID).child(userId).child(USER_DATA)
            .get().addOnSuccessListener { it ->
                val userData = it.value as HashMap <*, *>
                cardUserDataClass = CardUserDataClass(
                    userId,
                    userData[USER_NAME] as String,
                    userData[USER_PHONE] as String,
                    userData[USER_PHOTO] as String
                )
            }.addOnFailureListener {
                Log.e("firebase", "Error getting data", it)
            }

        while (cardUserDataClass == null) {
            delay(100)
        }

        return@coroutineScope cardUserDataClass!!
    }

}