/*
 * Copyright (C) 2019 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.shu.folders.models

import android.content.ContentUris
import android.net.Uri
import android.os.Parcelable
import android.provider.MediaStore
import androidx.recyclerview.widget.DiffUtil
import com.shu.folders.ui.home.Item
import kotlinx.parcelize.Parcelize

/**
 * Simple data class to hold information about an image included in the device's MediaStore.
 */
@Parcelize
data class MediaStoreImage(
    val id: Long,
    val displayName: String,
    val mimeType: String,
    val dateAdded: Long,
    val contentUri: Uri,
    val dateModified: Long,
    val size: Long,
    val dateTaken: Long,
    val orientation: Int,
    val height: Int,
    val width: Int,
    val duration: Int,
): Parcelable {
    /*companion object {
        val DiffCallback = object : DiffUtil.ItemCallback<Item>() {
            override fun areItemsTheSame(oldItem: Item, newItem: Item) =
                if (oldItem is Item.HeaderItem && newItem is Item.HeaderItem)
                    oldItem.title == newItem.title
                else if (oldItem is Item.MediaStoreImageItem && newItem is Item.MediaStoreImageItem)
                    oldItem.mediaStoreImage.id == newItem.mediaStoreImage.id
                else false

            override fun areContentsTheSame(oldItem: Item, newItem: Item): Boolean =
                if (oldItem is Item.HeaderItem && newItem is Item.HeaderItem)
                    oldItem == newItem
                else if (oldItem is Item.MediaStoreImageItem && newItem is Item.MediaStoreImageItem)
                    oldItem == newItem
                else false
        }
    }*/

    override fun toString(): String {
        return "  id = $id, displayName = $displayName, mimeType = $mimeType, dateModified = $dateModified, dateAdded = $dateAdded, contentUri = $contentUri, size = $size, dateTaken = $dateTaken, orientation = $orientation, height = $height, width = $width, duration = $duration"
    }

}


val MediaStoreImage.isImage get() = mimeType.startsWith("image/")
val MediaStoreImage.mediaUri
    get() = if (isImage)
        ContentUris.withAppendedId(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, id)
    else
        ContentUris.withAppendedId(MediaStore.Video.Media.EXTERNAL_CONTENT_URI, id)