package com.shu.folders.models

import android.os.Parcelable
import kotlinx.parcelize.Parcelize


@Parcelize
data class Images(
    val path: String,
    val listImages: List<Long>,
): Parcelable
