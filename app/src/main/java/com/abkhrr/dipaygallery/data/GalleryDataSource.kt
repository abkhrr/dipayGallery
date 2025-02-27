package com.abkhrr.dipaygallery.data

import androidx.lifecycle.LiveData
import com.abkhrr.dipaygallery.domain.dto.Gallery

interface GalleryDataSource {
    fun getAllGallery(): LiveData<List<Gallery>>
    suspend fun insert(gallery: Gallery)
    suspend fun delete(gallery: Gallery)
}