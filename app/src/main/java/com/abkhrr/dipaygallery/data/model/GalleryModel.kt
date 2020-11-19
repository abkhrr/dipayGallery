package com.abkhrr.dipaygallery.data.model.api

import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import kotlinx.android.parcel.Parcelize

class GalleryApi {

    @SerializedName("message")
    val responseMessage: String?            = null

    @SerializedName("message")
    val responseStatus: String?             = null

    @SerializedName("data")
    val responseData: List<GalleryModel>    = listOf()

    @Parcelize
    data class GalleryModel (
        @SerializedName("_id")
        val id: String,

        @SerializedName("name")
        val galleryName: String,

        @SerializedName("mimeTypes")
        val galleryMimeType: String,

        @SerializedName("url")
        val galleryUrl: String,

        @SerializedName("isVideos")
        val isVideos: Boolean
    ): Parcelable

}