package com.appAllFriendsNearby.talk.view.activity

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.fragment.app.FragmentTransaction
import com.appAllFriendsNearby.talk.R
import com.appAllFriendsNearby.talk.dataBase.setUserConnection
import com.appAllFriendsNearby.talk.dataBase.USER_ID_O
import com.appAllFriendsNearby.talk.databinding.ActivityMainMenuBinding
import com.appAllFriendsNearby.talk.tools.constants.DIALOG_FRAGMENT
import com.appAllFriendsNearby.talk.tools.constants.MAIN_MENU_FRAGMENT
import com.appAllFriendsNearby.talk.tools.constants.MAIN_MENU_FRAGMENT_REPLACE
import com.appAllFriendsNearby.talk.view.fragment.DialogFragment
import com.appAllFriendsNearby.talk.view.fragment.MainMenuFragment
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class MainMenuActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainMenuBinding
    private lateinit var fragmentTransaction: FragmentTransaction

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainMenuBinding.inflate(layoutInflater)
        setContentView(binding.root)
        changeFragment(MAIN_MENU_FRAGMENT)
    }
    fun changeFragment (tag: String) {
        fragmentTransaction = supportFragmentManager.beginTransaction();
        when (tag) {
            MAIN_MENU_FRAGMENT -> {fragmentTransaction.add(R.id.frameLayout, MainMenuFragment ()).commit()}
            DIALOG_FRAGMENT -> {fragmentTransaction.replace(R.id.frameLayout, DialogFragment ()).commit()}
            MAIN_MENU_FRAGMENT_REPLACE -> {fragmentTransaction.replace(R.id.frameLayout, MainMenuFragment()).commit()}
        }
    }

    override fun onStart() {
        super.onStart()
        CoroutineScope(Dispatchers.Main).launch {
            setUserConnection(true)
        }
    }

    override fun onStop() {
        super.onStop()
        CoroutineScope(Dispatchers.Main).launch {
            setUserConnection(false)
        }
    }

}