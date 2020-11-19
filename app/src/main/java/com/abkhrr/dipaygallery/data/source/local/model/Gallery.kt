package com.abkhrr.dipaygallery.data.source.local.model

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.google.gson.annotations.Expose

@Entity(tableName = "gallery")
data class Gallery (

    @Expose
    @field: PrimaryKey
    @ColumnInfo(name = "id")
    var galleryId: String,

    @field: ColumnInfo(name = "name")
    var name: String,

    @field: ColumnInfo(name = "price")
    var mimeType: Int = 0,

    @field: ColumnInfo(name = "imageUrl")
    var url: String,

    @field: ColumnInfo(name = "isVideos")
    var isVideos: Boolean,
)