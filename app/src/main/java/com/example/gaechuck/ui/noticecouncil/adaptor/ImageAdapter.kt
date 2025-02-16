package com.example.gaechuck.ui.noticecouncil.adaptor

import android.net.Uri
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.gaechuck.R

class ImageAdapter(
    private val imageList: MutableList<Uri>,
    private val onDeleteClick: (Int) -> Unit
) : RecyclerView.Adapter<ImageAdapter.ImageViewHolder>() {
    class ImageViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val imageView: ImageView = view.findViewById(R.id.imageView)
        val deleteButton: ImageView = view.findViewById(R.id.deleteButton)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ImageViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.fragment_selected_image, parent, false)
        return ImageViewHolder(view)
    }

    override fun onBindViewHolder(holder: ImageViewHolder, position: Int) {
        val imageUri = imageList[position]

        Glide.with(holder.itemView.context)
            .load(imageUri)
            .into(holder.imageView)

        holder.deleteButton.setOnClickListener {
            onDeleteClick(position)
        }
    }

    override fun getItemCount(): Int = imageList.size
}