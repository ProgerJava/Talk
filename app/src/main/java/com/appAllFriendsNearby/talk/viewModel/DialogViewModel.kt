package com.appAllFriendsNearby.talk.viewModel

import android.content.SharedPreferences
import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.appAllFriendsNearby.talk.dataBase.MESSAGE
import com.appAllFriendsNearby.talk.dataBase.RECIPIENT
import com.appAllFriendsNearby.talk.dataBase.SENDER
import com.appAllFriendsNearby.talk.dataBase.TIMESTAMP
import com.appAllFriendsNearby.talk.dataBase.USERS
import com.appAllFriendsNearby.talk.dataBase.USER_DIALOGS
import com.appAllFriendsNearby.talk.dataBase.USER_ID
import com.appAllFriendsNearby.talk.dataBase.dataClass.CardUserDataClass
import com.appAllFriendsNearby.talk.dataBase.dataClass.UserMessagesWithCompanionDataClass
import com.appAllFriendsNearby.talk.dataBase.DATABASE_O
import com.appAllFriendsNearby.talk.dataBase.KEY
import com.appAllFriendsNearby.talk.dataBase.USER_ID_O
import com.appAllFriendsNearby.talk.model.DialogModel
import com.appAllFriendsNearby.talk.tools.constants.COMPANION_ID
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import javax.inject.Inject

class DialogViewModel @Inject constructor(
    private val dialogModel: DialogModel,
    sharedPreferences: SharedPreferences
) : ViewModel() {

    private val companionUserId = sharedPreferences.getString(COMPANION_ID, "")?:""
    private val scope = CoroutineScope(Dispatchers.Main)

    val companionUser = MutableLiveData<CardUserDataClass>()
    val messages = MutableLiveData<List<UserMessagesWithCompanionDataClass>>()


    fun getUserById() {
        scope.launch {
            companionUser.value = dialogModel.getUserDataById(companionUserId)
        }
    }
    fun sendMessage(message: String) {
        scope.launch {
            dialogModel.sendMessage(message, companionUserId)
        }
    }
    //////////////////Слушатель изменений в БД
    fun onDataChangeMessage() {
        scope.launch {
            val postListener = object : ValueEventListener {
                override fun onDataChange(dataSnapshot: DataSnapshot) {
                    if (dataSnapshot.childrenCount.toInt() != 0) {
                        val listWithUserMessages = mutableListOf<UserMessagesWithCompanionDataClass>()
                        (dataSnapshot.value as HashMap<*, *>).forEach { (key, value) ->
                            val result = value as HashMap<*, *>
                            listWithUserMessages.add(
                                UserMessagesWithCompanionDataClass(
                                    result[SENDER] as String,
                                    result[RECIPIENT] as String,
                                    result[MESSAGE] as String,
                                    result[TIMESTAMP] as Long,
                                    result[KEY] as String
                                )
                            )
                        }
                        val sorted = listWithUserMessages.sortedBy{it.timestamp}
                        messages.value = sorted
                    }
                }
                override fun onCancelled(databaseError: DatabaseError) {
                    Log.println(Log.ERROR, "onDataChangeMessage", databaseError.message)
                }
            }
            DATABASE_O.child(USERS).child(USER_ID).child(USER_ID_O).child(USER_DIALOGS).child(COMPANION_ID)
                .child(companionUserId).addValueEventListener(postListener)
        }
    }

}

class DialogViewModelFactory @Inject constructor(
    private val dialogModel: DialogModel,
    private val sharedPreferences: SharedPreferences
) : ViewModelProvider.Factory {

    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return DialogViewModel(dialogModel, sharedPreferences) as T
    }

}