package com.shu.folders.ui.home.model

import android.net.Uri

data class CardItem(
    override val id: String = "card",
    val image: Uri,
    val title: String,
    val description: String,
) : HasStringId
