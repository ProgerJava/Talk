package com.appAllFriendsNearby.talk.model

import android.content.Intent
import android.content.SharedPreferences
import android.util.Log
import com.appAllFriendsNearby.talk.R
import com.appAllFriendsNearby.talk.dataBase.USERS
import com.appAllFriendsNearby.talk.dataBase.LOG_IN
import com.appAllFriendsNearby.talk.dataBase.USER_DATA
import com.appAllFriendsNearby.talk.dataBase.USER_ID
import com.appAllFriendsNearby.talk.dataBase.USER_NAME
import com.appAllFriendsNearby.talk.dataBase.USER_EMAIL
import com.appAllFriendsNearby.talk.dataBase.auth
import com.appAllFriendsNearby.talk.dataBase.DATABASE_O
import com.appAllFriendsNearby.talk.dataBase.SIGN_IN
import com.appAllFriendsNearby.talk.dataBase.USER_ID_O
import com.appAllFriendsNearby.talk.dataBase.USER_PASSWORD
import com.appAllFriendsNearby.talk.dataBase.currentUser
import com.appAllFriendsNearby.talk.tools.constants.USER_DATA_FRAGMENT
import com.appAllFriendsNearby.talk.tools.constants.USER_DATA_FRAGMENT_REPLACE
import com.appAllFriendsNearby.talk.tools.generalStaticFunction.showToast
import com.appAllFriendsNearby.talk.view.activity.MainMenuActivity
import com.appAllFriendsNearby.talk.view.activity.RegistrationActivity
import kotlinx.coroutines.async
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import javax.inject.Inject

class RegistrationModel @Inject constructor(private val sharedPreferencesEditor: SharedPreferences.Editor) {


    /////////////////////Регистрация нового пользователя
    suspend fun createUserWithEmailAndPassWord (email: String, password: String, activity: RegistrationActivity): Boolean = coroutineScope {
        var flag: Boolean? = null
        auth.createUserWithEmailAndPassword(email, password)
            .addOnSuccessListener {
                USER_ID_O = it.user!!.uid
                changeFragment(email, password, activity)
                flag = true
            }.addOnFailureListener {
                flag = false
                showToast(activity, it.message.toString())
                Log.println(Log.ERROR, "createUserWithEmailAndPassWord", it.message.toString())
            }
        while (flag == null) {
            delay(100)
        }
        return@coroutineScope flag!!
    }
    /////////////////////Функция для входа в приложение. Возвращает Boolean как результат
    suspend fun signInWithEmailAndPassword (email: String, password: String, activity: RegistrationActivity): Boolean = coroutineScope {
        var flag: Boolean? = null
        auth.signInWithEmailAndPassword(email, password).addOnSuccessListener {
            USER_ID_O = it.user!!.uid
            launch {
                if (async{checkExistsUserData()}.await()) {
                    activity.startActivity(Intent(activity, MainMenuActivity::class.java))
                    ////////////////////Сохраняем информацию о том, что пользователь прошел верификацию
                    sharedPreferencesEditor.putString(SIGN_IN, activity.getString(R.string.statusUserSignIn)).commit()
                    activity.finish()
                }else {
                    changeFragment(email, password, activity)
                }
            }
        }.addOnFailureListener {
            flag = false
            showToast(activity, it.message.toString())
        }
        while (flag == null) {
            delay(100)
        }
        return@coroutineScope flag!!
    }
    private fun changeFragment (email: String, password: String, activity: RegistrationActivity) {
        ///////////////////Сохраняем данные пользователя во внутренню память устройства
        sharedPreferencesEditor.putString(USER_ID, USER_ID_O).commit()
        sharedPreferencesEditor.putString(USER_EMAIL, email).commit()
        sharedPreferencesEditor.putString(USER_PASSWORD, password).commit()
        /////////////////////Переходим в активити добавления персональных данных
        activity.changeFragment(USER_DATA_FRAGMENT_REPLACE)
        ////////////////////Сохраняем информацию о том, что пользователь подтвердил почту
        sharedPreferencesEditor.putString(LOG_IN, activity.getString(R.string.statusUserLoginTrue)).commit()
    }

    //////////////////////////Проверяем, вводил ли пользователь данные в БД
    private suspend fun checkExistsUserData() : Boolean = coroutineScope  {
        var flag: Boolean? = null
        DATABASE_O.child(USERS).child(USER_ID).child(USER_ID_O).child(USER_DATA).child(USER_NAME)
            .get().addOnSuccessListener { result -> ///////Если нет имени
                flag = result.value != null
            }
        while (flag == null) {
            delay(100)
        }
        return@coroutineScope flag!!
    }

}