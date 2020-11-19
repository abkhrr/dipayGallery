package com.abkhrr.dipaygallery.data.source.local

import androidx.lifecycle.LiveData
import com.abkhrr.dipaygallery.data.source.local.dao.GalleryDao
import com.abkhrr.dipaygallery.data.source.local.model.Gallery

interface LocalDataSource {
    fun getAllStudent(): LiveData<List<Gallery>>
    fun insertStudent(gallery: Gallery): Observable<Long>
    fun deleteStudent(gallery: Gallery): Observable<Boolean>
}