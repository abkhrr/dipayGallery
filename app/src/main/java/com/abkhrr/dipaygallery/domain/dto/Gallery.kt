package com.abkhrr.dipaygallery.domain.dto

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.google.gson.annotations.Expose

@Entity(tableName = "gallery")
data class Gallery(

        @PrimaryKey(autoGenerate = true)
        @ColumnInfo(name = "id")
        val galleryId: Int = 0,

        @field: ColumnInfo(name = "name")
        val name: String,

        @field: ColumnInfo(name = "imagePath")
        val path: String,

        @field: ColumnInfo(name = "isVideos")
        val isVideos: Boolean
)