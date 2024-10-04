package com.appAllFriendsNearby.talk.viewModel

import android.app.Application
import android.content.SharedPreferences
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.appAllFriendsNearby.talk.model.RegistrationModel
import com.appAllFriendsNearby.talk.view.activity.RegistrationActivity
import kotlinx.coroutines.async
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.delay
import javax.inject.Inject


class RegistrationViewModel @Inject constructor(private val registrationModel: RegistrationModel) : ViewModel() {

    ///////////////////////////Observe liveData. Слушатель отправки сообщений
    val sendMessageFlag: MutableLiveData<Boolean> = MutableLiveData<Boolean>()


    suspend fun setPhoneNumber(number: String, registrationActivity: RegistrationActivity) = coroutineScope {
        registrationModel.registrationActivity = registrationActivity
        async {registrationModel.setPhoneNumber(number)}.await()
        while (registrationModel.callBackSendMessage == null) {
            delay(100)
        }
        if (registrationModel.callBackSendMessage == 1) sendMessageFlag.value = true
    }
    suspend fun signInWithPhoneAuthCredential (personCode: String, personPhone: String) {
        registrationModel.signInWithPhoneAuthCredential(personCode, personPhone)
    }

}

@Suppress("UNCHECKED_CAST")
class RegistrationViewModelFabric @Inject constructor(private val registrationModel: RegistrationModel) :
    ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return RegistrationViewModel(registrationModel) as T
    }
}