package com.appAllFriendsNearby.talk.view.fragment

import android.content.SharedPreferences
import android.os.Build
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.OnBackPressedCallback
import androidx.annotation.RequiresApi
import androidx.core.widget.doOnTextChanged
import androidx.fragment.app.Fragment
import com.appAllFriendsNearby.talk.R
import com.appAllFriendsNearby.talk.databinding.FragmentRegistrationBinding
import com.appAllFriendsNearby.talk.di.MyApplication
import com.appAllFriendsNearby.talk.model.RegistrationModel
import com.appAllFriendsNearby.talk.view.activity.RegistrationActivity
import com.appAllFriendsNearby.talk.viewModel.RegistrationViewModel
import javax.inject.Inject

class RegistrationFragment : Fragment() {

    private lateinit var binding: FragmentRegistrationBinding

    private lateinit var registrationActivity: RegistrationActivity
    @Inject
    lateinit var viewModel: RegistrationViewModel
    @Inject
    lateinit var registrationModel: RegistrationModel
    @Inject
    lateinit var sharedPreferences: SharedPreferences
    @Inject
    lateinit var sharedPreferencesEditor: SharedPreferences.Editor
    private var alreadyRegisteredFlag = false


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        binding = FragmentRegistrationBinding.inflate(inflater, container, false)
        registrationActivity = activity as RegistrationActivity
        onBackPressed()

        (requireActivity().application as MyApplication).appComponent.inject(this)

        binding.buttonNext.setOnClickListener {
            verification()
        }
        viewModel.flagUserConnect.observe(registrationActivity) {
            binding.progressBar.visibility = View.GONE
        }
        binding.alreadyRegistered.setOnClickListener {
            alreadyRegisteredFlag = !alreadyRegisteredFlag
             if (alreadyRegisteredFlag) {
                 binding.alreadyRegistered.text = getString(R.string.login)
                 binding.buttonNext.text = getString(R.string.signIn)
            }else {
                 binding.alreadyRegistered.text = getString(R.string.alreadyRegistered)
                 binding.buttonNext.text = getString(R.string.login)
            }
        }
        ////////////////////Обработка полей с ошибками
        binding.personEmail.doOnTextChanged { _, _, _, _ ->
            binding.textInputLayoutEmail.error = null
        }
        binding.personPassword.doOnTextChanged { _, _, _, _ ->
            binding.textInputLayoutPassword.error = null
        }


        return binding.root
    }
    ////////////////////Проверка корректности имени и номера
    private fun verification() {
        if (binding.personEmail.text!!.isEmpty()) {
            binding.textInputLayoutEmail.error = getString(R.string.firstEnterYourEmail)
        }
        else if (binding.personPassword.text!!.isEmpty()) {
            binding.textInputLayoutPassword.error = getString(R.string.firstEnterYourPassword)
        } else {
            binding.progressBar.visibility = View.VISIBLE
            if (binding.buttonNext.text == getString(R.string.login)) {
                viewModel.login(binding.personEmail.text.toString(), binding.personPassword.text.toString(), registrationActivity)
            }else {
                viewModel.signIn(binding.personEmail.text.toString(), binding.personPassword.text.toString(), registrationActivity)
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