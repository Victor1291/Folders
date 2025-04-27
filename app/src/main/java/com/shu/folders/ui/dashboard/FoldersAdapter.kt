package com.shu.folders.ui.dashboard

import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.navigation.fragment.FragmentNavigator
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.shu.folders.databinding.ItemFolderBinding
import com.shu.folders.models.Folder

class FoldersAdapter(
    private val onClickButton: (String) -> Unit,
) : ListAdapter<Folder, PhotoViewHolder>(ItemDiffCallback()) {

    private var data: List<Folder> = emptyList()

    fun setData(data: List<Folder>) {
        this.data = data
    }

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PhotoViewHolder {
        val binding = ItemFolderBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return PhotoViewHolder(binding, onClickButton)
    }

    override fun getItemCount(): Int {
        return data.size
    }

    override fun onBindViewHolder(holder: PhotoViewHolder, position: Int) {
        val card = data[position]
        holder.bind(card)
    }
}

class PhotoViewHolder(
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
        binding.tvWorkerTwo.text = item.path
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