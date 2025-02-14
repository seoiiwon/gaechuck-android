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
        holder.department.text = notice.body  // 공지 내용 표시
        holder.category.text = getCategoryByBbsId(notice.id)  // bbsId에 맞는 카테고리 표시
    }

    private fun getCategoryByBbsId(id: Int): String {
        return when (id) {
            1 -> "학사"
            2 -> "기관"
            3 -> "채용"
            4 -> "장학"
            5 -> "입법예고"
            else -> "기타"
        }
    }


//    override fun onBindViewHolder(holder: NoticeViewHolder, position: Int) {
//        val notice = noticeUnivModels[position]
//        holder.title.text = notice.title
//        holder.department.text = notice.department
//        holder.category.text = notice.category
//
//        // 아이템 클릭 리스너 설정
//        holder.itemView.setOnClickListener {
//            val context = holder.itemView.context
//            val intent = Intent(context, NoticeUnivDetailActivity::class.java)
//            intent.putExtra("notice_details", notice)  // 공지사항 객체를 전달
//            context.startActivity(intent)  // 상세 페이지로 이동
//        }
//    }

    override fun getItemCount(): Int = noticeUnivModels.size

    fun addNotices(newNoticeUnivModels: List<NoticeUnivModel>) {
        val startPosition = noticeUnivModels.size
        noticeUnivModels.addAll(newNoticeUnivModels)
        notifyItemRangeInserted(startPosition, newNoticeUnivModels.size)
    }
}
