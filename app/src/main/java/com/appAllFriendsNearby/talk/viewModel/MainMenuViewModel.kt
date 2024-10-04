package com.appAllFriendsNearby.talk.viewModel

import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.appAllFriendsNearby.talk.dataBase.DATABASE_O
import com.appAllFriendsNearby.talk.dataBase.MESSAGE
import com.appAllFriendsNearby.talk.dataBase.SENDER
import com.appAllFriendsNearby.talk.dataBase.TIMESTAMP
import com.appAllFriendsNearby.talk.dataBase.USERS
import com.appAllFriendsNearby.talk.dataBase.USER_DIALOGS
import com.appAllFriendsNearby.talk.dataBase.USER_ID
import com.appAllFriendsNearby.talk.dataBase.USER_ID_O
import com.appAllFriendsNearby.talk.dataBase.dataClass.CardUserDataClass
import com.appAllFriendsNearby.talk.dataBase.dataClass.UserDialogsDataClass
import com.appAllFriendsNearby.talk.model.MainMenuModel
import com.appAllFriendsNearby.talk.tools.constants.COMPANION_ID
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import javax.inject.Inject

class MainMenuViewModel @Inject constructor (private val mainMenuModel: MainMenuModel) : ViewModel () {

    val listSearchView = MutableLiveData <MutableList<CardUserDataClass>> ()
    val listAllUsers = MutableLiveData <MutableList<CardUserDataClass>> ()
    val currentUser = MutableLiveData <CardUserDataClass> ()
    val userDialogs = MutableLiveData <MutableList<UserDialogsDataClass>> ()

    private val scope = CoroutineScope(Dispatchers.Main)



    fun getAllUsers () {
        scope.launch {
            listAllUsers.value = async {mainMenuModel.getAllUsers()}.await()
        }
    }
    fun getCurrentUserData () {
        scope.launch {
            currentUser.value = async {mainMenuModel.getCurrentUserDataById(USER_ID_O)}.await()
        }
    }
    fun getUserByRequestSearchView(newText: String) {
        if (newText.isEmpty()) {
            listSearchView.value = listAllUsers.value
        }
        else if (listAllUsers.value?.isNotEmpty() == true) {
            scope.launch {
                val mutableList = mutableListOf<CardUserDataClass>()
                listAllUsers.value!!.forEach { cardUserDataClass ->
                    if (cardUserDataClass.userName.contains(
                            newText,
                            ignoreCase = true
                        ) || cardUserDataClass.userPhone.contains(newText, ignoreCase = true)
                    ) {
                        mutableList.add(cardUserDataClass)
                    }
                }
                listSearchView.value = mutableList
            }
        }
    }
    ///////////////////Слушатель всех диалогов пользователя
    fun getAllUserDialogs() {
        val dialogs = mutableListOf<UserDialogsDataClass>()
        var countDialogs = 0
        scope.launch {
            val postListener = object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    dialogs.clear()
                    countDialogs = snapshot.childrenCount.toInt()
                    (snapshot.value as HashMap<*, *>).forEach { (key, value) ->
                        scope.launch {
                            val result = value as HashMap<*, *>
                            val lastMessage = result.entries.last().value as HashMap<*, *>
                            val userData = async {mainMenuModel.getCurrentUserDataById(key as String)}.await()
                            //////////////Добавляем id, сообщение последнее, остальное пока по умолчанию
                            dialogs.add(
                                UserDialogsDataClass(
                                    key as String,
                                    lastMessage[SENDER] as String,
                                    userData.userName,
                                    userData.userPhoto,
                                    lastMessage[MESSAGE] as String,
                                    userData.userConnection,
                                    lastMessage[TIMESTAMP] as Long
                                )
                            )
                        }
                    }

                }

                override fun onCancelled(error: DatabaseError) {
                    Log.println(Log.ERROR, "Error getting data", error.message)
                }

            }
            DATABASE_O.child(USERS).child(USER_ID).child(USER_ID_O).child(USER_DIALOGS)
                .child(COMPANION_ID)
                .addValueEventListener(postListener)

            while (dialogs.isEmpty()) {
                delay(100)
            }
            userDialogs.value = dialogs
        }

    }


}
class MainMenuFabric @Inject constructor(private val mainMenuModel: MainMenuModel) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return (MainMenuViewModel (mainMenuModel) as T)
    }
}