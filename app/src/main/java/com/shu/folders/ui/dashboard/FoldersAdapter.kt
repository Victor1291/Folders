package com.shu.folders.ui.dashboard

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.shu.folders.databinding.ItemFolderBinding
import com.shu.folders.models.Folder

class FoldersAdapter(
    private val onClickButton: (String) -> Unit,
) : ListAdapter<Folder, FolderViewHolder>(ItemDiffCallback()) {


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): FolderViewHolder {
        val binding = ItemFolderBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return FolderViewHolder(binding, onClickButton)
    }

    override fun onBindViewHolder(holder: FolderViewHolder, position: Int) {
        val card = getItem(position)
        holder.bind(card)
    }
}

class FolderViewHolder(
    private val binding: ItemFolderBinding,
    private val onClickButton: (String) -> Unit
) : RecyclerView.ViewHolder(binding.root) {

    private lateinit var item: Folder

    init {
        binding.constraintLayout.setOnClickListener {
            onClickButton(item.name)
        }
    }

    fun bind(item: Folder) {
        this.item = item

        bindButtonState(item)
    }

    fun bindButtonState(item: Folder) {

        binding.tvDay.text = item.name

        binding.tvWorkerOne.text = "${item.count}"

        Glide.with(binding.image)
            .load(item.artworkUri)
            .thumbnail(0.33f)
            .centerCrop()
            .into(binding.image)
    }
}

class ItemDiffCallback : DiffUtil.ItemCallback<Folder>() {
    override fun areItemsTheSame(oldItem: Folder, newItem: Folder): Boolean {
        return oldItem.name == newItem.name
    }

    override fun areContentsTheSame(oldItem: Folder, newItem: Folder): Boolean {
        return oldItem == newItem
    }

}