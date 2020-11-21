package com.abkhrr.dipaygallery.presentation.main.shared

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.abkhrr.dipaygallery.data.GalleryDataSource
import com.abkhrr.dipaygallery.domain.dto.db.Gallery
import kotlinx.coroutines.launch

class SharedViewModel(private val galleryDataSource: GalleryDataSource) : ViewModel() {

    val galleryLiveData: LiveData<List<Gallery>> = galleryDataSource.getAllGallery()

    fun insertGallery(gallery: Gallery){
        viewModelScope.launch {
            galleryDataSource.insert(gallery)
        }
    }

    fun deleteGalleryItems(gallery: Gallery){
        viewModelScope.launch {
            galleryDataSource.delete(gallery)
        }
    }
}