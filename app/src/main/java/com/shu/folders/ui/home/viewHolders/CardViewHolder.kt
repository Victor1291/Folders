package com.shu.folders.ui.home.viewHolders

import androidx.viewbinding.ViewBinding
import com.bumptech.glide.Glide
import com.shu.folders.R
import com.shu.folders.databinding.CardItemBinding
import com.shu.folders.ui.home.ViewHolderVisitor
import com.shu.folders.ui.home.model.CardItem
import com.shu.mynews.ui.visitor.adapter.AdapterClickListenerById

class CardViewHolder : ViewHolderVisitor {
    override val layout: Int = R.layout.card_item

    override fun acceptBinding(item: Any): Boolean = item is CardItem

    override fun bind(binding: ViewBinding, item: Any, clickListener: AdapterClickListenerById) {
        with(binding as CardItemBinding) {
            with(item as CardItem) {
                cardTitle.text = item.title
                // txtDiscription.text = item.description
            }
            Glide.with(binding.cardBackgroundImage)
                .load(item.image)
                .thumbnail(0.33f)
                .centerCrop()
                .into(binding.cardBackgroundImage)
        }
    }
}