package com.abkhrr.dipaygallery.data.model.api

import com.google.gson.annotations.SerializedName

data class ApiStringResponse (
    @SerializedName("message")
    val responseMessage: String,

    @SerializedName("status")
    val responseStatus: Int
)