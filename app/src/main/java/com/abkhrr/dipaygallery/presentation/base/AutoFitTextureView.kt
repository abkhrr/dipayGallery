package com.abkhrr.dipaygallery.presentation.base

import android.content.Context
import android.util.AttributeSet
import android.view.TextureView


class AutoFitTextureView(context: Context?, attrs: AttributeSet?, defStyle: Int) :
    TextureView(context!!, attrs, defStyle) {
    private var mRatioWidth = 0
    private var mRatioHeight = 0

    constructor(context: Context?) : this(context, null)
    constructor(context: Context?, attrs: AttributeSet?) : this(context, attrs, 0)

    fun setAspectRatio(width: Int, height: Int) {
        require(!(width < 0 || height < 0)) { "Size cannot be negative." }
        mRatioWidth = width
        mRatioHeight = height
        requestLayout()
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec)
        val width = MeasureSpec.getSize(widthMeasureSpec)
        val height = MeasureSpec.getSize(heightMeasureSpec)
        if (0 == mRatioWidth || 0 == mRatioHeight) {
            setMeasuredDimension(width, height)
        } else {
            if (width < height * mRatioWidth / mRatioHeight) {
                setMeasuredDimension(height * mRatioWidth / mRatioHeight, height)
            } else {
                setMeasuredDimension(width, width * mRatioHeight / mRatioWidth)
            }
        }
    }
}