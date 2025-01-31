package com.example.gaechuck.ui.lose.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.gaechuck.data.response.LoseList
import com.example.gaechuck.databinding.RowLoseItemBinding
import com.example.gaechuck.ui.lose.adapter.LoseAdapter.OnLoseItemClickListener

class GridAdapter(
    private val items: List<LoseList>,
    private val listener: OnLoseItemClickListener
) : RecyclerView.Adapter<GridAdapter.GridViewHolder>() {

    inner class GridViewHolder(private val binding: RowLoseItemBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(item: LoseList) {
            // 이미지 바인딩
            // TODO : 이미지 요청 수정
            Glide.with(binding.loseImage.context)
                .load(item.image) // image는 URL 문자열
                .into(binding.loseImage)

            // 이름 바인딩
            binding.loseName.text = item.title
            // 날짜 바인딩
            val dateOnly = item.lostDate.split(" ")[0]
            binding.loseDate.text = dateOnly // 날짜 바인딩

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