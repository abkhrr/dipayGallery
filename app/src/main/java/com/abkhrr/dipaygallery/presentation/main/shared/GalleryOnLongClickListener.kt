package com.abkhrr.dipaygallery.presentation.main.shared

import android.view.View
import com.abkhrr.dipaygallery.domain.dto.db.Gallery

interface GalleryOnLongClickListener {
    fun onLongItemClicked(gallery: Gallery, position: Int)
}