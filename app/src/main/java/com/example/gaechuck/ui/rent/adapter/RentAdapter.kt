package com.example.gaechuck.ui.rent.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.gaechuck.data.model.RentItem
import com.example.gaechuck.databinding.RowRentItemBinding

class RentAdapter(private val listener: OnRentItemClickListener):
    ListAdapter<RentItem, RentAdapter.ViewHolder>(RentItemDiffCallback()) {

    interface OnRentItemClickListener{
            fun OnRentItemClick(item:RentItem)
        }
    inner class ViewHolder(private val binding: RowRentItemBinding) : RecyclerView.ViewHolder(binding.root){
        fun bind(item:RentItem){
            binding.rentImage.setImageResource(item.images[0])
            binding.rentName?.text = item.name
            binding.rentCount?.text = item.count.toString()

            // Item 클릭 이벤트
            binding.root.setOnClickListener{
                listener.OnRentItemClick(item)
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = RowRentItemBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return  ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(getItem(position))
    }
    // DiffUtil을 위한 콜백 클래스
    class RentItemDiffCallback : DiffUtil.ItemCallback<RentItem>() {
        override fun areItemsTheSame(oldItem: RentItem, newItem: RentItem): Boolean {
            // 동일한 아이템인지 비교 (ID나 다른 고유값을 기준으로 비교)
            return oldItem.name == newItem.name // 예시로 이름을 비교 (아이템의 고유 ID가 있다면 그걸 기준으로 비교하는 것이 더 안전)
        }

        override fun areContentsTheSame(oldItem: RentItem, newItem: RentItem): Boolean {
            // 아이템 내용이 동일한지 비교
            return oldItem == newItem
        }
    }
}