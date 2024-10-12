package com.appAllFriendsNearby.talk.viewModel

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.appAllFriendsNearby.talk.dataBase.USER_ID_O
import com.appAllFriendsNearby.talk.model.RegistrationModel
import com.appAllFriendsNearby.talk.view.activity.RegistrationActivity
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.launch
import javax.inject.Inject


class RegistrationViewModel @Inject constructor(private val registrationModel: RegistrationModel) : ViewModel() {

    val flagUserConnect = MutableLiveData<Boolean>()

    fun login(email: String, password: String, registrationActivity: RegistrationActivity) {
        CoroutineScope(Dispatchers.Main).launch {
            flagUserConnect.value = async {registrationModel.createUserWithEmailAndPassWord(email, password, registrationActivity)}.await()
        }
    }
    fun signIn(email: String, password: String, registrationActivity: RegistrationActivity) {
        CoroutineScope(Dispatchers.Main).launch {
            flagUserConnect.value = async {registrationModel.signInWithEmailAndPassword(email, password, registrationActivity)}.await()
        }
    }

}

@Suppress("UNCHECKED_CAST")
class RegistrationViewModelFabric @Inject constructor(private val registrationModel: RegistrationModel) :
    ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return RegistrationViewModel(registrationModel) as T
    }
}