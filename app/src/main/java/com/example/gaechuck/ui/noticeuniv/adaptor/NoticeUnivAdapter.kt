package com.example.gaechuck.ui.noticeuniv.adaptor

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.gaechuck.R
import com.example.gaechuck.data.model.NoticeUnivModel

class NoticeUnivAdapter(
    private val notices: MutableList<NoticeUnivModel>,
    private val onItemClick: (String) -> Unit
) : RecyclerView.Adapter<NoticeUnivAdapter.NoticeViewHolder>() {

    inner class NoticeViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        private val title: TextView = view.findViewById(R.id.noticeTitle)
        private val body: TextView = view.findViewById(R.id.noticeBody)
        private val date: TextView = view.findViewById(R.id.noticeDate)
        private val category: TextView = view.findViewById(R.id.noticeCategory)

        fun bind(notice: NoticeUnivModel) {
            val noticeDept = notice.departmentName ?: ""
            val noticeTitle = notice.title

            title.text = "[$noticeDept] $noticeTitle"
            body.text = notice.departmentName ?: "부서 없음"
            date.text = notice.regiDate.replace("-", ".")
            category.text = notice.bbsId ?: "카테고리 없음"

            itemView.setOnClickListener {
                onItemClick(notice.url)
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): NoticeViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.fragment_notice_univ, parent, false)
        return NoticeViewHolder(view)
    }

    override fun onBindViewHolder(holder: NoticeViewHolder, position: Int) {
        holder.bind(notices[position])
    }

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

    fun getItem(position: Int): NoticeUnivModel? {
        return if (position in notices.indices) notices[position] else null
    }
}