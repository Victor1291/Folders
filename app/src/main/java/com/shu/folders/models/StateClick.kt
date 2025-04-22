package com.shu.folders.models

import androidx.navigation.fragment.FragmentNavigator
import com.shu.folders.ui.home.ItemTypes.CARD

data class StateClick(
    var id: String = "card",
    var position: Int = 0,
    var itemTypes: Int = CARD,
    var extras: FragmentNavigator.Extras? = null ,
)
