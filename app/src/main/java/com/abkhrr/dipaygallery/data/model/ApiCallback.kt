package com.abkhrr.dipaygallery.data.model.api

sealed class ApiCallback<out T : Any> {
    data class Success<out T : Any>(val data: T) : ApiCallback<T>()
    data class Error(val message: String?, val statusCode: Int? = null) : ApiCallback<Nothing>()
}