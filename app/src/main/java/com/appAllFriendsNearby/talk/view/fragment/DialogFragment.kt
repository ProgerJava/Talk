package com.appAllFriendsNearby.talk.view.fragment


import android.annotation.SuppressLint
import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.OnBackPressedCallback
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.annotation.RequiresApi
import androidx.core.widget.doOnTextChanged
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.appAllFriendsNearby.talk.R
import com.appAllFriendsNearby.talk.dataBase.dataClass.CardUserDataClass
import com.appAllFriendsNearby.talk.dataBase.dataClass.UserMessagesWithCompanionDataClass
import com.appAllFriendsNearby.talk.dataBase.removeSelectMessages
import com.appAllFriendsNearby.talk.databinding.FragmentDialogBinding
import com.appAllFriendsNearby.talk.di.MyApplication
import com.appAllFriendsNearby.talk.tools.constants.MAIN_MENU_FRAGMENT_REPLACE
import com.appAllFriendsNearby.talk.tools.generalStaticFunction.hideKeyboardFrom
import com.appAllFriendsNearby.talk.tools.generalStaticFunction.setUserPhoto
import com.appAllFriendsNearby.talk.tools.generalStaticFunction.showToast
import com.appAllFriendsNearby.talk.view.OnClickRemoveItem
import com.appAllFriendsNearby.talk.view.OnLongTouchRecyclerViewItemDelete
import com.appAllFriendsNearby.talk.view.activity.MainMenuActivity
import com.appAllFriendsNearby.talk.view.recyclerView.RecyclerViewAllMessagesWithCompanion
import com.appAllFriendsNearby.talk.viewModel.DialogViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.launch
import javax.inject.Inject


class DialogFragment : Fragment(), OnLongTouchRecyclerViewItemDelete, OnClickRemoveItem {

    private lateinit var binding: FragmentDialogBinding
    private lateinit var mainMenuActivity: MainMenuActivity
    @Inject
    lateinit var dialogViewModel: DialogViewModel
    private lateinit var getContent : ActivityResultLauncher<Intent>
    private lateinit var uriSendImage: Uri
    private val listWithRemovedMessages = mutableListOf<UserMessagesWithCompanionDataClass>()
    private var flagEmptyList = false


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        binding = FragmentDialogBinding.inflate(inflater, container, false)
        onBackPressed()
        mainMenuActivity = (requireActivity() as MainMenuActivity)
        (requireActivity().application as MyApplication).appComponent.inject(this@DialogFragment)
        ///////////////InitRecycler
        binding.recyclerViewMessages.layoutManager = LinearLayoutManager(requireContext())
        ///////////////получаем фото из Intent по клику на кнопку sendOrAttach
        getContent = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) {
            if (it.resultCode == Activity.RESULT_OK && it.data != null) {
                binding.cardViewIntentImage.visibility = View.VISIBLE
                uriSendImage = it.data!!.data!!
                binding.intentImage.setImageURI(uriSendImage)
                binding.sendOrAttach.setImageResource(R.drawable.style_button_send)
            }
        }
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
            if (it != null && !flagEmptyList) {
                binding.recyclerViewMessages.adapter = RecyclerViewAllMessagesWithCompanion(it, this, this)
                binding.recyclerViewMessages.scrollToPosition(it.size - 1)
            }
        }
        binding.sendOrAttach.setOnClickListener {
            flagEmptyList = false
            val text = binding.message.text
            if (text.isNotEmpty() && binding.cardViewIntentImage.visibility == View.VISIBLE) {
                binding.cardViewIntentImage.visibility = View.GONE
                dialogViewModel.sendMessage(text.toString())
                dialogViewModel.sendImageToStorageAndDB(uriSendImage)
            } else if (text.isEmpty() && binding.cardViewIntentImage.visibility == View.VISIBLE) {
                binding.cardViewIntentImage.visibility = View.GONE
                dialogViewModel.sendImageToStorageAndDB(uriSendImage)
            } else if (text.isNotEmpty() && binding.cardViewIntentImage.visibility == View.GONE) {
                dialogViewModel.sendMessage(text.toString())
            } else {
                val intent = Intent(Intent.ACTION_PICK)
                intent.type = "image/*"
                getContent.launch(intent)
            }
            binding.message.setText("")
        }
        ///////////////Удаление картинки из Intent
        binding.removeIntentImage.setOnClickListener {
            binding.cardViewIntentImage.visibility = View.GONE
            binding.sendOrAttach.setImageResource(R.drawable.style_button_attach_file)
        }
        ///////////////////Слушатель ввода символов
        binding.message.doOnTextChanged { text, _, _, _ ->
            if (text!!.isEmpty()) {
                binding.sendOrAttach.setImageResource(R.drawable.style_button_attach_file)
            } else {
                binding.sendOrAttach.setImageResource(R.drawable.style_button_send)
            }
        }
        /////////////////////////////Закрытие поля удаления сообщения
        binding.close.setOnClickListener {
            binding.constraintDeleteMessage.visibility = View.GONE
            binding.constraintSendMessage.visibility = View.VISIBLE
            listWithRemovedMessages.clear()
        }
        //////////////Удаляем сообщения
        binding.delete.setOnClickListener {
            CoroutineScope(Dispatchers.Main).launch {
                async { removeSelectMessages(listWithRemovedMessages) }.await()
                //////////////////////Если удаляются все элементы
                if (listWithRemovedMessages.size == binding.recyclerViewMessages.adapter?.itemCount) {
                    binding.recyclerViewMessages.adapter = null
                    flagEmptyList = true
                }
                listWithRemovedMessages.clear()
            }
        }
        binding.mainConstraint.setOnClickListener {
            hideKeyboardFrom(requireContext(), binding.recyclerViewMessages)
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

    /////////////////////Если пользователь удерживает палец на сообщении
    override fun longItemClick(longTouch: Boolean) {
        binding.constraintSendMessage.visibility = View.GONE
        binding.constraintDeleteMessage.visibility = View.VISIBLE
        hideKeyboardFrom(requireContext(), binding.message)
    }

    ////////////////Добавляем сообщение в очередь на удаление
    override fun addItem(messages: UserMessagesWithCompanionDataClass) {
        listWithRemovedMessages.add(messages)
    }

    ////////////////Удаляем из очереди
    override fun removeItem(messages: UserMessagesWithCompanionDataClass) {
        listWithRemovedMessages.remove(messages)

    }


}