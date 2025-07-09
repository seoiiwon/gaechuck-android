package com.gaechuck_package.gaechuck.ui.noticecouncil.adaptor

import android.net.Uri
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.gaechuck_package.gaechuck.R

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
            .load(imageUri.toString())
            .into(holder.imageView)

        holder.deleteButton.setOnClickListener {
            val position = holder.adapterPosition
            if (position != RecyclerView.NO_POSITION && position < imageList.size) {
                imageList.removeAt(position)
                notifyItemRemoved(position)
                // 필요 시, notifyDataSetChanged()로 전체 갱신 (단, 성능에 영향을 줄 수 있음)
            }
        }
    }

    override fun getItemCount(): Int = imageList.size

    fun onItemMove(fromPosition: Int, toPosition: Int) {
        if (fromPosition < toPosition) {
            for (i in fromPosition until toPosition) {
                imageList[i] = imageList.set(i + 1, imageList[i])
            }
        } else {
            for (i in fromPosition downTo toPosition + 1) {
                imageList[i] = imageList.set(i - 1, imageList[i])
            }
        }
        notifyItemMoved(fromPosition, toPosition)
    }
}