package com.example.gaechuck.ui.noticecouncil.adaptor

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.gaechuck.R
import com.example.gaechuck.data.model.NoticeCouncilModel

class NoticeCouncilAdapter(private val noticeCouncilModels: MutableList<NoticeCouncilModel>) :
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
        val notice = noticeCouncilModels[position]

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

        // 아이템 클릭 리스너 연결
        holder.itemView.setOnClickListener {
            listener?.onItemClick(position)
        }
    }


    override fun getItemCount(): Int = noticeCouncilModels.size

    fun addNotices(newNoticeCouncilModels: List<NoticeCouncilModel>) {
        val startPosition = noticeCouncilModels.size
        noticeCouncilModels.addAll(newNoticeCouncilModels)
        notifyItemRangeInserted(startPosition, newNoticeCouncilModels.size)
    }

    interface OnItemClickListener {
        fun onItemClick(position: Int)
    }

    private var listener: OnItemClickListener? = null

    fun setOnItemClickListener(listener: OnItemClickListener) {
        this.listener = listener
    }
}
