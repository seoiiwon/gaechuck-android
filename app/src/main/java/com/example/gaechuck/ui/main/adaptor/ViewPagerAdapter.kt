package com.example.gaechuck.ui.main.adaptor

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.GridLayout
import androidx.recyclerview.widget.RecyclerView
import com.example.gaechuck.R

class ViewPagerAdapter(
    private val layouts: List<Int>,
    private val onGridClickSetup: (GridLayout, Int) -> Unit
) : RecyclerView.Adapter<ViewPagerAdapter.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(viewType, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val gridLayout = holder.itemView.findViewById<GridLayout>(R.id.gridLayout)
        gridLayout?.let {
            onGridClickSetup(it, position)
        }
    }

    override fun getItemCount(): Int = layouts.size

    override fun getItemViewType(position: Int): Int = layouts[position]

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView)
}
