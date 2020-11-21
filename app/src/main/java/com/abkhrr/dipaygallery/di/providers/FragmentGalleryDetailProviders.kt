package com.abkhrr.dipaygallery.di.providers

import com.abkhrr.dipaygallery.presentation.main.galleryDetail.FragmentGalleryDetail
import dagger.Module
import dagger.android.ContributesAndroidInjector

@Module
abstract class FragmentGalleryDetailProviders {
    @ContributesAndroidInjector
    abstract fun provideFragmentGalleryDetailFactory(): FragmentGalleryDetail
}