package com.shu.folders.ui.home

import androidx.viewbinding.ViewBinding

interface ViewHolderVisitor {
    val layout: Int
    fun acceptBinding(item: Any): Boolean
    fun bind(binding: ViewBinding, item: Any, clickListener: AdapterClickListenerById, position: Int)
}