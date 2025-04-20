package com.shu.folders.ui.home.model

import com.shu.folders.ui.home.model.HasStringId

data class OneLineItem2(
    override val id: String = "one line",
    val left: String,
    val right: String
) : HasStringId