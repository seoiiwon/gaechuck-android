package com.gaechuck_package.gaechuck.ui.noticeuniv.adaptor

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.gaechuck_package.gaechuck.R
import com.gaechuck_package.gaechuck.data.model.NoticeUnivModel

class NoticeUnivAdapter(
    private val notices: MutableList<NoticeUnivModel>,
    private val onItemClick: (String) -> Unit
) : RecyclerView.Adapter<NoticeUnivAdapter.NoticeViewHolder>() {

    inner class NoticeViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val category: TextView = view.findViewById(R.id.noticeCategory)
        val title: TextView = view.findViewById(R.id.noticeTitle)
        val body: TextView = view.findViewById(R.id.noticeBody)
        val date: TextView = view.findViewById(R.id.noticeDate)

        fun bind(notice: NoticeUnivModel) {
            val badgeText = notice.categoryName ?: notice.bbsId
            category.text = badgeText.ifBlank { "공지" }

            title.text = notice.title
            body.text = notice.departmentName ?: ""
            date.text = notice.regiDate.replace("-", ".")

            itemView.setOnClickListener { onItemClick(notice.url) }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) =
        NoticeViewHolder(LayoutInflater.from(parent.context).inflate(R.layout.fragment_notice_univ, parent, false))

    override fun onBindViewHolder(holder: NoticeViewHolder, position: Int) =
        holder.bind(notices[position])

    override fun getItemCount(): Int = notices.size

    fun setNotices(newNotices: List<NoticeUnivModel>) {
        notices.clear()
        notices.addAll(newNotices)
        notifyDataSetChanged()
    }

    fun appendNotices(nextPageNotices: List<NoticeUnivModel>) {
        if (nextPageNotices.isEmpty()) return
        val oldSize = notices.size
        notices.addAll(nextPageNotices)
        notifyItemRangeInserted(oldSize, nextPageNotices.size)
    }
}
