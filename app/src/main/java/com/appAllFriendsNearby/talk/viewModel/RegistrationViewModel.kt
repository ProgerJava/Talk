package com.appAllFriendsNearby.talk.viewModel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.appAllFriendsNearby.talk.model.RegistrationModel
import kotlinx.coroutines.async
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.delay


class RegistrationViewModel(
    private val registrationModel: RegistrationModel,
    application: Application
) : AndroidViewModel(application) {

    ///////////////////////////Observe liveData. Слушатель отправки сообщений
    val sendMessageFlag: MutableLiveData<Boolean> = MutableLiveData<Boolean>()


    suspend fun setPhoneNumber(number: String) = coroutineScope {
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
class RegistrationViewModelFabric(private val registrationModel: RegistrationModel, private val application: Application) :
    ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return RegistrationViewModel(registrationModel, application) as T
    }
}