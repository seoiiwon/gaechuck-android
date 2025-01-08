package com.example.gaechuck.noticecouncil.adaptor

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.gaechuck.noticecouncil.viewmodel.Notice
import com.example.gaechuck.R

class NoticeCouncilAdapter(private val notices: MutableList<Notice>) :
    RecyclerView.Adapter<NoticeCouncilAdapter.NoticeCouncilViewHolder>() {

    class NoticeCouncilViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val noticeImage: ImageView = view.findViewById(R.id.noticeImage)
        val imagePlaceholder: TextView = view.findViewById(R.id.imagePlaceholder)
        val noticeTitle: TextView = view.findViewById(R.id.noticeTitle)
        val noticeDescription: TextView = view.findViewById(R.id.noticeDescription)
        val noticeDate: TextView = view.findViewById(R.id.noticeDate)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): NoticeCouncilViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.fragment_notice_council, parent, false)
        return NoticeCouncilViewHolder(view)
    }

    override fun onBindViewHolder(holder: NoticeCouncilViewHolder, position: Int) {
        val notice = notices[position]

        // 이미지 처리
        if (notice.image == null) {
            holder.noticeImage.visibility = View.GONE
            holder.imagePlaceholder.visibility = View.VISIBLE
        } else {
            holder.noticeImage.visibility = View.VISIBLE
            holder.imagePlaceholder.visibility = View.GONE
            Glide.with(holder.itemView.context)
                .load(notice.image)
                .into(holder.noticeImage)
        }

        // 텍스트 처리
        holder.noticeTitle.text = notice.title
        holder.noticeDescription.text = notice.body
        holder.noticeDate.text = notice.date
    }

    override fun getItemCount(): Int = notices.size

    fun addNotices(newNotices: List<Notice>) {
        val startPosition = notices.size
        notices.addAll(newNotices)
        notifyItemRangeInserted(startPosition, newNotices.size)
    }
}
