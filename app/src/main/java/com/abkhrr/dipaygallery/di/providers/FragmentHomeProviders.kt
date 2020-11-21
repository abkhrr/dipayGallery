package com.abkhrr.dipaygallery.di.providers

import com.abkhrr.dipaygallery.presentation.main.home.FragmentHome
import dagger.Module
import dagger.android.ContributesAndroidInjector

@Module
abstract class FragmentHomeProviders {
    @ContributesAndroidInjector
    abstract fun provideFragmentHomeFactory(): FragmentHome
}