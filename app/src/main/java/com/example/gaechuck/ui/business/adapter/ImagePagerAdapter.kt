package com.example.gaechuck.ui.business.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.ImageView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.gaechuck.R

class ImagePagerAdapter(private val images: List<String>) : RecyclerView.Adapter<ImagePagerAdapter.ImageViewHolder>() {

    inner class ImageViewHolder(private val imageView: ImageView) : RecyclerView.ViewHolder(imageView) {
        fun bind(imageUrl: String) {
            Glide.with(imageView.context)
                .load(imageUrl)  // URL 기반 이미지 로딩
//                .placeholder(R.drawable.placeholder)  // 로딩 중일 때 보여줄 기본 이미지
//                .error(R.drawable.error_image)  // 에러 발생 시 보여줄 이미지
                .into(imageView)
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ImageViewHolder {
        val imageView = LayoutInflater.from(parent.context)
            .inflate(R.layout.row_item_image_view, parent, false) as ImageView
        return ImageViewHolder(imageView)
    }

    override fun onBindViewHolder(holder: ImageViewHolder, position: Int) {
        holder.bind(images[position])
    }

    override fun getItemCount(): Int = images.size
}