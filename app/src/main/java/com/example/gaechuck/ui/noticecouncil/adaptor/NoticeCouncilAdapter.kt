package com.example.gaechuck.ui.noticecouncil.adaptor

import android.content.Intent
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.gaechuck.R
import com.example.gaechuck.api.AuthManager
import com.example.gaechuck.data.response.GetCouncilNoticeDataResponse
import com.example.gaechuck.ui.noticecouncil.NoticeCouncilActivity
import com.example.gaechuck.ui.noticecouncil.NoticeCouncilUpdateActivity
import java.text.SimpleDateFormat
import java.util.Locale

class NoticeCouncilAdapter(
    private val noticeList: MutableList<GetCouncilNoticeDataResponse>,
    private val onDeleteClick: (Int) -> Unit,
    private val onUpdateClick: (Int) -> Unit
) : RecyclerView.Adapter<NoticeCouncilAdapter.NoticeCouncilViewHolder>() {

    private var listener: OnItemClickListener? = null
    private var filteredList: MutableList<GetCouncilNoticeDataResponse> = noticeList.toMutableList()

    class NoticeCouncilViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val noticeImage: ImageView = view.findViewById(R.id.noticeImage)
        val imagePlaceholder: TextView = view.findViewById(R.id.imagePlaceholder)
        val noticeTitle: TextView = view.findViewById(R.id.noticeTitle)
        val noticeDescription: TextView = view.findViewById(R.id.noticeDescription)
        val noticeDate: TextView = view.findViewById(R.id.noticeDate)
        val deleteButton: Button = view.findViewById(R.id.notice_delete_button)
        val updateButton: Button = view.findViewById(R.id.notice_update_button)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): NoticeCouncilViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.fragment_notice_council, parent, false)
        return NoticeCouncilViewHolder(view)
    }

    override fun onBindViewHolder(holder: NoticeCouncilViewHolder, position: Int) {
        val notice = filteredList[position]
        Log.d("Adapter", "Binding notice: ${notice.title} at position: $position")

        val token = AuthManager.getToken()

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

        if (token.isNullOrEmpty()) {
            holder.updateButton.visibility = View.GONE
            holder.deleteButton.visibility = View.GONE
        } else {
            holder.updateButton.visibility = View.VISIBLE
            holder.deleteButton.visibility = View.VISIBLE
        }

        holder.noticeTitle.text = notice.title
        holder.noticeDescription.text = notice.body
        holder.noticeDate.text = formatNoticeDate(notice.time)

        holder.deleteButton.setOnClickListener {
            onDeleteClick(notice.id)
        }

        holder.updateButton.setOnClickListener {
            val context = holder.itemView.context
            if (context is NoticeCouncilActivity) {
                val intent = Intent(context, NoticeCouncilUpdateActivity::class.java)
                intent.putExtra("notice_id", notice.id)
                context.updateNoticeLauncher.launch(intent)
            } else {
                // 만약 context 캐스팅이 불가능하면 일반 startActivity() 사용
                val intent = Intent(context, NoticeCouncilUpdateActivity::class.java)
                intent.putExtra("notice_id", notice.id)
                context.startActivity(intent)
            }
        }

        holder.itemView.setOnClickListener {
            listener?.onItemClick(notice.id)
        }
    }

    override fun getItemCount(): Int = filteredList.size

    fun removeNotice(noticeId: Int) {
        val position = noticeList.indexOfFirst { it.id == noticeId }
        if (position != -1) {
            noticeList.removeAt(position)
            notifyItemRemoved(position)
            notifyItemRangeChanged(position, noticeList.size)
        }
    }


    fun updateData(newList: List<GetCouncilNoticeDataResponse>) {
        noticeList.clear()
        noticeList.addAll(newList)
        filteredList = noticeList.toMutableList()
        notifyDataSetChanged()
    }

    fun setOnItemClickListener(listener: OnItemClickListener) {
        this.listener = listener
    }

    fun filter(query: String) {
        filteredList = if (query.isEmpty()) {
            noticeList.toMutableList()
        } else {
            noticeList.filter { it.title.contains(query, ignoreCase = true) }.toMutableList()
        }
        Log.d("Search", "Filtered items count: ${filteredList.size}")
        notifyDataSetChanged()
    }


    private fun formatNoticeDate(inputDate: String): String {
        return try {
            val inputFormat = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault())
            val outputFormat = SimpleDateFormat("yyyy.MM.dd", Locale.getDefault())
            val date = inputFormat.parse(inputDate)
            outputFormat.format(date)
        } catch (e: Exception) {
            inputDate
        }
    }


    interface OnItemClickListener {
        fun onItemClick(position: Int)
    }
}