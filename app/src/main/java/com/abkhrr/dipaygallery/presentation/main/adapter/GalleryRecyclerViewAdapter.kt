package com.abkhrr.dipaygallery.presentation.main.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.abkhrr.dipaygallery.databinding.GalleryHomeListBinding
import com.abkhrr.dipaygallery.databinding.ItemEmptyViewBinding
import com.abkhrr.dipaygallery.domain.dto.db.Gallery
import com.abkhrr.dipaygallery.presentation.base.BaseRecyclerViewAdapter
import com.abkhrr.dipaygallery.presentation.base.BaseViewHolder
import com.abkhrr.dipaygallery.presentation.main.home.HomeClickListener
import com.abkhrr.dipaygallery.presentation.main.shared.GalleryItem
import com.abkhrr.dipaygallery.presentation.main.shared.GalleryOnLongClickListener
import com.abkhrr.dipaygallery.utils.AppConstants.VIEW_TYPE_EMPTY
import com.abkhrr.dipaygallery.utils.AppConstants.VIEW_TYPE_NORMAL

class GalleryRecyclerViewAdapter(items: MutableList<Gallery>, listener: HomeClickListener)
    : BaseRecyclerViewAdapter<Gallery>(items, listener) {

    private var galleryOnLongClickListener: GalleryOnLongClickListener? = null

    fun setGalleryOnClickListener(listener: GalleryOnLongClickListener){
        galleryOnLongClickListener = listener
    }

    override fun getItemCount(): Int {
        return if (items.size > 0) items.size else 1
    }

    override fun getItemViewType(position: Int): Int {
        return if (items.isNotEmpty()) VIEW_TYPE_NORMAL else VIEW_TYPE_EMPTY
    }

    fun deleteItemsInGallery(position: Int){
        items.removeAt(position)
        this.notifyItemRemoved(position)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BaseViewHolder {
        return when (viewType) {
            VIEW_TYPE_NORMAL -> {
                GalleryViewHolder(
                    GalleryHomeListBinding.inflate(
                        LayoutInflater.from(parent.context),
                        parent, false
                    )
                )
            }
            else -> {
                EmptyViewHolder(
                    ItemEmptyViewBinding.inflate(
                        LayoutInflater.from(parent.context),
                        parent, false
                    )
                )
            }
        }
    }

    inner class GalleryViewHolder(private val mBinding: GalleryHomeListBinding) :
        BaseViewHolder(mBinding.root), View.OnLongClickListener {
        override fun onBind(position: Int) {
            val gallery = items[position]
            mBinding.gallery = gallery
            mBinding.listCoverGallery.setOnLongClickListener(this)
            mBinding.item = GalleryItem { itemListener.onItemClick(gallery) }
            checkIfGalleryVideoOrImage(gallery)
            mBinding.executePendingBindings()
        }

        override fun onLongClick(v: View?): Boolean {
            galleryOnLongClickListener?.onLongItemClicked(items[adapterPosition], adapterPosition)
            return true
        }

        private fun checkIfGalleryVideoOrImage(gallery: Gallery){
            if (gallery.isVideos){
                mBinding.actionPlay.visibility = View.VISIBLE
            }
            else{
                mBinding.actionPlay.visibility = View.INVISIBLE
            }
        }

    }

    inner class EmptyViewHolder(private val mBinding: ItemEmptyViewBinding) :
        BaseViewHolder(mBinding.root) {
        override fun onBind(position: Int) {
            mBinding.executePendingBindings()
        }
    }

}