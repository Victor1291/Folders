package com.shu.folders.ui.home.viewHolders

import android.util.Log
import androidx.viewbinding.ViewBinding
import com.shu.folders.R
import com.shu.folders.databinding.RecyclerHeaderItemBinding
import com.shu.folders.models.StateClick
import com.shu.folders.ui.home.ViewHolderVisitor
import com.shu.folders.ui.home.model.RecyclerHeader
import com.shu.folders.ui.home.AdapterClickListenerById
import com.shu.folders.ui.home.ItemTypes.HEADER

class HeaderViewHolder : ViewHolderVisitor {

    override val layout = R.layout.recycler_header_item

    override fun bind(binding: ViewBinding, item: Any, clickListener: AdapterClickListenerById, position: Int) {
        with(item as RecyclerHeader) {
            (binding as RecyclerHeaderItemBinding).header.text = item.text
            binding.checkBox.setOnClickListener {
                Log.d("click", "click on checkBox $position , ${item.id}")
                clickListener.onClick(
                    StateClick(
                        id = item.id,
                        position = position,
                        itemTypes = HEADER
                    )
                )
            }
        }

    }

    override fun acceptBinding(item: Any): Boolean = item is RecyclerHeader
}