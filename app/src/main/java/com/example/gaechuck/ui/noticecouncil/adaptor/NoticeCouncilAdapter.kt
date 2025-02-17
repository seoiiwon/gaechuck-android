package com.example.gaechuck.ui.noticecouncil.adaptor

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.gaechuck.R
import com.example.gaechuck.data.response.GetCouncilNoticeDataResponse

class NoticeCouncilAdapter(
    private val noticeList: MutableList<GetCouncilNoticeDataResponse>
) : RecyclerView.Adapter<NoticeCouncilAdapter.NoticeCouncilViewHolder>() {

    private var listener: OnItemClickListener? = null

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
        val notice = noticeList[position]

        // ✅ 이미지 처리
        if (!notice.representationImages.isNullOrEmpty()) {
            holder.noticeImage.visibility = View.VISIBLE
            holder.imagePlaceholder.visibility = View.GONE
            Glide.with(holder.itemView.context)
                .load(notice.representationImages)
                .into(holder.noticeImage)
        } else {
            holder.noticeImage.visibility = View.GONE
            holder.imagePlaceholder.visibility = View.VISIBLE
        }

        holder.noticeTitle.text = notice.title
        holder.noticeDescription.text = notice.body
        holder.noticeDate.text = notice.time

        holder.itemView.setOnClickListener {
            listener?.onItemClick(position)
        }
    }

    override fun getItemCount(): Int = noticeList.size

    fun addNotices(newNotices: List<GetCouncilNoticeDataResponse>) {
        val startPosition = noticeList.size
        noticeList.addAll(newNotices)
        notifyItemRangeInserted(startPosition, newNotices.size)
    }

    interface OnItemClickListener {
        fun onItemClick(position: Int)
    }

    fun setOnItemClickListener(listener: OnItemClickListener) {
        this.listener = listener
    }
}