package com.shu.folders.ui.home.viewHolders

import androidx.viewbinding.ViewBinding
import com.shu.folders.R
import com.shu.folders.databinding.RecyclerHeaderItemBinding
import com.shu.folders.ui.home.ViewHolderVisitor
import com.shu.folders.ui.home.model.RecyclerHeader
import com.shu.mynews.ui.visitor.adapter.AdapterClickListenerById

class HeaderViewHolder : ViewHolderVisitor {

    override val layout = R.layout.recycler_header_item

    override fun bind(binding: ViewBinding, item: Any, clickListener: AdapterClickListenerById) {
        (binding as RecyclerHeaderItemBinding).header.text = (item as RecyclerHeader).text
    }

    override fun acceptBinding(item: Any): Boolean = item is RecyclerHeader
}