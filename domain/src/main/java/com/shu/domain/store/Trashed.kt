/*
 * Copyright 2024 Zakir Sheikh
 *
 * Created by Zakir Sheikh on 20-07-2024.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *   https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.shu.domain.store

/**
 * Represents a trashed file.
 *
 * @property id The unique identifier of the trashed file.
 * @property name The name of the trashed file.
 * @property expires The timestamp (in milliseconds) when the trashed file will be permanently deleted.
 * @property path The absolute path to the trashed file.
 * @property size The size of the trashed file in bytes.
 * @property mimeType The MIME type of the trashed file (e.g., "image/jpeg", "video/mp4").
 */
data class Trashed(
    val id: Long,
    val name: String,
    val expires: Long,
    val path: String,
    val size: Long,
    val mimeType: String,
    val duration: Int,
)

val Trashed.isImage get() = mimeType.startsWith("image/")
val Trashed.mediaUri get() = MediaProvider.contentUri(id)