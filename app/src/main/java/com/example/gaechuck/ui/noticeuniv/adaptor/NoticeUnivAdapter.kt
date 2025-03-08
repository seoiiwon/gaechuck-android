package com.example.gaechuck.ui.noticeuniv.adaptor

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.gaechuck.R
import com.example.gaechuck.data.model.NoticeUnivModel

class NoticeUnivAdapter(private val noticeUnivModels: MutableList<NoticeUnivModel>) :
    RecyclerView.Adapter<NoticeUnivAdapter.NoticeViewHolder>() {

    class NoticeViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val title: TextView = view.findViewById(R.id.noticeTitle)
        val body: TextView = view.findViewById(R.id.noticeBody)
        val category: TextView = view.findViewById(R.id.noticeCategory)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): NoticeViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.fragment_notice_univ, parent, false)
        return NoticeViewHolder(view)
    }

    override fun onBindViewHolder(holder: NoticeViewHolder, position: Int) {
        val notice = noticeUnivModels[position]
        holder.title.text = notice.title
        holder.body.text = notice.departmentName ?: "부서 없음"
        holder.category.text = notice.bbsId ?: "카테고리 없음"
    }

    override fun getItemCount(): Int = noticeUnivModels.size

    fun setNotices(newNotices: List<NoticeUnivModel>) {
        noticeUnivModels.clear()
        noticeUnivModels.addAll(newNotices)
        notifyDataSetChanged()
    }

    fun getItem(position: Int): NoticeUnivModel? {
        return if (position in noticeUnivModels.indices) noticeUnivModels[position] else null
    }
}