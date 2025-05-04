package com.example.gaechuck.ui.business.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.ImageView
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.gaechuck.R
import com.example.gaechuck.ui.util.ZoomImageDialogFragment

class ImagePagerAdapter(
    private val fragment: Fragment,
    private val images: List<String>
) : RecyclerView.Adapter<ImagePagerAdapter.ImageViewHolder>() {


    inner class ImageViewHolder(private val imageView: ImageView) : RecyclerView.ViewHolder(imageView) {
        fun bind(imageUrl: String) {
            Glide.with(imageView.context)
                .load(imageUrl)  // URL 기반 이미지 로딩
                .into(imageView)

            imageView.setOnClickListener {
                val dialog = ZoomImageDialogFragment(imageUrl)
                dialog.show(fragment.parentFragmentManager, "image_dialog")
            }
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