package com.appAllFriendsNearby.talk.view.activity

import android.content.Intent
import android.content.SharedPreferences
import android.content.SharedPreferences.Editor
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.MediaStore
import androidx.fragment.app.FragmentTransaction
import com.appAllFriendsNearby.talk.R
import com.appAllFriendsNearby.talk.dataBase.USER_CONFIRMATION
import com.appAllFriendsNearby.talk.dataBase.USER_ID_O
import com.appAllFriendsNearby.talk.dataBase.USER_REGISTRATION
import com.appAllFriendsNearby.talk.dataBase.auth
import com.appAllFriendsNearby.talk.databinding.ActivityRegistrationBinding
import com.appAllFriendsNearby.talk.di.MyApplication
import com.appAllFriendsNearby.talk.tools.constants.CODE_PICK_IMAGE
import com.appAllFriendsNearby.talk.tools.constants.CROPPED_URI
import com.appAllFriendsNearby.talk.tools.constants.MAIN
import com.appAllFriendsNearby.talk.tools.constants.REGISTRATION_FRAGMENT
import com.appAllFriendsNearby.talk.tools.constants.USER_DATA_FRAGMENT
import com.appAllFriendsNearby.talk.tools.constants.USER_DATA_FRAGMENT_REPLACE
import com.appAllFriendsNearby.talk.view.fragment.RegistrationFragment
import com.appAllFriendsNearby.talk.view.fragment.UserDataFragment
import com.soundcloud.android.crop.Crop
import java.io.File
import javax.inject.Inject


class RegistrationActivity : AppCompatActivity() {


    private lateinit var binding: ActivityRegistrationBinding
    private lateinit var fragmentTransaction: FragmentTransaction

    @Inject
    lateinit var sharedPreferences: SharedPreferences
    @Inject
    lateinit var sharedPreferencesEditor: Editor


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        //////////////////////Привязка и отрисовка интерфейса
        binding = ActivityRegistrationBinding.inflate(layoutInflater)
        setContentView(binding.root)

        (application as MyApplication).appComponent.inject(this)
        if (auth.currentUser != null) {
            USER_ID_O = auth.currentUser!!.uid
        }

        //Проверяем, зарегестрирован ли пользователь
        if (sharedPreferences.getString(USER_REGISTRATION, "") == getString(R.string.statusUserRegistrationTrue)) {
            /////////////////////Переходим в активити главного меню
            startActivity(Intent(this, MainMenuActivity::class.java))
            finish()
        }
        //Если пользователь не зарегестрирован, но подтвердил телефон
        else if (sharedPreferences.getString(USER_CONFIRMATION, "") == getString(R.string.statusUserConfirmationTrue)) {
            changeFragment(USER_DATA_FRAGMENT)
        }else {
            //////////////////////Если впервые тут
            changeFragment(REGISTRATION_FRAGMENT)
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == CODE_PICK_IMAGE && resultCode == RESULT_OK && data != null) {
            val uri = data.data
            if (uri != null) {
                Crop.of(uri, Uri.fromFile(File(this.cacheDir, "cropped")))
                    .asSquare()
                    .start(this)
            }
        } else if (requestCode == Crop.REQUEST_CROP && resultCode == RESULT_OK) {
            sharedPreferencesEditor.putString(CROPPED_URI, Crop.getOutput(data).toString()).commit()
        }
    }

    fun changeFragment (tag: String) {
        fragmentTransaction = supportFragmentManager.beginTransaction()
        when (tag) {
            REGISTRATION_FRAGMENT -> {fragmentTransaction.add(R.id.frameLayout, RegistrationFragment()).commit()}
            USER_DATA_FRAGMENT -> {fragmentTransaction.add(R.id.frameLayout, UserDataFragment()).commit()}
            USER_DATA_FRAGMENT_REPLACE -> {fragmentTransaction.replace(R.id.frameLayout, UserDataFragment()).commit()}
        }
    }
}