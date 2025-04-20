package com.shu.mynews.ui.visitor.adapter


class AdapterClickListenerById(val clickListener: (id: String) -> Unit) {
    fun onClick(id: String) = clickListener(id)
}