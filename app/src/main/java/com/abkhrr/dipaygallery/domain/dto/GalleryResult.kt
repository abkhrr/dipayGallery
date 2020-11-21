package com.abkhrr.dipaygallery.domain.dto

/**
 * A sealed class that encapsulates successful outcome with a value of type [T]
 * or a failure with message and statusCode
 */
sealed class GalleryResult<out T : Any> {
    data class Success<out T : Any>(val data: T) : GalleryResult<T>()
    data class Error(val message: String?, val statusCode: Int? = null) : GalleryResult<Nothing>()
}