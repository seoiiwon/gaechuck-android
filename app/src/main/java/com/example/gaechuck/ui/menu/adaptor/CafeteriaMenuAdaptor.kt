package com.example.gaechuck.ui.menu.adaptor

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.example.gaechuck.R

class CafeteriaMenuAdaptor(
    private val context: Context,
    private val days: List<String>,
    private val selectedDay: String,
    private val onDaySelected: (String) -> Unit
) : RecyclerView.Adapter<CafeteriaMenuAdaptor.DayViewHolder>() {

    private var selectedPosition = days.indexOf(selectedDay)

    inner class DayViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val dayButton: Button = view.findViewById(R.id.dayButton)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): DayViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_day_button, parent, false)
        return DayViewHolder(view)
    }

    override fun onBindViewHolder(holder: DayViewHolder, position: Int) {
        val day = days[position]
        holder.dayButton.text = day.first().toString()

        if (position == selectedPosition) {
            holder.dayButton.setBackgroundColor(ContextCompat.getColor(context, R.color.gnu_blue))
        } else {
            holder.dayButton.setBackgroundColor(ContextCompat.getColor(context, R.color.default_tab_color))
        }

        holder.dayButton.setOnClickListener {
            val previousPosition = selectedPosition
            selectedPosition = position
            notifyItemChanged(previousPosition)
            notifyItemChanged(selectedPosition)
            onDaySelected(day)
        }
    }

    override fun getItemCount(): Int = days.size

}