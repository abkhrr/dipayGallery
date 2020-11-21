package com.abkhrr.dipaygallery.domain.repository

import androidx.lifecycle.LiveData
import com.abkhrr.dipaygallery.data.GalleryDataSource
import com.abkhrr.dipaygallery.data.database.AppDatabase
import com.abkhrr.dipaygallery.domain.dto.GalleryResult
import com.abkhrr.dipaygallery.domain.dto.db.Gallery
import javax.inject.Inject

class GalleryRepository @Inject constructor(private val mAppDatabase: AppDatabase) : GalleryDataSource {

    override fun getAllGallery(): LiveData<List<Gallery>> = mAppDatabase.galleryDao().getAllGallery()

    override suspend fun insert(gallery: Gallery) = mAppDatabase.galleryDao().insert(gallery)

    override suspend fun delete(gallery: Gallery) = mAppDatabase.galleryDao().delete(gallery)

    override suspend fun getGalleryById(id: Int): GalleryResult<Gallery> {
        return try {
            GalleryResult.Success(mAppDatabase.galleryDao().getGalleryById(id))
        } catch (e: Exception) {
            GalleryResult.Error(e.localizedMessage)
        }
    }

}