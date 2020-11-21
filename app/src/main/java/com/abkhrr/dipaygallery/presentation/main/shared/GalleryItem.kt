package com.abkhrr.dipaygallery.presentation.main.shared

class GalleryItem(private val action: () -> Unit) {
    fun onItemClick() = action.invoke()
}