package com.example.gaechuck.ui.business.adapter

import android.graphics.Color
import android.text.SpannableString
import android.text.style.ForegroundColorSpan
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.PopupMenu
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.gaechuck.R
import com.example.gaechuck.data.response.BusinessList
import com.example.gaechuck.databinding.RowBusinessItemBinding
import okio.utf8Size

class BusinessAdapter(private val data: MutableList<BusinessList>,
                      private val listener: OnBusinessItemClickListener,
                      private var isLoggedIn: Boolean = false) : RecyclerView.Adapter<BusinessAdapter.ViewHolder>() {

    interface OnBusinessItemClickListener {
        fun onBusinessItemClick(item: BusinessList)
        fun onEditClicked(item: BusinessList)
        fun onDeleteClicked(item: BusinessList)
    }

    inner class ViewHolder(private val binding: RowBusinessItemBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(item: BusinessList) {
            Glide.with(binding.businessImage.context)
                .load(item.image) // image는 URL 문자열
                .into(binding.businessImage) // 대표 이미지 설정

            binding.businessName.text = if(item.coalitionName.length > 12) {
                item.coalitionName.substring(0,12) + "..."
            } else {
                item.coalitionName
            }
            binding.businessInfo.text = if (item.benefit.length > 20) {
                item.benefit.substring(0, 20) + "..."
            } else {
                item.benefit
            }

            binding.businessCategory.text = item.category
            binding.buttonEtc.visibility = if (isLoggedIn) View.VISIBLE else View.GONE


            binding.buttonEtc.setOnClickListener {
                val popupMenu = PopupMenu(binding.buttonEtc.context ,  binding.buttonEtc, Gravity.END , 0 , R.style.PopupMenuStyle)
                popupMenu.menuInflater.inflate(R.menu.etc_menu, popupMenu.menu)

                // 삭제하기 항목만 색상 변경
                val deleteMenuItem = popupMenu.menu.findItem(R.id.menu_delete)
                val redTitle = SpannableString(deleteMenuItem.title)
                redTitle.setSpan(ForegroundColorSpan(Color.RED), 0, redTitle.length, 0)
                deleteMenuItem.title = redTitle

                popupMenu.setOnMenuItemClickListener { menuItem ->
                    when (menuItem.itemId) {
                        com.example.gaechuck.R.id.menu_edit -> {
                            listener.onEditClicked(item)
                            true
                        }
                        com.example.gaechuck.R.id.menu_delete -> {
                            listener.onDeleteClicked(item)
                            true
                        }
                        else -> false
                    }
                }

                popupMenu.show()
            }

            // Item 클릭 이벤트 추가
            binding.root.setOnClickListener {
                listener.onBusinessItemClick(item)  // 클릭된 아이템 전달
            }
        }

    }

    // 아이템 업데이트 메소드 추가
    fun updateItems(newItems: List<BusinessList>) {
        data.clear()
        data.addAll(newItems)
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = RowBusinessItemBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(binding)
    }

    override fun getItemCount(): Int = data.size

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(data[position])
    }

    fun updateLoginState(loggedIn: Boolean) {
        this.isLoggedIn = loggedIn
        notifyDataSetChanged()
    }
}
