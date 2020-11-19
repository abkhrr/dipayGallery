package com.abkhrr.dipaygallery.data.source.remote.network

import com.abkhrr.dipaygallery.data.model.api.ApiCallback
import com.abkhrr.dipaygallery.data.model.api.ApiStringResponse
import com.abkhrr.dipaygallery.data.model.api.GalleryApi
import com.abkhrr.dipaygallery.utils.constant.ApiEndpoint
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Path

interface ApiService {

    companion object{
        fun getApiService(): Class<ApiService> {
            return ApiService::class.java
        }
    }

    @POST(ApiEndpoint.POST_TO_GALLERY)
    suspend fun postGallery() : ApiCallback<ApiStringResponse>

    @GET(ApiEndpoint.GET_ALL_LIST_OF_GALLERY)
    suspend fun getListOfGallery() : ApiCallback<GalleryApi>

    @GET(ApiEndpoint.FIND_GALLERY_BY_ID)
    suspend fun findGalleryById(@Path("id") id: String) : ApiCallback<ApiStringResponse>

    @GET(ApiEndpoint.DELETE_GALLERY)
    suspend fun deleteGalleryById(@Path("id") id: String) : ApiCallback<ApiStringResponse>

}