package com.example.gaechuck.noticeuniv.adaptor

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.gaechuck.noticecouncil.viewmodel.NoticeUnivModel
import com.example.gaechuck.R

class NoticeUnivAdapter(private val noticeUnivModels: MutableList<NoticeUnivModel>) :
    RecyclerView.Adapter<NoticeUnivAdapter.NoticeViewHolder>() {

    class NoticeViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val title: TextView = view.findViewById(R.id.noticeTitle)
        val department: TextView = view.findViewById(R.id.noticeDepartment)
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
        holder.department.text = notice.department
        holder.category.text = notice.category
    }

    override fun getItemCount(): Int = noticeUnivModels.size

    fun addNotices(newNoticeUnivModels: List<NoticeUnivModel>) {
        val startPosition = noticeUnivModels.size
        noticeUnivModels.addAll(newNoticeUnivModels)
        notifyItemRangeInserted(startPosition, newNoticeUnivModels.size)
    }
}
