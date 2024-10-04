package com.appAllFriendsNearby.talk.di


import android.content.Context
import android.content.SharedPreferences
import com.appAllFriendsNearby.talk.model.RegistrationModel
import com.appAllFriendsNearby.talk.tools.constants.MAIN
import dagger.Module
import dagger.Provides
import javax.inject.Singleton

@Module
class RegistrationModule () {
    @Singleton
    @Provides
    fun provideSharedPreferences (context: Context) : SharedPreferences {
        return context.getSharedPreferences(MAIN, Context.MODE_PRIVATE)
    }
    @Singleton
    @Provides
    fun provideSharedPreferencesEditor (sharedPreferences: SharedPreferences) : SharedPreferences.Editor {
        return sharedPreferences.edit()
    }
    @Singleton
    @Provides
    fun provideRegistrationModel (sharedPreferences: SharedPreferences) : RegistrationModel {
        return RegistrationModel(sharedPreferences.edit())
    }
}