package com.shu.folders.ui.gallery

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.shu.folders.databinding.OnboardingItemContainerBinding
import com.shu.folders.models.MediaStoreImage

class OnboardingItemAdapter(private val onboardingItems: List<MediaStoreImage>) :
    RecyclerView.Adapter<OnboardingItemAdapter.OnboardingItemViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): OnboardingItemViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val binding = OnboardingItemContainerBinding.inflate(inflater, parent, false)

        return OnboardingItemViewHolder(binding)
    }

    override fun onBindViewHolder(holder: OnboardingItemViewHolder, position: Int) {
        holder.bind(onboardingItems[position])
    }

    override fun getItemCount(): Int {
        return onboardingItems.size
    }

    inner class OnboardingItemViewHolder(val binding: OnboardingItemContainerBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(onboardingItem: MediaStoreImage) {
            Glide.with(binding.imageOnboarding)
                .load(onboardingItem.contentUri)
                .thumbnail(0.33f)
                .centerCrop()
                .into(binding.imageOnboarding)

            //binding.imageOnboarding.setImageResource(onboardingItem.image)
        }

    }


}