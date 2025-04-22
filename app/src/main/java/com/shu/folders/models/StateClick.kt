package com.shu.folders.models

import android.icu.text.Transliterator.Position
import com.shu.folders.ui.home.ItemTypes.CARD

data class StateClick(
    var id : String = "card",
    var position: Int = 0,
    var itemTypes : Int = CARD,
)
