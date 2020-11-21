package com.abkhrr.dipaygallery.di.builder

import com.abkhrr.dipaygallery.di.providers.FragmentGalleryDetailProviders
import com.abkhrr.dipaygallery.di.providers.FragmentHomeProviders
import com.abkhrr.dipaygallery.presentation.main.MainActivity
import com.abkhrr.dipaygallery.presentation.main.camera.ActivityDipayCamera
import dagger.Module
import dagger.android.ContributesAndroidInjector

@Module
abstract class ActivityBuilder {
    @ContributesAndroidInjector(modules = [FragmentHomeProviders::class, FragmentGalleryDetailProviders::class])
    abstract fun bindMainActivity(): MainActivity

    @ContributesAndroidInjector
    abstract fun bindCameraActivity(): ActivityDipayCamera
}