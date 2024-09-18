package com.appAllFriendsNearby.talk.view.fragment

import android.content.Context.MODE_PRIVATE
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.provider.MediaStore
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.net.toUri
import com.appAllFriendsNearby.talk.R
import com.appAllFriendsNearby.talk.dataBase.USER_NAME
import com.appAllFriendsNearby.talk.dataBase.USER_PHOTO
import com.appAllFriendsNearby.talk.dataBase.USER_REGISTRATION
import com.appAllFriendsNearby.talk.dataBase.addToStorageUserProfilePhoto
import com.appAllFriendsNearby.talk.dataBase.writeNewUserToDB
import com.appAllFriendsNearby.talk.databinding.FragmentUserDataBinding
import com.appAllFriendsNearby.talk.tools.constants.CODE_PICK_IMAGE
import com.appAllFriendsNearby.talk.tools.constants.CROPPED_URI
import com.appAllFriendsNearby.talk.tools.constants.MAIN
import com.appAllFriendsNearby.talk.tools.generalStaticFunction.showToast
import com.appAllFriendsNearby.talk.view.activity.MainMenuActivity
import com.appAllFriendsNearby.talk.view.activity.RegistrationActivity
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch


class UserDataFragment : Fragment() {

    private lateinit var binding: FragmentUserDataBinding
    private lateinit var registrationActivity: RegistrationActivity
    private lateinit var sharedPreferences: SharedPreferences
    private var croppedUri = ""

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        binding = FragmentUserDataBinding.inflate(inflater, container, false)
        registrationActivity = activity as RegistrationActivity
        sharedPreferences = registrationActivity.getSharedPreferences(MAIN, MODE_PRIVATE)
        ////////////////////Если есть информация о пользовательском фото
        if (sharedPreferences.getString(CROPPED_URI, "").toString().isNotEmpty()) {
            setPhoto()
        }

        binding.insertPhoto.setOnClickListener {
            ///////////////////////Обрезка фото
            val intent = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
            registrationActivity.startActivityForResult(intent, CODE_PICK_IMAGE)
            sharedPreferences.edit().remove(CROPPED_URI).apply()
            awaitPhoto()
        }

        binding.buttonNext.setOnClickListener {
            checkCorrectField()
        }



        return binding.root
    }
    /////////////////////Как только появится URI, загружаем фото
    private fun awaitPhoto () {
        CoroutineScope(Dispatchers.Main).launch {
            while (sharedPreferences.getString(CROPPED_URI, "").toString().isEmpty()){
                delay(100)
            }
            setPhoto()
        }
    }
    private fun setPhoto () {
        croppedUri = sharedPreferences.getString(CROPPED_URI, "").toString()
        val bitmap = MediaStore.Images.Media.getBitmap(registrationActivity.contentResolver, croppedUri.toUri())
        binding.insertPhoto.setImageBitmap(bitmap)
        binding.insertPhoto.tag = croppedUri
    }
    private fun checkCorrectField() {
        if (binding.insertPhoto.tag == getString(R.string.tagPhoto)) {
            showToast(registrationActivity, R.string.firstAddYourPhoto)
        } else if (binding.personName.text.length <= 1) {
            showToast(registrationActivity, R.string.firstEnterYourName)
        } else {
            /////////////////////Если поля заполнены корректно, сохраняем данные в SP, заносим в базу данных и
            ///////////идем на следующий экран
            with (sharedPreferences.edit()) {
                putString(USER_NAME, binding.personName.text.toString().trim()).apply()
                putString(USER_PHOTO, croppedUri).commit()
            }
            CoroutineScope(Dispatchers.Main).launch {
                writeNewUserToDB(sharedPreferences)
            }
            CoroutineScope(Dispatchers.Main).launch {
                addToStorageUserProfilePhoto(sharedPreferences)
            }
        startActivity(Intent(registrationActivity, MainMenuActivity::class.java))
        //////////////////////////Сохраняем состояние активити - MainMenu
            sharedPreferences.edit()
                .putString(USER_REGISTRATION, getString(R.string.statusUserRegistrationTrue))
                .apply()
        }
    }
}