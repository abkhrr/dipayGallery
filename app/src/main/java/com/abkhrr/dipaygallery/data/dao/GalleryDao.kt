package com.abkhrr.dipaygallery.data.dao

import androidx.lifecycle.LiveData
import androidx.room.*
import com.abkhrr.dipaygallery.domain.dto.db.Gallery

@Dao
interface GalleryDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(gallery: Gallery)

    @Delete
    suspend fun delete(gallery: Gallery)

    @Query("SELECT * FROM gallery WHERE id = :id")
    suspend fun getGalleryById(id: Int): Gallery

    @Query("SELECT * FROM gallery")
    fun getAllGallery(): LiveData<List<Gallery>>
}