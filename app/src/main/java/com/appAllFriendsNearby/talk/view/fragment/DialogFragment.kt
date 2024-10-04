package com.appAllFriendsNearby.talk.view.fragment

import android.os.Build
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.OnBackPressedCallback
import androidx.annotation.RequiresApi
import androidx.recyclerview.widget.LinearLayoutManager
import com.appAllFriendsNearby.talk.dataBase.dataClass.CardUserDataClass
import com.appAllFriendsNearby.talk.databinding.FragmentDialogBinding
import com.appAllFriendsNearby.talk.di.MyApplication
import com.appAllFriendsNearby.talk.tools.constants.MAIN_MENU_FRAGMENT_REPLACE
import com.appAllFriendsNearby.talk.tools.generalStaticFunction.setUserPhoto
import com.appAllFriendsNearby.talk.view.activity.MainMenuActivity
import com.appAllFriendsNearby.talk.view.recyclerView.RecyclerViewAllMessagesWithCompanion
import com.appAllFriendsNearby.talk.viewModel.DialogViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import javax.inject.Inject


class DialogFragment : Fragment() {

    private lateinit var binding: FragmentDialogBinding
    private lateinit var mainMenuActivity: MainMenuActivity
    @Inject
    lateinit var dialogViewModel: DialogViewModel


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        binding = FragmentDialogBinding.inflate(inflater, container, false)
        onBackPressed()
        mainMenuActivity = (requireActivity() as MainMenuActivity)
        (requireActivity().application as MyApplication).appComponent.inject(this)
        ///////////////InitRecycler
        binding.recyclerViewMessages.layoutManager = LinearLayoutManager(requireContext())
        ///////////////////////Получаем данные человека, с которым вступаем в диалог
        dialogViewModel.getUserById()
        ///////////////////////Слушатель обновлений в сообщениях
        dialogViewModel.onDataChangeMessage()
        ////////////////Выход на главный экран
        binding.back.setOnClickListener {
            mainMenuActivity.changeFragment(MAIN_MENU_FRAGMENT_REPLACE)
        }
        //////////////////Устанавливаем данные пользователя, с которым вступаем в диалог
        dialogViewModel.companionUser.observe(requireActivity()) {
            setUserCompanion(it)
        }
        //////////////////Показываем сообщения с пользователем
        dialogViewModel.messages.observe(requireActivity()) {
            binding.recyclerViewMessages.adapter = RecyclerViewAllMessagesWithCompanion(it, binding)
            binding.recyclerViewMessages.scrollToPosition(it.size-1)
        }
        binding.send.setOnClickListener {
            val text = binding.message.text
            if (text.isNotEmpty()) {
                dialogViewModel.sendMessage(text.toString())
                binding.message.text.clear()
            }
        }
        binding.close.setOnClickListener {
            val messages = dialogViewModel.messages.value
            if (messages != null) {
                binding.recyclerViewMessages.adapter = RecyclerViewAllMessagesWithCompanion(messages, binding)
                binding.recyclerViewMessages.scrollToPosition(messages.size - 1)
                binding.constraintDeleteMessage.visibility = View.GONE
                binding.constraintSendMessage.visibility = View.VISIBLE
            }
        }


        return binding.root
    }
    private fun setUserCompanion(cardUserDataClass: CardUserDataClass) {
        binding.userName.text = cardUserDataClass.userName
        CoroutineScope(Dispatchers.Main).launch {
            setUserPhoto(cardUserDataClass.userPhoto, binding.progressBar, binding.currentUserPhoto)
        }
    }
    ///////////////////Функция выхода из приложения
    private fun onBackPressed() {
        requireActivity().onBackPressedDispatcher.addCallback(object : OnBackPressedCallback(true) {
            @RequiresApi(api = Build.VERSION_CODES.O)
            override fun handleOnBackPressed() {
                mainMenuActivity.changeFragment(MAIN_MENU_FRAGMENT_REPLACE)
            }
        })
    }

}