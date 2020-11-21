package com.abkhrr.dipaygallery.di.component

import android.app.Application
import com.abkhrr.dipaygallery.di.app.CoreApplication
import com.abkhrr.dipaygallery.di.builder.ActivityBuilder
import com.abkhrr.dipaygallery.di.module.AppModule
import dagger.BindsInstance
import dagger.Component
import dagger.android.AndroidInjectionModule
import javax.inject.Singleton

@Singleton
@Component(modules = [AndroidInjectionModule::class, AppModule::class, ActivityBuilder::class])
interface AppComponent {

    fun inject(app: CoreApplication)

    @Component.Builder
    interface Builder {
        @BindsInstance
        fun application(application: Application): Builder

        fun build(): AppComponent
    }
}