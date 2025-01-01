package com.example.gaechuck.ui.business.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.gaechuck.data.model.BusinessItem
import com.example.gaechuck.databinding.RowBusinessItemBinding

class BusinessAdapter(private val data: List<BusinessItem>) : RecyclerView.Adapter<BusinessAdapter.ViewHolder>() {

    inner class ViewHolder(private val binding: RowBusinessItemBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(item: BusinessItem) {
            binding.businessImage.setImageResource(item.images[0]) // 대표 이미지 설정
            binding.businessInfo.text = item.info
            binding.businessCategory.text = item.category
            binding.businessName.text = item.name
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = RowBusinessItemBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(binding)
    }

    override fun getItemCount(): Int = data.size

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(data[position])
    }
}
