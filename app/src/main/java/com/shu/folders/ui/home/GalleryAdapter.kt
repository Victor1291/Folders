package com.shu.folders.ui.home

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import androidx.viewbinding.ViewBinding
import com.shu.folders.R
import com.shu.folders.databinding.CardItemBinding
import com.shu.folders.databinding.OneLineItem2Binding
import com.shu.folders.databinding.RecyclerHeaderItemBinding
import com.shu.folders.databinding.TwoLineItemBinding
import com.shu.folders.ui.home.model.HasStringId

class GalleryAdapter(
    private val viewHoldersManager: ViewHoldersManager,
    private val clickListener: AdapterClickListenerById,
    // private val onClick: (MediaStoreImage) -> Unit
) : ListAdapter<HasStringId, GalleryAdapter.DataViewHolder>(BaseDiffCallback()) {

    inner class DataViewHolder(
        private val binding: ViewBinding,
        private val holder: ViewHolderVisitor,
    ) : RecyclerView.ViewHolder(binding.root) {
        fun bind(item: HasStringId, clickListener: AdapterClickListenerById, position: Int) =
            holder.bind(binding, item, clickListener, position)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): DataViewHolder {
        val layoutInflater = LayoutInflater.from(parent.context)
        val holder = viewHoldersManager.getViewHolder(viewType)
        val view = when (holder.layout) {

            R.layout.card_item -> {
                CardItemBinding.inflate(layoutInflater, parent, false)
            }

            R.layout.recycler_header_item -> {
                RecyclerHeaderItemBinding.inflate(layoutInflater, parent, false)
            }

            R.layout.one_line_item_2 -> {
                OneLineItem2Binding.inflate(layoutInflater, parent, false)

            }

            R.layout.two_line_item -> {
                TwoLineItemBinding.inflate(layoutInflater, parent, false)
            }

            else -> {
                throw IllegalArgumentException("Wrong type")
            }
        }

        layoutInflater.inflate(R.layout.gallery_layout, parent, false)
        return DataViewHolder(view, holder)
    }

    override fun onBindViewHolder(holder: DataViewHolder, position: Int) =
        holder.bind(getItem(position), clickListener, position)

    override fun getItemViewType(position: Int): Int =
        viewHoldersManager.getItemType(getItem(position))

    /* override fun onBindViewHolder(holder: ImageViewHolder, position: Int) {
         val mediaStoreImage = getItem(position)
         holder.rootView.tag = mediaStoreImage
         when (holder.layout) {
             is Item.HeaderItem -> {

             }
             is Item.MediaStoreImageItem -> {
                 Glide.with(holder.imageView)
                     .load(mediaStoreImage.mediaStoreImage.contentUri)
                     .thumbnail(0.33f)
                     .centerCrop()
                     .into(holder.imageView)
             }
         }

     }*/
}

class BaseDiffCallback : DiffUtil.ItemCallback<HasStringId>() {
    override fun areItemsTheSame(oldItem: HasStringId, newItem: HasStringId): Boolean =
        oldItem.id == newItem.id

    override fun areContentsTheSame(oldItem: HasStringId, newItem: HasStringId): Boolean =
        oldItem == newItem
}

/**
 * Basic [RecyclerView.ViewHolder] for our gallery.
 *//*
class ImageViewHolder(view: View, onClick: (MediaStoreImage) -> Unit) :
    RecyclerView.ViewHolder(view) {
    private val rootView = view
    private val imageView: ImageView = view.findViewById(R.id.image)

    init {
        imageView.setOnClickListener {
            val image = rootView.tag as? MediaStoreImage ?: return@setOnClickListener
            onClick(image)
        }
    }
}*/
