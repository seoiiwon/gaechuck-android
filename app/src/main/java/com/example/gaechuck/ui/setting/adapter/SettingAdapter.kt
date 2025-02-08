package com.example.gaechuck.ui.setting.adapter

import android.content.Intent
import android.content.res.ColorStateList
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.appcompat.widget.SwitchCompat
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.example.gaechuck.R
import com.example.gaechuck.data.models.SettingItem
import com.example.gaechuck.ui.termsofuse.TermsOfUseActivity

class SettingAdapter(private val items: List<SettingItem>) :
    RecyclerView.Adapter<SettingAdapter.SettingViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SettingViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.column_setting_item, parent, false)
        return SettingViewHolder(view)
    }

    override fun onBindViewHolder(holder: SettingViewHolder, position: Int) {
        val item = items[position]
        holder.title.text = item.title
        holder.description.text = item.description

        // 두 번째 설정 항목 클릭 리스너 추가
        holder.itemView.setOnClickListener {
            if (position == 1) { // 두 번째 아이템이면
                val intent = Intent(holder.itemView.context, TermsOfUseActivity::class.java)
                holder.itemView.context.startActivity(intent)
            }
        }

        // 기본 설정
        holder.notificationSwitch.trackTintList = ColorStateList.valueOf(ContextCompat.getColor(holder.itemView.context, R.color.gnu_blue))
        // Switch가 필요 없는 항목은 숨기기
        if (item.hasToggle) {
            holder.notificationSwitch.visibility = View.VISIBLE
            holder.notificationSwitch.isChecked = item.isNotificationEnabled

            //
            holder.notificationSwitch.setOnCheckedChangeListener { _, isChecked ->
                item.isNotificationEnabled = isChecked
                if(isChecked) {
                    holder.notificationSwitch.trackTintList = ColorStateList.valueOf(ContextCompat.getColor(holder.itemView.context, R.color.gnu_blue))
                } else {
                    holder.notificationSwitch.trackTintList = ColorStateList.valueOf(ContextCompat.getColor(holder.itemView.context, R.color.grey))

                }
            }
        } else {
            holder.notificationSwitch.visibility = View.GONE
        }
    }

    override fun getItemCount(): Int = items.size

    inner class SettingViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val title: TextView = view.findViewById(R.id.settingTitle)
        val description: TextView = view.findViewById(R.id.settingDescription)
        val notificationSwitch: SwitchCompat = view.findViewById(R.id.notificationSwitch)  // 여기서 Switch를 찾기
    }
}
