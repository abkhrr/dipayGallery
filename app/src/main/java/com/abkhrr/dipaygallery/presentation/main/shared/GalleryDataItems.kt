package com.abkhrr.dipaygallery.presentation.main.shared

import android.os.Parcel
import android.os.Parcelable

class GalleryDataItems (
    val id: Int,
    val name: String?,
    val path: String?,
    val isVideos: Boolean?
) : Parcelable {

    constructor(parcel: Parcel) : this(
        parcel.readInt(),
        parcel.readString(),
        parcel.readString(),
        parcel.readValue(Boolean::class.java.classLoader) as? Boolean
    )

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeInt(id)
        parcel.writeString(name)
        parcel.writeString(path)
        parcel.writeValue(isVideos)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<GalleryDataItems> {
        override fun createFromParcel(parcel: Parcel): GalleryDataItems {
            return GalleryDataItems(parcel)
        }

        override fun newArray(size: Int): Array<GalleryDataItems?> {
            return arrayOfNulls(size)
        }
    }
}