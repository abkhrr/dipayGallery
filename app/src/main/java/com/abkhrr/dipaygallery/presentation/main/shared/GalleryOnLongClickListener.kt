package com.abkhrr.dipaygallery.presentation.main.shared

import com.abkhrr.dipaygallery.domain.dto.Gallery

interface GalleryOnLongClickListener {
    fun onLongItemClicked(gallery: Gallery, position: Int)
}