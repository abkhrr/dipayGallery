package com.abkhrr.dipaygallery.data.database

import androidx.room.Database
import androidx.room.RoomDatabase
import com.abkhrr.dipaygallery.data.dao.GalleryDao
import com.abkhrr.dipaygallery.domain.dto.db.Gallery

@Database(entities = [Gallery::class], version = 3, exportSchema = false)
abstract class AppDatabase : RoomDatabase() {
    abstract fun galleryDao(): GalleryDao
}