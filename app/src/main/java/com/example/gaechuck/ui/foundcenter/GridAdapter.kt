package com.example.gaechuck.ui.foundcenter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.TextView
import com.example.gaechuck.R

class GridAdapter(private val context: Context, private val items: List<GridItem>) : BaseAdapter() {

    override fun getCount(): Int = items.size

    override fun getItem(position: Int): Any = items[position]

    override fun getItemId(position: Int): Long = position.toLong()

    override fun getView(position: Int, convertView: View?, parent: ViewGroup?): View {
        val view: View = convertView ?: LayoutInflater.from(context).inflate(R.layout.grid_item, parent, false)

        // 현재 아이템 가져오기
        val item = items[position]

        // View 설정
        val titleTextView: TextView = view.findViewById(R.id.itemTitle)
        val dateTextView: TextView = view.findViewById(R.id.itemDate)

        titleTextView.text = item.title
        dateTextView.text = item.date

        return view
    }
}
