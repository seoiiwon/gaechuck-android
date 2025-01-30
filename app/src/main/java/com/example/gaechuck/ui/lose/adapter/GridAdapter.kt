package com.example.gaechuck.ui.lose.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.gaechuck.data.model.LoseItem
import com.example.gaechuck.databinding.RowLoseItemBinding
import com.example.gaechuck.ui.lose.adapter.LoseAdapter.OnLoseItemClickListener

class GridAdapter(
    private val items: List<LoseItem>,
    private val listener: OnLoseItemClickListener
) : RecyclerView.Adapter<GridAdapter.GridViewHolder>() {

    inner class GridViewHolder(private val binding: RowLoseItemBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(item: LoseItem) {
            binding.loseImage.setImageResource(item.images[0]) // 이미지 바인딩
            binding.loseName.text = item.name // 이름 바인딩
            binding.loseDate.text = item.date // 날짜 바인딩

            // 클릭 이벤트
            binding.root.setOnClickListener {
                listener.onLoseItemClick(item)
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): GridViewHolder {
        val binding = RowLoseItemBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return GridViewHolder(binding)
    }

    override fun onBindViewHolder(holder: GridViewHolder, position: Int) {
        holder.bind(items[position])
    }

    override fun getItemCount(): Int = items.size
}