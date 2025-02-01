package com.example.gaechuck.ui.rent.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.gaechuck.data.response.RentList
import com.example.gaechuck.databinding.RowRentItemBinding

class RentAdapter(private val listener: OnRentItemClickListener):
    ListAdapter<RentList, RentAdapter.ViewHolder>(RentItemDiffCallback()) {

    interface OnRentItemClickListener{
            fun OnRentItemClick(item:RentList)
        }
    inner class ViewHolder(private val binding: RowRentItemBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(item: RentList) {
            // TODO : 이미지 요청 수정
            Glide.with(binding.rentImage.context)
                .load(item.rentItemImage) // image는 URL 문자열
                .into(binding.rentImage)
            binding.rentName.text = item.rentItemName // name 바인딩
            binding.rentCount.text = item.rentItemCount.toString() // count 바인딩

            // Item 클릭 이벤트
            binding.root.setOnClickListener {
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
    class RentItemDiffCallback : DiffUtil.ItemCallback<RentList>() {
        override fun areItemsTheSame(oldItem: RentList, newItem: RentList): Boolean {
            // 동일한 아이템인지 비교 (ID나 다른 고유값을 기준으로 비교)
            return oldItem.rentItemId == newItem.rentItemId // 예시로 이름을 비교 (아이템의 고유 ID가 있다면 그걸 기준으로 비교하는 것이 더 안전)
        }

        override fun areContentsTheSame(oldItem: RentList, newItem: RentList): Boolean {
            // 아이템 내용이 동일한지 비교
            return oldItem.rentItemId == newItem.rentItemId
        }
    }
}