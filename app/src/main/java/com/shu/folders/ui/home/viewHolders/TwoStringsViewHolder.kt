package com.shu.folders.ui.home.viewHolders

import androidx.viewbinding.ViewBinding
import com.shu.folders.R
import com.shu.folders.databinding.TwoLineItemBinding
import com.shu.folders.ui.home.ViewHolderVisitor
import com.shu.folders.ui.home.model.TwoStringsItem
import com.shu.mynews.ui.visitor.adapter.AdapterClickListenerById

class TwoStringsViewHolder : ViewHolderVisitor {

    override val layout: Int = R.layout.two_line_item

    override fun acceptBinding(item: Any): Boolean = item is TwoStringsItem

    override fun bind(binding: ViewBinding, item: Any, clickListener: AdapterClickListenerById) {
        with((binding as TwoLineItemBinding)) {
            with((item as TwoStringsItem)) {
                text1.text = item.caption
                text2.text = item.details
            }
        }

    }
}