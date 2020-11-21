package com.abkhrr.dipaygallery.presentation.main.galleryDetail

import android.os.Bundle
import android.view.View
import androidx.lifecycle.ViewModelProvider
import com.abkhrr.dipaygallery.BR
import com.abkhrr.dipaygallery.R
import com.abkhrr.dipaygallery.databinding.FragmentGalleryDetailBinding
import com.abkhrr.dipaygallery.factory.ViewModelFactory
import com.abkhrr.dipaygallery.presentation.base.BaseFragment
import com.abkhrr.dipaygallery.presentation.main.MainActivity
import com.abkhrr.dipaygallery.presentation.main.shared.GalleryDataItems
import com.abkhrr.dipaygallery.presentation.main.shared.SharedViewModel
import com.abkhrr.dipaygallery.utils.AppConstants
import com.bumptech.glide.Glide
import com.norulab.exofullscreen.MediaPlayer
import com.norulab.exofullscreen.preparePlayer
import com.norulab.exofullscreen.setSource
import javax.inject.Inject


class FragmentGalleryDetail : BaseFragment<FragmentGalleryDetailBinding, SharedViewModel>() {

    @Inject
    lateinit var factory: ViewModelFactory

    override val bindingVariable: Int
        get() = BR.viewModel

    override val layoutId: Int
        get() = R.layout.fragment_gallery_detail

    private var galleryDataItem: GalleryDataItems? = null

    override val viewModel: SharedViewModel
        get() = ViewModelProvider(this,factory).get(SharedViewModel::class.java)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        if (arguments != null) {
            galleryDataItem = arguments?.getParcelable(AppConstants.GALLERY)
        }
    }

    override fun onViewCreated(
        view: View,
        savedInstanceState: Bundle?
    ) {
        super.onViewCreated(view, savedInstanceState)
        setUp()
    }

    private fun setUp() {
        setUpContent()
    }

    private fun setUpContent(){
        with(getViewDataBinding()){
            if (galleryDataItem?.isVideos == false){
                coverImageView.visibility = View.VISIBLE
                coverVideoView.visibility = View.INVISIBLE
                toolbar.visibility        = View.VISIBLE
                setUpToolbar()

                Glide.with(root.context)
                    .load(galleryDataItem?.path)
                    .into(coverImageView)
            }
            else {
                coverImageView.visibility = View.INVISIBLE
                coverVideoView.visibility = View.VISIBLE
                toolbar.visibility        = View.GONE

                MediaPlayer.initialize(requireContext())
                MediaPlayer.exoPlayer?.preparePlayer(coverVideoView, true)
                galleryDataItem?.path?.let {
                    MediaPlayer.exoPlayer?.setSource(
                        requireContext(),
                        it
                    )
                }
                MediaPlayer.startPlayer()
            }
        }
    }

    override fun onPause() {
        super.onPause()
        MediaPlayer.pausePlayer()
    }

    override fun onDestroy() {
        MediaPlayer.stopPlayer()
        super.onDestroy()
    }

    private fun setUpToolbar() {
        if (activity != null) {
            (activity as MainActivity).setSupportActionBar(getViewDataBinding().toolbar)
            getViewDataBinding().toolbar.title = galleryDataItem?.name
            val actionBar = (activity as MainActivity).supportActionBar
            actionBar?.setDisplayHomeAsUpEnabled(true)
            actionBar?.setDisplayShowHomeEnabled(true)
            actionBar?.setDisplayShowTitleEnabled(true)
        }
        getViewDataBinding().toolbar.setNavigationOnClickListener {
            if (activity != null) {
                activity?.onBackPressed()
            }
        }
    }

}