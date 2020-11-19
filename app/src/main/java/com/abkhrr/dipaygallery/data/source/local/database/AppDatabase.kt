package com.abkhrr.dipaygallery.data.source.local.database

import androidx.room.Database
import androidx.room.RoomDatabase
import com.abkhrr.dipaygallery.data.source.local.dao.GalleryDao
import com.abkhrr.dipaygallery.data.source.local.model.Gallery

@Database(entities = [Gallery::class], version = 3)
abstract class AppDatabase : RoomDatabase() {
    abstract fun cartDao(): GalleryDao
}