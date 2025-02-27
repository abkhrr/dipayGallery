package com.abkhrr.dipaygallery.presentation.main.home

import com.abkhrr.dipaygallery.domain.dto.Gallery
import com.abkhrr.dipaygallery.presentation.base.BaseItemListener

interface HomeClickListener : BaseItemListener<Gallery> {
    override fun onRetryClick() {}
}