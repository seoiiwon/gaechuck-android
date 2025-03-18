package com.example.gaechuck.ui.noticeuniv.adaptor

import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.gaechuck.R
import com.example.gaechuck.data.model.NoticeUnivModel
import retrofit2.http.Query

class NoticeUnivAdapter(
    private val noticeUnivModels: MutableList<NoticeUnivModel>,
    private val onItemClick: (String) -> Unit
) : RecyclerView.Adapter<NoticeUnivAdapter.NoticeViewHolder>() {

    private var filteredNotices: List<NoticeUnivModel> = noticeUnivModels.toList()

    class NoticeViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val title: TextView = view.findViewById(R.id.noticeTitle)
        val body: TextView = view.findViewById(R.id.noticeBody)
//        val category: TextView = view.findViewById(R.id.noticeCategory)

        fun bind(notice: NoticeUnivModel, onItemClick: (String) -> Unit) {
            title.text = notice.title
            body.text = notice.departmentName ?: "부서 없음"
//            category.text = notice.bbsId ?: "카테고리 없음"

            itemView.setOnClickListener {
                notice.url?.let { url -> onItemClick(url) }
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): NoticeViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.fragment_notice_univ, parent, false)
        return NoticeViewHolder(view)
    }

    override fun onBindViewHolder(holder: NoticeViewHolder, position: Int) {
        holder.bind(filteredNotices[position], onItemClick)
    }

    override fun getItemCount(): Int = filteredNotices.size

    fun setNotices(newNotices: List<NoticeUnivModel>) {
        noticeUnivModels.clear()
        noticeUnivModels.addAll(newNotices)
        filteredNotices = newNotices.toList()
        notifyDataSetChanged()
    }

    fun getItem(position: Int): NoticeUnivModel? {
        return if (position in noticeUnivModels.indices) noticeUnivModels[position] else null
    }

    fun filter(query: String) {
        filteredNotices = if (query.isEmpty()) {
            noticeUnivModels.toList() // 검색어 없을 경우 전체 리스트 유지
        } else {
            noticeUnivModels.filter {
                it.title.contains(query, ignoreCase = true) // 제목에서만 검색
            }
        }
        Log.d("Search", "Filtered items count: ${filteredNotices.size}") // 로그 추가
        notifyDataSetChanged() // UI 갱신
    }

}