package com.appAllFriendsNearby.talk.view.fragment

import android.content.Context.MODE_PRIVATE
import android.content.SharedPreferences
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.ViewModelProvider
import com.appAllFriendsNearby.talk.R
import com.appAllFriendsNearby.talk.databinding.FragmentRegistrationBinding
import com.appAllFriendsNearby.talk.model.RegistrationModel
import com.appAllFriendsNearby.talk.tools.constants.MAIN
import com.appAllFriendsNearby.talk.tools.generalStaticFunction.showToast
import com.appAllFriendsNearby.talk.view.activity.RegistrationActivity
import com.appAllFriendsNearby.talk.viewModel.RegistrationViewModel
import com.appAllFriendsNearby.talk.viewModel.RegistrationViewModelFabric
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class RegistrationFragment : Fragment() {

    private lateinit var binding: FragmentRegistrationBinding
    private lateinit var viewModel: RegistrationViewModel
    private lateinit var registrationModel: RegistrationModel
    private lateinit var registrationActivity: RegistrationActivity
    private lateinit var sharedPreferences: SharedPreferences


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        binding = FragmentRegistrationBinding.inflate(inflater, container, false)
        registrationActivity = activity as RegistrationActivity
        sharedPreferences = registrationActivity.getSharedPreferences(MAIN, MODE_PRIVATE)
        /////////////////ViewModel
        registrationModel = RegistrationModel(registrationActivity, sharedPreferences.edit())
        viewModel = ViewModelProvider (this, RegistrationViewModelFabric(registrationModel, registrationActivity.application))[RegistrationViewModel::class.java]

        /////////////////////Слушатель отправки кода на телефон пользователя
        viewModel.sendMessageFlag.observe(registrationActivity) {
            if (it) {
                binding.personCode.visibility = View.VISIBLE /////////////код отправлен, подключаем поле "код" для ввода, меняем название кнопки
                binding.buttonNext.text = registrationActivity.getString(R.string.next)
            }
        }

        binding.buttonNext.setOnClickListener {
            CoroutineScope(Dispatchers.Main).launch {
                ////////////Если кнопка имеет название "получить код"
                if (binding.buttonNext.text == registrationActivity.getString(R.string.getCode)) {
                    verification()
                } else {
                    if (binding.personCode.text.isNotEmpty()) {
                        viewModel.signInWithPhoneAuthCredential(binding.personCode.text.toString(), binding.personPhone.text.toString())
                    } else {
                        showToast(registrationActivity, R.string.firstEnterYourCode)
                    }
                }
            }
        }


        return binding.root
    }
    ////////////////////Проверка корректности имени и номера
    private fun verification() {
        if (binding.personPhone.text.length != 10) {
            showToast(registrationActivity, R.string.firstEnterYourPhone)
        } else {
            CoroutineScope(Dispatchers.Main).launch {
                viewModel.setPhoneNumber("+7${binding.personPhone.text.trim()}")
            }
        }
    }

}