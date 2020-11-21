package com.abkhrr.dipaygallery.factory

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.abkhrr.dipaygallery.data.GalleryDataSource
import com.abkhrr.dipaygallery.presentation.main.MainViewModel
import com.abkhrr.dipaygallery.presentation.main.shared.SharedViewModel
import javax.inject.Inject

class ViewModelFactory @Inject constructor(
    private val galleryDataSource: GalleryDataSource
) : ViewModelProvider.NewInstanceFactory() {
    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return when {
            modelClass.isAssignableFrom(MainViewModel::class.java) -> {
                MainViewModel() as T
            }
            modelClass.isAssignableFrom(SharedViewModel::class.java) -> {
                SharedViewModel(galleryDataSource) as T
            }
            else -> throw IllegalArgumentException("Unknown ViewModel class: " + modelClass.name)
        }
    }
}