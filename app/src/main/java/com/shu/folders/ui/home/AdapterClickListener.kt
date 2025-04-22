package com.shu.folders.ui.home

import com.shu.folders.models.StateClick


class AdapterClickListenerById(val clickListener: (stateClick: StateClick) -> Unit) {
    fun onClick(stateClick: StateClick) = clickListener(stateClick)
}