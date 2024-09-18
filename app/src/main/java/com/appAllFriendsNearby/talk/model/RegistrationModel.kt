package com.appAllFriendsNearby.talk.model

import android.content.Intent
import android.content.SharedPreferences
import android.util.Log
import com.appAllFriendsNearby.talk.R
import com.appAllFriendsNearby.talk.dataBase.USER_CONFIRMATION
import com.appAllFriendsNearby.talk.dataBase.USER_ID
import com.appAllFriendsNearby.talk.dataBase.USER_PHONE
import com.appAllFriendsNearby.talk.dataBase.auth
import com.appAllFriendsNearby.talk.dataBase.checkExistsUserData
import com.appAllFriendsNearby.talk.dataBase.currentUser
import com.appAllFriendsNearby.talk.tools.constants.USER_DATA_FRAGMENT_REPLACE
import com.appAllFriendsNearby.talk.tools.generalStaticFunction.showToast
import com.appAllFriendsNearby.talk.view.activity.MainMenuActivity
import com.appAllFriendsNearby.talk.view.activity.RegistrationActivity
import com.google.firebase.FirebaseException
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException
import com.google.firebase.auth.PhoneAuthCredential
import com.google.firebase.auth.PhoneAuthOptions
import com.google.firebase.auth.PhoneAuthProvider
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.launch
import java.util.concurrent.TimeUnit

class RegistrationModel (private val registrationActivity: RegistrationActivity, private val sharedPreferencesEditor: SharedPreferences.Editor) {

    ///////////////////////////VerificationId
    private lateinit var storedVerificationId: String
    ///////////////////////////resendToken
    private lateinit var resendToken: PhoneAuthProvider.ForceResendingToken
    //////////////////////////callBack. If 0 -> Failure, 1 -> Success. Code send
    var callBackSendMessage : Int? = null


    fun setPhoneNumber(phoneNumber: String) {
        val options = PhoneAuthOptions.newBuilder(auth)
            .setPhoneNumber(phoneNumber) // Phone number to verify
            .setTimeout(60L, TimeUnit.SECONDS) // Timeout and unit
            .setActivity (registrationActivity) // Activity (for callback binding)
            .setCallbacks(getCallbacks()) // OnVerificationStateChangedCallbacks
            .build()
        PhoneAuthProvider.verifyPhoneNumber(options)
    }

    private fun getCallbacks(): PhoneAuthProvider.OnVerificationStateChangedCallbacks {
        val callbacks = object : PhoneAuthProvider.OnVerificationStateChangedCallbacks() {

            override fun onVerificationCompleted(credential: PhoneAuthCredential) {
                Log.println(Log.INFO, "setCallbacks", "onVerificationCompleted:$credential")
                callBackSendMessage = 2
            }

            override fun onVerificationFailed(e: FirebaseException) {
                Log.println(Log.ERROR, "setCallbacks", e.message.toString())
                callBackSendMessage = 0
            }

            override fun onCodeSent(
                verificationId: String,
                token: PhoneAuthProvider.ForceResendingToken
            ) {
                storedVerificationId = verificationId
                resendToken = token
                /////////////Success sending
                callBackSendMessage = 1
            }
        }
        return callbacks
    }

    suspend fun signInWithPhoneAuthCredential(personCode: String, personPhone: String) {
        val credential = PhoneAuthProvider.getCredential(storedVerificationId, personCode)
        auth.signInWithCredential(credential)
            .addOnCompleteListener(registrationActivity) {
                if (it.isSuccessful) {
                    currentUser = it.result.user
                    ///////////////////Сохраняем данные пользователя во внутренню память устройства
                    sharedPreferencesEditor.putString(USER_ID, currentUser?.uid).commit()
                    sharedPreferencesEditor.putString(USER_PHONE, "+7${personPhone}".replace(" ", "")).commit()
                    ////////////////////Проверяем, была ли уже регистрация у пользователя
                    CoroutineScope(Dispatchers.Main).launch {
                        if (!checkExistsUser()) {
                            /////////////////////Переходим в активити добавления персональных данных
                            registrationActivity.changeFragment(USER_DATA_FRAGMENT_REPLACE)
                            ////////////////////Сохраняем информацию о том, что пользователь подтвердил телефон
                            sharedPreferencesEditor.putString(USER_CONFIRMATION, registrationActivity.getString(R.string.statusUserConfirmationTrue)).commit()
                        } else {
                            /////////////////////Переходим в активити главного меню
                            registrationActivity.startActivity(Intent(registrationActivity, MainMenuActivity::class.java))
                        }
                    }
                } else {
                    if (it.exception is FirebaseAuthInvalidCredentialsException) {
                        Log.println(Log.ERROR, "signInWithPhoneAuthCredential", it.exception.toString())
                    }
                    showToast(registrationActivity, R.string.incorrectCode)
                }
            }

    }

    private suspend fun checkExistsUser() = coroutineScope {
        return@coroutineScope async { checkExistsUserData() }.await()
    }
}