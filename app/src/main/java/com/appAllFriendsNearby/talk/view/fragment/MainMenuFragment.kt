package com.appAllFriendsNearby.talk.view.fragment

import android.content.Intent
import android.content.SharedPreferences
import android.os.Build
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.SearchView
import androidx.activity.OnBackPressedCallback
import androidx.annotation.RequiresApi
import androidx.fragment.app.Fragment
import com.appAllFriendsNearby.talk.dataBase.setUserConnection
import com.appAllFriendsNearby.talk.databinding.FragmentMainMenuBinding
import com.appAllFriendsNearby.talk.di.MyApplication
import com.appAllFriendsNearby.talk.tools.constants.COMPANION_ID
import com.appAllFriendsNearby.talk.tools.constants.DIALOG_FRAGMENT
import com.appAllFriendsNearby.talk.tools.generalStaticFunction.setUserPhoto
import com.appAllFriendsNearby.talk.view.OnClickRecyclerViewItemUsersOrDialogs
import com.appAllFriendsNearby.talk.view.activity.MainMenuActivity
import com.appAllFriendsNearby.talk.view.activity.RegistrationActivity
import com.appAllFriendsNearby.talk.view.recyclerView.RecyclerViewAllUserDialogs
import com.appAllFriendsNearby.talk.view.recyclerView.RecyclerViewListAllUsers
import com.appAllFriendsNearby.talk.viewModel.MainMenuViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import javax.inject.Inject

class MainMenuFragment : Fragment(), OnClickRecyclerViewItemUsersOrDialogs {

    private lateinit var binding: FragmentMainMenuBinding
    @Inject
    lateinit var mainMenuViewModel: MainMenuViewModel
    @Inject
    lateinit var sharedPreferencesEditor: SharedPreferences.Editor
    private lateinit var mainMenuActivity: MainMenuActivity


    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        binding = FragmentMainMenuBinding.inflate(inflater, container, false)
        onBackPressed()
        (requireActivity().application as MyApplication).appComponent.inject(this)
        mainMenuActivity = (requireActivity() as MainMenuActivity)
        /////////////////Данные по текущему пользователю
        mainMenuViewModel.getCurrentUserData()
        /////////////////Получаем список пользователей
        mainMenuViewModel.getAllUsers()
        //////////////////Получаем все диалоги пользователя
        mainMenuViewModel.getAllUserDialogs()


        /////////////////Слушатель все пользователи
        mainMenuViewModel.listAllUsers.observe(requireActivity()) {
            if (it.size != 0) {
                binding.recyclerViewAllUsers.adapter = RecyclerViewListAllUsers(it, this)
            }
        }
        /////////////////Слушатель текущий пользователь
        mainMenuViewModel.currentUser.observe(requireActivity()) {
            CoroutineScope(Dispatchers.Main).launch {
                setUserPhoto(it.userPhoto, binding.progressBar, binding.currentUserPhoto)
            }
        }
        /////////////////Слушатель поиска пользователей
        mainMenuViewModel.listSearchView.observe(requireActivity()) {
            binding.recyclerViewAllUsers.adapter = RecyclerViewListAllUsers(it, this)
        }
        ///////////////Слушатель все диалоги
        mainMenuViewModel.userDialogs.observe(requireActivity()) {
            if (it.size != 0) {
                binding.allUserDialogs.adapter = RecyclerViewAllUserDialogs(it, this)
            }
        }
        binding.searcher.setOnQueryTextFocusChangeListener { _, hasFocus ->
            if (hasFocus) {
                binding.cardView.visibility = View.GONE
            }else {
                binding.cardView.visibility = View.VISIBLE
            }
        }


        //////////////////////Функция поиска пользователей
        binding.searcher.setOnQueryTextListener(object: SearchView.OnQueryTextListener,
            androidx.appcompat.widget.SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean {
                query?.let {mainMenuViewModel.getUserByRequestSearchView(query.trim())}
                return true
            }

            override fun onQueryTextChange(newText: String?): Boolean {
                newText?.let {mainMenuViewModel.getUserByRequestSearchView(newText.trim())}
                return true
            }

        })

        /////////////////////Забываем пользователя на устройстве
        binding.buttonExit.setOnClickListener {
            removeUserDataLocalAddRegistrationFragment()
        }

        return binding.root
    }

    private fun removeUserDataLocalAddRegistrationFragment () {
        sharedPreferencesEditor.clear().commit()
        startActivity(Intent(requireActivity(), RegistrationActivity::class.java))
        CoroutineScope(Dispatchers.Main).launch {
            setUserConnection(false)
        }
        requireActivity().finish()
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

    override fun itemClick(userIdCompanion: String) {
        sharedPreferencesEditor.putString(COMPANION_ID, userIdCompanion).commit()
        mainMenuActivity.changeFragment(DIALOG_FRAGMENT)
    }


}