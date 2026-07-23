package com.gaechuck_package.gaechuck.ui.club.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.gaechuck_package.gaechuck.databinding.RowClubGalleryItemBinding

class ClubGalleryAdapter(private val images: List<String>) :
    RecyclerView.Adapter<ClubGalleryAdapter.ViewHolder>() {

    inner class ViewHolder(private val binding: RowClubGalleryItemBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(url: String) {
            Glide.with(binding.galleryImage.context)
                .load(url)
                .centerCrop()
                .into(binding.galleryImage)
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = RowClubGalleryItemBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) = holder.bind(images[position])

    override fun getItemCount() = images.size
}
