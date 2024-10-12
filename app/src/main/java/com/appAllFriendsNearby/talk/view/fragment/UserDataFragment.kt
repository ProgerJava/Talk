package com.appAllFriendsNearby.talk.view.fragment

import android.content.Intent
import android.content.SharedPreferences
import android.os.Build
import android.os.Bundle
import android.provider.MediaStore
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.OnBackPressedCallback
import androidx.annotation.RequiresApi
import androidx.core.net.toUri
import androidx.core.widget.doOnTextChanged
import com.appAllFriendsNearby.talk.R
import com.appAllFriendsNearby.talk.dataBase.USER_NAME
import com.appAllFriendsNearby.talk.dataBase.USER_EMAIL
import com.appAllFriendsNearby.talk.dataBase.USER_PHOTO
import com.appAllFriendsNearby.talk.dataBase.SIGN_IN
import com.appAllFriendsNearby.talk.dataBase.USER_NICK
import com.appAllFriendsNearby.talk.dataBase.addToStorageUserProfilePhoto
import com.appAllFriendsNearby.talk.dataBase.checkExistsUserNick
import com.appAllFriendsNearby.talk.dataBase.writeNewUserToDB
import com.appAllFriendsNearby.talk.databinding.FragmentUserDataBinding
import com.appAllFriendsNearby.talk.di.MyApplication
import com.appAllFriendsNearby.talk.tools.constants.CODE_PICK_IMAGE
import com.appAllFriendsNearby.talk.tools.constants.CROPPED_URI
import com.appAllFriendsNearby.talk.tools.generalStaticFunction.showToast
import com.appAllFriendsNearby.talk.view.activity.MainMenuActivity
import com.appAllFriendsNearby.talk.view.activity.RegistrationActivity
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import javax.inject.Inject


class UserDataFragment : Fragment() {

    private lateinit var binding: FragmentUserDataBinding
    private lateinit var registrationActivity: RegistrationActivity
    @Inject
    lateinit var sharedPreferences: SharedPreferences
    @Inject
    lateinit var sharedPreferencesEditor: SharedPreferences.Editor
    private var croppedUri = ""

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        binding = FragmentUserDataBinding.inflate(inflater, container, false)
        registrationActivity = activity as RegistrationActivity
        onBackPressed()
        (requireActivity().application as MyApplication).appComponent.inject(this)
        ////////////////////Если есть информация о пользовательском фото
        if (sharedPreferences.getString(CROPPED_URI, "").toString().isNotEmpty()) {
            setPhoto()
        }
        //////////////////Запускаем выбор и обрезку фото
        binding.cardView.setOnClickListener {
            ///////////////////////Обрезка фото
            val intent = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
            registrationActivity.startActivityForResult(intent, CODE_PICK_IMAGE)
            awaitPhoto()
        }
        binding.buttonNext.setOnClickListener {
            CoroutineScope(Dispatchers.Main).launch {
                checkCorrectField(
                    binding.insertPhoto.tag.toString(),
                    binding.personName.text.toString().trim(),
                    binding.personNick.text.toString().trim()
                )
            }
        }
        //////////////////отключаем ошибки
        binding.personName.doOnTextChanged { _, _, _, _ ->
            binding.textInputLayoutName.error = null
        }
        binding.personNick.doOnTextChanged { _, _, _, _ ->
            binding.textInputLayoutNickname.error = null
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
        binding.buttonSetImage.visibility = View.GONE
        binding.insertPhoto.visibility = View.VISIBLE
    }
    private suspend fun checkCorrectField(tagPhoto: String, personName: String, personNick: String) = coroutineScope{
        if (tagPhoto == getString(R.string.tagPhoto)) {
            showToast(registrationActivity, R.string.firstAddYourPhoto)
        } else if (personName.length < 2) {
            binding.textInputLayoutName.error = getString(R.string.enterCorrectName)
        }
        else if (personNick.length < 4) {
            binding.textInputLayoutNickname.error = getString(R.string.minimumNickNameLength)
        }
        else if (personNick.contains(" ")) {
            binding.textInputLayoutNickname.error = getString(R.string.removeSpaces)
        }
        else if (personName.contains(" ")) {
            binding.textInputLayoutName.error = getString(R.string.removeSpaces)
        }
        else if (async {checkExistsUserNick(binding.personNick.text.toString())}.await()) {
            binding.textInputLayoutName.error = getString(R.string.enterAnotherNick)
        }
        else {
            binding.progressBar.visibility = View.VISIBLE
            /////////////////////Если поля заполнены корректно, сохраняем данные в SP, заносим в базу данных и
            ///////////идем на следующий экран
            CoroutineScope(Dispatchers.Main).launch {
                val userPhoto = async {addToStorageUserProfilePhoto(croppedUri.toUri())}.await() ////////////добавляем фото в storage и получаем на него ссылку
                with (sharedPreferencesEditor) {
                    putString(USER_NAME, personName).apply()
                    putString(USER_PHOTO, userPhoto).apply()
                    putString(USER_NICK, "#$personNick").apply()
                }
                writeNewUserToDB(sharedPreferences)
                startActivity(Intent(registrationActivity, MainMenuActivity::class.java))
                //////////////////////////Сохраняем состояние активити - MainMenu
                sharedPreferencesEditor.putString(SIGN_IN, getString(R.string.statusUserSignIn)).apply()
                requireActivity().finish()
            }
        }
    }
    ///////////////////Функция выхода из приложения
    private fun onBackPressed() {
        requireActivity().onBackPressedDispatcher.addCallback(object : OnBackPressedCallback(true) {
            @RequiresApi(api = Build.VERSION_CODES.O)
            override fun handleOnBackPressed() {
                requireActivity().finish()
            }
        })
    }
}