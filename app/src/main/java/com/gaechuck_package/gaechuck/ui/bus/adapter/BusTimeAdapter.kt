package com.gaechuck_package.gaechuck.ui.bus.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.gaechuck_package.gaechuck.R
import com.gaechuck_package.gaechuck.ui.bus.viewmodel.BusTimeEntry

class BusTimeAdapter(private var items: List<BusTimeEntry>) :
    RecyclerView.Adapter<BusTimeAdapter.ViewHolder>() {

    inner class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val departureTime: TextView = view.findViewById(R.id.departureTime)
        val fridayBadge: TextView = view.findViewById(R.id.fridayCancelledBadge)
        val arrivalTime: TextView = view.findViewById(R.id.arrivalTime)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_bus_time, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val entry = items[position]
        holder.departureTime.text = entry.departureTime
        holder.arrivalTime.text = entry.arrivalTime
        holder.fridayBadge.visibility = if (entry.isFridayCancelled) View.VISIBLE else View.GONE
    }

    override fun getItemCount() = items.size

    fun updateItems(newItems: List<BusTimeEntry>) {
        items = newItems
        notifyDataSetChanged()
    }
}
