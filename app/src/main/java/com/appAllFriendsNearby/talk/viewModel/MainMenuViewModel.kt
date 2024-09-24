package com.appAllFriendsNearby.talk.viewModel

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.appAllFriendsNearby.talk.dataBase.dataClass.CardUserDataClass
import com.appAllFriendsNearby.talk.model.MainMenuModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.launch
import javax.inject.Inject

class MainMenuViewModel @Inject constructor (private val mainMenuModel: MainMenuModel) : ViewModel () {

    val listSearchView = MutableLiveData <MutableList<CardUserDataClass>> ()
    val listAllUsers = MutableLiveData <MutableList<CardUserDataClass>> ()
    val currentUser = MutableLiveData <CardUserDataClass> ()



    fun getAllUsers () {
        CoroutineScope(Dispatchers.Main).launch {
            listAllUsers.value = async {mainMenuModel.getAllUsers()}.await()
        }
    }
    fun getCurrentUserData () {
        CoroutineScope(Dispatchers.Main).launch {
            currentUser.value = async {mainMenuModel.getCurrentUserData()}.await()
        }
    }
    fun getUserByRequestSearchView(newText: String) {
        if (newText.isEmpty()) {
            listSearchView.value = listAllUsers.value
        }
        else if (listAllUsers.value?.isNotEmpty() == true) {
            CoroutineScope(Dispatchers.Main).launch {
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


}
class MainMenuFabric @Inject constructor(private val mainMenuModel: MainMenuModel) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return (MainMenuViewModel (mainMenuModel) as T)
    }
}