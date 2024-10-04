package com.appAllFriendsNearby.talk.di


import android.content.Context
import com.appAllFriendsNearby.talk.view.activity.RegistrationActivity
import com.appAllFriendsNearby.talk.view.fragment.DialogFragment
import com.appAllFriendsNearby.talk.view.fragment.MainMenuFragment
import com.appAllFriendsNearby.talk.view.fragment.RegistrationFragment
import com.appAllFriendsNearby.talk.view.fragment.UserDataFragment
import com.appAllFriendsNearby.talk.viewModel.MainMenuViewModel
import dagger.BindsInstance
import dagger.Component
import dagger.Module
import javax.inject.Singleton

@Singleton
@Component (modules = [AppModule::class])
interface ApplicationComponent {

    @Component.Builder
    interface Builder {
        @BindsInstance
        fun setContext (context: Context) : Builder
        fun build (): ApplicationComponent
    }
    fun inject (registrationActivity: RegistrationActivity)
    fun inject (registrationFragment: RegistrationFragment)
    fun inject (userDataFragment: UserDataFragment)
    fun inject (mainMenuFragment: MainMenuFragment)
    fun inject (dialogFragment: DialogFragment)

}
@Module (includes = [RegistrationModule::class])
class AppModule ()


