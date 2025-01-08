package com.example.gaechuck.main.adaptor

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.GridLayout
import androidx.recyclerview.widget.RecyclerView
import com.example.gaechuck.noticeuniv.viewmodel.GridClickListener
import com.example.gaechuck.R

class ViewPagerAdapter(
    private val listener: GridClickListener,
    private val layouts: List<Int>
) : RecyclerView.Adapter<ViewPagerAdapter.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(viewType, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val gridLayout = holder.itemView.findViewById<GridLayout>(R.id.gridLayout)
        gridLayout?.let {
            listener.onGridClick(it, position) // GridClickListener 호출
        }
    }

    override fun getItemCount(): Int = layouts.size

    override fun getItemViewType(position: Int): Int = layouts[position]

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView)
}
