package com.shu.folders.models

import android.os.Parcelable
import kotlinx.parcelize.Parcelize


@Parcelize
data class ViewPagerItem(
    val listImages: List<MediaStoreImage>,
    val position: Int
): Parcelable
