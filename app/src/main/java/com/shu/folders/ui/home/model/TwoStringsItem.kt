package com.shu.folders.ui.home.model

data class TwoStringsItem(
    override val id: String = "two strings",
    val caption: String,
    val details: String
) : HasStringId
