package com.abkhrr.dipaygallery.di.module

import android.app.Application
import android.content.Context
import androidx.room.Room
import com.abkhrr.dipaygallery.data.GalleryDataSource
import com.abkhrr.dipaygallery.data.database.AppDatabase
import com.abkhrr.dipaygallery.domain.repository.GalleryRepository
import com.abkhrr.dipaygallery.utils.AppConstants
import dagger.Module
import dagger.Provides
import javax.inject.Singleton

@Module
class AppModule {

    @Provides
    @Singleton
    fun provideContext(application: Application): Context {
        return application
    }

    @Provides
    @Singleton
    fun provideAppDatabase(context: Context): AppDatabase {
        return Room.databaseBuilder(context, AppDatabase::class.java, AppConstants.DB_NAME)
            .createFromAsset("database/sqlite.db").build()
    }

    @Provides
    @Singleton
    fun provideGalleryDataSource(GalleryRepository: GalleryRepository): GalleryDataSource {
        return GalleryRepository
    }

}