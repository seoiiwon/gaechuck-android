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
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.gaechuck_package.gaechuck.R
import com.gaechuck_package.gaechuck.data.response.RentList
import com.gaechuck_package.gaechuck.databinding.RowRentItemBinding

class RentAdapter(private val listener: OnRentItemClickListener, private var isLoggedIn: Boolean = false) :
    ListAdapter<RentList, RentAdapter.ViewHolder>(RentItemDiffCallback()) {

    interface OnRentItemClickListener {
        fun OnRentItemClick(item: RentList)
        fun onEditClicked(item: RentList)
        fun onDeleteClicked(item: RentList)
    }

    inner class ViewHolder(private val binding: RowRentItemBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(item: RentList) {
            Glide.with(binding.rentImage.context)
                .load(item.image)
                .centerCrop()
                .into(binding.rentImage)

            binding.rentName.text = item.rentItemName
            binding.rentCount.text = item.rentItemCount.toString()

            val category = item.rentCategory
            if (!category.isNullOrEmpty()) {
                binding.rentCategory.text = category
                binding.rentCategory.visibility = View.VISIBLE
            } else {
                binding.rentCategory.visibility = View.GONE
            }

            binding.buttonEtc.visibility = if (isLoggedIn) View.VISIBLE else View.GONE

            binding.buttonEtc.setOnClickListener {
                val popupMenu = PopupMenu(binding.buttonEtc.context, binding.buttonEtc, Gravity.END, 0, R.style.PopupMenuStyle)
                popupMenu.menuInflater.inflate(R.menu.etc_menu, popupMenu.menu)

                val deleteMenuItem = popupMenu.menu.findItem(R.id.menu_delete)
                val redTitle = SpannableString(deleteMenuItem.title)
                redTitle.setSpan(ForegroundColorSpan(Color.RED), 0, redTitle.length, 0)
                deleteMenuItem.title = redTitle

                popupMenu.setOnMenuItemClickListener { menuItem ->
                    when (menuItem.itemId) {
                        R.id.menu_edit -> { listener.onEditClicked(item); true }
                        R.id.menu_delete -> { listener.onDeleteClicked(item); true }
                        else -> false
                    }
                }
                popupMenu.show()
            }

            binding.root.setOnClickListener { listener.OnRentItemClick(item) }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = RowRentItemBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    class RentItemDiffCallback : DiffUtil.ItemCallback<RentList>() {
        override fun areItemsTheSame(oldItem: RentList, newItem: RentList) =
            oldItem.rentItemId == newItem.rentItemId

        override fun areContentsTheSame(oldItem: RentList, newItem: RentList) =
            oldItem == newItem
    }

    fun updateItems(newList: List<RentList>) {
        submitList(newList)
    }

    fun updateLoginState(loggedIn: Boolean) {
        this.isLoggedIn = loggedIn
        notifyDataSetChanged()
    }

    fun setOnScrollListener(recyclerView: RecyclerView, onScrolledToEnd: () -> Unit) {
        recyclerView.addOnScrollListener(object : RecyclerView.OnScrollListener() {
            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                super.onScrolled(recyclerView, dx, dy)
                if (!recyclerView.canScrollVertically(1)) {
                    onScrolledToEnd()
                }
            }
        })
    }
}
