package com.gaechuck_package.gaechuck.ui.rent.adapter

import android.graphics.Color
import android.text.SpannableString
import android.text.style.ForegroundColorSpan
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.PopupMenu
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.gaechuck_package.gaechuck.R
import com.gaechuck_package.gaechuck.data.response.RentList
import com.gaechuck_package.gaechuck.databinding.RowRentItemBinding

class RentAdapter(private val listener: OnRentItemClickListener, private var isLoggedIn: Boolean = false):
    ListAdapter<RentList, RentAdapter.ViewHolder>(RentItemDiffCallback()) {


    interface OnRentItemClickListener{
        fun OnRentItemClick(item:RentList)
        fun onEditClicked(item: RentList)
        fun onDeleteClicked(item: RentList)
    }

    inner class ViewHolder(private val binding: RowRentItemBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(item: RentList) {
            Glide.with(binding.rentImage.context)
                .load(item.image) // image는 URL 문자열
                .into(binding.rentImage)

            binding.rentName.text = if(item.rentItemName.length > 10) {
                item.rentItemName.substring(0,10) + "..."
            } else {
                item.rentItemName
            }
            binding.rentCount.text = item.rentItemCount.toString() // count 바인딩

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
                        R.id.menu_edit -> {
                            listener.onEditClicked(item)
                            true
                        }
                        R.id.menu_delete -> {
                            listener.onDeleteClicked(item)
                            true
                        }
                        else -> false
                    }
                }

                popupMenu.show()
            }

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

    fun updateItems(newList : List<RentList>) {
        submitList(newList) // 필터링된 리스트를 적용
    }

    // 스크롤 리스너 추가
    fun setOnScrollListener(recyclerView: RecyclerView, onScrolledToEnd: () -> Unit) {
        recyclerView.addOnScrollListener(object : RecyclerView.OnScrollListener() {
            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                super.onScrolled(recyclerView, dx, dy)
                val layoutManager = recyclerView.layoutManager as LinearLayoutManager
                val visibleItemCount = layoutManager.childCount
                val totalItemCount = layoutManager.itemCount
                val pastVisibleItems = layoutManager.findFirstVisibleItemPosition()

                if (!recyclerView.canScrollVertically(1)) { // 리스트의 끝에 도달하면
                    onScrolledToEnd()
                }
            }
        })
    }

    fun updateLoginState(loggedIn: Boolean) {
        this.isLoggedIn = loggedIn
        notifyDataSetChanged()
    }
}