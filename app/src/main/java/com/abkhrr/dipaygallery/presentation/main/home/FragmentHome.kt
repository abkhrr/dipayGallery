package com.abkhrr.dipaygallery.presentation.main.home

import android.app.AlertDialog
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.view.View
import android.view.animation.Animation
import android.view.animation.AnimationUtils
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.DefaultItemAnimator
import androidx.recyclerview.widget.GridLayoutManager
import com.abkhrr.dipaygallery.BR
import com.abkhrr.dipaygallery.R
import com.abkhrr.dipaygallery.databinding.FragmentHomeBinding
import com.abkhrr.dipaygallery.domain.dto.db.Gallery
import com.abkhrr.dipaygallery.factory.ViewModelFactory
import com.abkhrr.dipaygallery.presentation.base.BaseFragment
import com.abkhrr.dipaygallery.presentation.base.NavigationCommand
import com.abkhrr.dipaygallery.presentation.main.MainActivity
import com.abkhrr.dipaygallery.presentation.main.adapter.GalleryRecyclerViewAdapter
import com.abkhrr.dipaygallery.presentation.main.camera.ActivityDipayCamera
import com.abkhrr.dipaygallery.presentation.main.shared.GalleryDataItems
import com.abkhrr.dipaygallery.presentation.main.shared.GalleryOnLongClickListener
import com.abkhrr.dipaygallery.presentation.main.shared.SharedViewModel
import javax.inject.Inject


@Suppress("DEPRECATION")
class FragmentHome : BaseFragment<FragmentHomeBinding, SharedViewModel>(),
    HomeClickListener, GalleryOnLongClickListener {

    @Inject
    lateinit var factory: ViewModelFactory

    private lateinit var galleryRecyclerViewAdapter: GalleryRecyclerViewAdapter

    override val bindingVariable: Int
        get() = BR.viewModel

    override val layoutId: Int
        get() = R.layout.fragment_home

    override val viewModel: SharedViewModel
        get() = ViewModelProvider(this,factory).get(SharedViewModel::class.java)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        galleryRecyclerViewAdapter = GalleryRecyclerViewAdapter(arrayListOf(), this)
        galleryRecyclerViewAdapter.setGalleryOnClickListener(this)
    }

    override fun onViewCreated(
        view: View,
        savedInstanceState: Bundle?
    ) {
        super.onViewCreated(view, savedInstanceState)
        setUp()
    }

    override fun onItemClick(item: Gallery) {
        navigate(
            NavigationCommand.To(
                FragmentHomeDirections.toGalleryDetailsFragment(
                    GalleryDataItems(
                        item.galleryId,
                        item.name,
                        item.path,
                        item.isVideos
                    )
                )
            )
        )
    }

    private fun setUp() {
        if (activity != null) {
            (activity as MainActivity).setSupportActionBar(getViewDataBinding().toolbar)
            getViewDataBinding().toolbar.title = getString(R.string.title_home)
            (activity as MainActivity).supportActionBar?.apply {
                setDisplayHomeAsUpEnabled(false)
                setDisplayShowHomeEnabled(false)
            }
        }
        setHasOptionsMenu(true)
        setUpRecyclerView()
        setupFab()
    }

    private fun setUpRecyclerView() {
        getViewDataBinding().favoritesRecyclerView.layoutManager = GridLayoutManager(activity, 3)
        getViewDataBinding().favoritesRecyclerView.itemAnimator  = DefaultItemAnimator()
        getViewDataBinding().favoritesRecyclerView.adapter       = galleryRecyclerViewAdapter
        galleryRecyclerViewAdapter.notifyDataSetChanged()
    }

    private var isFabOpen = false

    private fun setupFab(){
        with(getViewDataBinding()){

            val animOpenFab      = AnimationUtils.loadAnimation(requireContext(), R.anim.fab_open)
            val animCloseFab     = AnimationUtils.loadAnimation(requireContext(), R.anim.fab_close)
            val animFabClockWise = AnimationUtils.loadAnimation(
                requireContext(),
                R.anim.fab_rotate_clock
            )
            val animFabAntiClock = AnimationUtils.loadAnimation(
                requireContext(),
                R.anim.fab_anti_clock
            )

            mainFab.setOnClickListener {
                if (isFabOpen) {
                    closeFabMain(animCloseFab, animFabAntiClock)
                } else {
                    openFabMain(animOpenFab, animFabClockWise)
                }
            }

            fabCamera.setOnClickListener {
                val intent = Intent(requireActivity(), ActivityDipayCamera::class.java)
                requireActivity().startActivity(intent)
                closeFabMain(animCloseFab, animFabAntiClock)
            }

            fabGalleryPick.setOnClickListener {
                val pickIntent = Intent(
                    Intent.ACTION_PICK,
                    MediaStore.Images.Media.EXTERNAL_CONTENT_URI
                )
                pickIntent.type = "image/* video/*"
                startActivityForResult(pickIntent, IMAGE_PICKER_SELECT)
                closeFabMain(animCloseFab, animFabAntiClock)
            }
        }
    }

    private fun closeFabMain(animCloseFab: Animation, animFabAntiClock: Animation){
        with(getViewDataBinding()){
            fabGalleryPick.startAnimation(animCloseFab)
            fabCamera.startAnimation(animCloseFab)
            mainFab.startAnimation(animFabAntiClock)
            fabGalleryPick.isClickable = false
            fabCamera.isClickable      = false
            isFabOpen = false
        }
    }

    private fun openFabMain(animOpenFab: Animation, animFabClockWise: Animation){
        with(getViewDataBinding()){
            fabGalleryPick.startAnimation(animOpenFab)
            fabCamera.startAnimation(animOpenFab)
            mainFab.startAnimation(animFabClockWise)
            fabGalleryPick.isClickable = true
            fabCamera.isClickable      = true
            isFabOpen = true
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        val selectedMediaUri: Uri? = data?.data
        if (requestCode == IMAGE_PICKER_SELECT) {
            if (selectedMediaUri.toString().contains("image")) {
                val myGallery = addNewGallery(
                    galleryName = selectedMediaUri?.lastPathSegment.toString(),
                    boolean = false,
                    galleryPath = selectedMediaUri.toString()
                )
                viewModel.insertGallery(myGallery)
            } else if (selectedMediaUri.toString().contains("video")) {
                val myGallery = addNewGallery(
                    galleryName = selectedMediaUri?.lastPathSegment.toString(),
                    boolean = true,
                    galleryPath = selectedMediaUri.toString()
                )
                viewModel.insertGallery(myGallery)
            }
        }
    }

    private fun addNewGallery(galleryName: String, boolean: Boolean, galleryPath: String) : Gallery {
        return Gallery(galleryId = 0, name = galleryName, isVideos = boolean, path = galleryPath)
    }

    companion object {
        private const val IMAGE_PICKER_SELECT = 1
    }

    override fun onLongItemClicked(gallery: Gallery, position: Int) {
        AlertDialog.Builder(context)
            .setTitle("Delete Items")
            .setMessage("Are you sure you want to delete this item?")
            .setPositiveButton(
                android.R.string.yes
            ) { _, _ ->
                viewModel.deleteGalleryItems(gallery)
                galleryRecyclerViewAdapter.deleteItemsInGallery(position)
            }
            .setNegativeButton(android.R.string.no, null)
            .setIcon(android.R.drawable.ic_dialog_alert)
            .show()
    }

}