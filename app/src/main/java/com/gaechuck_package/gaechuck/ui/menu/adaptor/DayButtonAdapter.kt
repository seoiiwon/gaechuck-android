package com.gaechuck_package.gaechuck.ui.menu.adaptor

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.gaechuck_package.gaechuck.R

class DayButtonAdapter(
    private val context: Context,
    private val days: List<String>,
    private val dates: List<String>,
    private var selectedIndex: Int,
    private val onDaySelected: (index: Int, date: String) -> Unit
) : RecyclerView.Adapter<DayButtonAdapter.DayViewHolder>() {

    inner class DayViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val root: LinearLayout = view.findViewById(R.id.dayButtonRoot)
        val dayLabel: TextView = view.findViewById(R.id.dayLabel)
        val dayNumber: TextView = view.findViewById(R.id.dayNumber)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): DayViewHolder {
        val view = LayoutInflater.from(context).inflate(R.layout.item_day_button, parent, false)
        return DayViewHolder(view)
    }

    override fun onBindViewHolder(holder: DayViewHolder, position: Int) {
        val day = days[position]
        val dateNum = dates.getOrNull(position)?.split("-")?.last()?.trimStart('0')?.ifEmpty { "0" } ?: ""

        holder.dayLabel.text = day
        holder.dayNumber.text = dateNum

        val isSelected = position == selectedIndex
        applyState(holder, isSelected)

        holder.root.setOnClickListener {
            val prev = selectedIndex
            selectedIndex = position
            notifyItemChanged(prev)
            notifyItemChanged(position)
            onDaySelected(position, dates.getOrElse(position) { "" })
        }
    }

    private fun applyState(holder: DayViewHolder, selected: Boolean) {
        if (selected) {
            holder.root.setBackgroundResource(R.drawable.bg_day_selected)
            holder.dayLabel.setTextColor(ContextCompat.getColor(context, R.color.white))
            holder.dayNumber.setTextColor(ContextCompat.getColor(context, R.color.white))
        } else {
            holder.root.setBackgroundResource(R.drawable.bg_day_unselected)
            holder.dayLabel.setTextColor(ContextCompat.getColor(context, R.color.text_secondary))
            holder.dayNumber.setTextColor(ContextCompat.getColor(context, R.color.text_primary))
        }
    }

    override fun getItemCount(): Int = days.size
}