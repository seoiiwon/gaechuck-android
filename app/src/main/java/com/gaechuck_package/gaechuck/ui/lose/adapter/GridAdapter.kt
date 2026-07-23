package com.gaechuck_package.gaechuck.ui.lose.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.gaechuck_package.gaechuck.data.response.LoseList
import com.gaechuck_package.gaechuck.databinding.RowLoseItemBinding
import com.gaechuck_package.gaechuck.ui.lose.adapter.LoseAdapter.OnLoseItemClickListener

class GridAdapter(
    private var items: List<LoseList>,
    private val listener: OnLoseItemClickListener
) : RecyclerView.Adapter<GridAdapter.GridViewHolder>() {

    inner class GridViewHolder(private val binding: RowLoseItemBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(item: LoseList) {
            Glide.with(binding.loseImage.context)
                .load(item.image)
                .centerCrop()
                .transform(RoundedCorners(48))
                .into(binding.loseImage)

            binding.loseName.text = item.title
            binding.loseLocation.text = item.description

            binding.root.setOnClickListener { listener.onLoseItemClick(item) }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): GridViewHolder {
        val binding = RowLoseItemBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return GridViewHolder(binding)
    }

    override fun onBindViewHolder(holder: GridViewHolder, position: Int) = holder.bind(items[position])

    override fun getItemCount(): Int = items.size

    fun updateItems(newItems: List<LoseList>) {
        items = newItems
        notifyDataSetChanged()
    }
}
