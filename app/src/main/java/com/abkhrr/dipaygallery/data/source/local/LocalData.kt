package com.abkhrr.dipaygallery.data.source.local

import com.abkhrr.dipaygallery.data.source.local.database.AppDatabase
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class LocalData @Inject constructor(private val mAppDatabase: AppDatabase) : LocalDataSource {
}