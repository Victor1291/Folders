package com.shu.folders.ui.home.viewHolders

import androidx.viewbinding.ViewBinding
import com.shu.folders.R
import com.shu.folders.databinding.OneLineItem2Binding
import com.shu.folders.ui.home.ViewHolderVisitor
import com.shu.folders.ui.home.model.OneLineItem2
import com.shu.folders.ui.home.AdapterClickListenerById


class OneLine2ViewHolder : ViewHolderVisitor {
    override val layout: Int = R.layout.one_line_item_2

    override fun acceptBinding(item: Any): Boolean = item is OneLineItem2

    override fun bind(
        binding: ViewBinding,
        item: Any,
        clickListener: AdapterClickListenerById,
        position: Int
    ) {
        with((binding as OneLineItem2Binding)) {
            with(item as OneLineItem2) {
                text1.text = item.left
                text2.text = item.right
            }
        }

    }
}