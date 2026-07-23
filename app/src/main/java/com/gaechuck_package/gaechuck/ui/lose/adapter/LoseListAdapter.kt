package com.gaechuck_package.gaechuck.ui.lose.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.gaechuck_package.gaechuck.data.response.LoseList
import com.gaechuck_package.gaechuck.databinding.RowLoseItemBinding

class LoseListAdapter(
    private var items: List<LoseList>,
    private val onItemClick: (LoseList) -> Unit
) : RecyclerView.Adapter<LoseListAdapter.ViewHolder>() {

    inner class ViewHolder(private val binding: RowLoseItemBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(item: LoseList) {
            binding.loseName.text = item.title
            binding.loseLocation.text = item.description

            if (!item.image.isNullOrEmpty()) {
                Glide.with(binding.loseImage.context)
                    .load(item.image)
                    .centerCrop()
                    .transform(RoundedCorners(48))
                    .into(binding.loseImage)
            } else {
                Glide.with(binding.loseImage.context).clear(binding.loseImage)
            }

            binding.root.setOnClickListener { onItemClick(item) }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = RowLoseItemBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) = holder.bind(items[position])

    override fun getItemCount() = items.size

    fun updateItems(newItems: List<LoseList>) {
        items = newItems
        notifyDataSetChanged()
    }
}
