package com.appAllFriendsNearby.talk.view.activity

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.fragment.app.FragmentTransaction
import com.appAllFriendsNearby.talk.R
import com.appAllFriendsNearby.talk.databinding.ActivityMainMenuBinding
import com.appAllFriendsNearby.talk.tools.constants.MAIN_MENU_FRAGMENT
import com.appAllFriendsNearby.talk.view.fragment.MainMenuFragment

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
        }
    }
}