package com.shu.folders.ui.home.model

import com.shu.folders.ui.home.model.HasStringId

data class RecyclerHeader(
    override val id: String = "header",
    val text: String
) : HasStringId
