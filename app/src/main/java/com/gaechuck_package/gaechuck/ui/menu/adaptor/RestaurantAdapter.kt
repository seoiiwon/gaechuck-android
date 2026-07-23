package com.gaechuck_package.gaechuck.ui.menu.adaptor

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.gaechuck_package.gaechuck.R
import com.gaechuck_package.gaechuck.ui.menu.RestaurantBottomSheetFragment.RestaurantItem

class RestaurantAdapter(
    private val items: List<RestaurantItem>,
    private var selectedSeq: Int,
    private val onSelect: (RestaurantItem) -> Unit
) : RecyclerView.Adapter<RestaurantAdapter.ViewHolder>() {

    inner class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val root: ViewGroup = view.findViewById(R.id.restaurantItem)
        val name: TextView = view.findViewById(R.id.restaurantName)
        val info: TextView = view.findViewById(R.id.restaurantInfo)
        val indicator: View = view.findViewById(R.id.restaurantSelector)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_restaurant, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = items[position]
        val isSelected = item.seq == selectedSeq

        holder.name.text = item.name
        holder.info.text = item.campus

        if (isSelected) {
            holder.root.setBackgroundResource(R.drawable.bg_restaurant_selected)
            holder.indicator.visibility = View.VISIBLE
        } else {
            holder.root.setBackgroundResource(R.drawable.bg_restaurant_unselected)
            holder.indicator.visibility = View.GONE
        }

        holder.root.setOnClickListener {
            val prev = items.indexOfFirst { it.seq == selectedSeq }
            selectedSeq = item.seq
            if (prev >= 0) notifyItemChanged(prev)
            notifyItemChanged(position)
            onSelect(item)
        }
    }

    override fun getItemCount(): Int = items.size
}
