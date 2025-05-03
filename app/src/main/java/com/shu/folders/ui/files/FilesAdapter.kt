package com.shu.folders.ui.files

import android.content.ContentUris
import android.provider.MediaStore
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.shu.folders.databinding.ItemFilesBinding

class FilesAdapter(
    private val onClickButton: (Long) -> Unit,
) : ListAdapter<Long, FilesViewHolder>(FilesDiffCallback()) {


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): FilesViewHolder {
        val binding = ItemFilesBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return FilesViewHolder(binding, onClickButton)
    }

    override fun onBindViewHolder(holder: FilesViewHolder, position: Int) {
        val card = getItem(position)
        holder.bind(card)
    }
}

class FilesViewHolder(
    private val binding: ItemFilesBinding,
    private val onClickButton: (Long) -> Unit
) : RecyclerView.ViewHolder(binding.root) {

    private var item: Long = 0L

    init {
        binding.materialCardView.setOnClickListener {
            onClickButton(item)
        }
    }

    fun bind(item: Long) {
        this.item = item
        val contentUri = ContentUris.withAppendedId(
            MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
            item
        )

        Glide.with(binding.image)
            .load(contentUri)
            .thumbnail(0.33f)
            .centerCrop()
            .into(binding.image)
    }

}

class FilesDiffCallback : DiffUtil.ItemCallback<Long>() {
    override fun areItemsTheSame(oldItem: Long, newItem: Long): Boolean {
        return oldItem == newItem
    }

    override fun areContentsTheSame(oldItem: Long, newItem: Long): Boolean {
        return oldItem == newItem
    }

}