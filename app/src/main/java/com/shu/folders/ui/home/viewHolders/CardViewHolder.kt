package com.shu.folders.ui.home.viewHolders

import android.util.Log
import androidx.core.view.ViewCompat
import androidx.navigation.fragment.FragmentNavigator
import androidx.navigation.fragment.FragmentNavigatorExtras
import androidx.viewbinding.ViewBinding
import com.bumptech.glide.Glide
import com.shu.folders.R
import com.shu.folders.databinding.CardItemBinding
import com.shu.folders.models.StateClick
import com.shu.folders.ui.home.AdapterClickListenerById
import com.shu.folders.ui.home.ItemTypes.CARD
import com.shu.folders.ui.home.ViewHolderVisitor
import com.shu.folders.ui.home.model.CardItem

class CardViewHolder : ViewHolderVisitor {

    override val layout: Int = R.layout.card_item

    private lateinit var item: CardItem
    private lateinit var extras: FragmentNavigator.Extras

    override fun acceptBinding(item: Any): Boolean = item is CardItem

    override fun bind(
        binding: ViewBinding,
        item: Any,
        clickListener: AdapterClickListenerById,
        position: Int
    ) {
        with(binding as CardItemBinding) {
            with(item as CardItem) {

                Glide.with(binding.cardBackgroundImage)
                    .load(item.image)
                    .thumbnail(0.33f)
                    .centerCrop()
                    .into(binding.cardBackgroundImage)

                ViewCompat.setTransitionName(binding.cardBackgroundImage , "image_${position}")


                extras = FragmentNavigatorExtras(
                    binding.cardBackgroundImage to binding.cardBackgroundImage.transitionName,
                )

                binding.cardView.setOnClickListener {
                    Log.d("click", "click on cardView $position , ${item.image}")
                    clickListener.onClick(
                        StateClick(
                            id = item.id,
                            position = position,
                            itemTypes = CARD,
                            extras = extras
                        )
                    )
                }


            }
        }
    }
}