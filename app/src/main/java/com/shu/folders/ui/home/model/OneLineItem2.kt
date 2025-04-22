package com.shu.folders.ui.home.model

data class OneLineItem2(
    override val id: String = "one line",
    val left: String,
    val right: String
) : HasStringId