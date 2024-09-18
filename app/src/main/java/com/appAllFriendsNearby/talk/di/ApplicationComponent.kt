package com.appAllFriendsNearby.talk.di


import android.content.Context
import com.appAllFriendsNearby.talk.view.activity.RegistrationActivity
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
}
@Module (includes = [RegistrationModule::class])
class AppModule ()


